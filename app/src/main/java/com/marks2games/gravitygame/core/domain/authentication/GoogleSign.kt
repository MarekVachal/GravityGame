package com.marks2games.gravitygame.core.domain.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.core.data.SharedPreferencesRepository
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleSign @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferencesRepository
) {

    private val webClientId = context.getString(R.string.default_web_client_id)
    private val credentialManager = CredentialManager.create(context)

    suspend fun logout(){
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        sharedPreferences.setHasSignIn(false)
    }

    @SuppressLint("CredentialManagerSignInWithGoogle")
    suspend fun signInWithCredentialManager() {
        try {
            val token = getGoogleIdToken() ?: throw Exception("Google ID token is null")
            authenticateWithFirebase(token)
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    suspend fun reauthenticateAndDeleteUser() {
        try {
            val googleIdToken = getGoogleIdToken() ?: throw Exception("Google ID token is null")
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.currentUser?.reauthenticate(credential)
                ?.addOnSuccessListener {
                    deleteUser()
                }
                ?.addOnFailureListener { e ->
                    Sentry.captureException(e)
                }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun deleteUser() {
        auth.currentUser?.delete()
            ?.addOnSuccessListener {
                sharedPreferences.setHasSignIn(false)
            }
            ?.addOnFailureListener { e ->
                Sentry.captureException(e)
            }
    }

    suspend fun linkGuestAccountWithGoogle(onUserUpdated:()-> Unit) {
        try {
            withContext(Dispatchers.IO) {
                val token = getGoogleIdToken() ?: throw Exception("Google ID token is null")
                val credential = GoogleAuthProvider.getCredential(token, null)

                auth.currentUser?.linkWithCredential(credential)
                    ?.addOnSuccessListener { task ->
                        val user = task.user
                        if (user != null) {
                            onUserUpdated()
                        }
                    }
                    ?.addOnFailureListener { error ->
                        if (error is FirebaseAuthUserCollisionException) {
                            authenticateWithFirebase(token)
                        } else {
                            Sentry.captureException(error)
                        }

                    }
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    suspend fun getGoogleIdToken(): String? {
        val nonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(nonce)

        // First attempt with filtering by authorized accounts
        return tryGetGoogleIdToken(hashedNonce, true) ?: tryGetGoogleIdToken(hashedNonce, false)
    }

    private suspend fun tryGetGoogleIdToken(
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): String? {
        return try {
            val googleIdOption = buildGoogleIdOption(hashedNonce, filterByAuthorizedAccounts)
            val request = buildGetCredentialRequest(googleIdOption)
            val result = getCredential(request)

            val credential = result?.credential ?: return null
            extractIdToken(credential)
        } catch (e: GetCredentialException) {
            handleCredentialException(e)
            null
        } catch(e: GetPublicKeyCredentialException){
            handleCredentialException(e)
            null
        }
    }

    private fun extractIdToken(credential: androidx.credentials.Credential): String? {
        return if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            googleIdTokenCredential.idToken
        } else {
            Log.e("GoogleSign", "Unexpected type of credential: ${credential.type}")
            null
        }
    }

    private fun buildGoogleIdOption(
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(webClientId)
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun buildGetCredentialRequest(credentialOption: GetGoogleIdOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(credentialOption)
            .build()
    }

    private suspend fun getCredential(request: GetCredentialRequest): GetCredentialResponse? {
        return runCatching {
            credentialManager.getCredential(
                request = request,
                context = context
            )
        }.onFailure { e ->
            if (e is NoCredentialException) {
                Sentry.captureException(e)
            }
        }.getOrNull()
    }

    private fun handleCredentialException(e: Exception) {
        Sentry.captureException(e)
        Log.e("GoogleSign", "Error getting credential", e)
    }

    private fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                sharedPreferences.setHasSignIn(true)
            }
            .addOnFailureListener { e ->
                Sentry.captureException(e)
                CoroutineScope(Dispatchers.IO).launch{
                    AnonymousSign(auth).signInAnonymously()
                }

            }
    }

    private fun hashNonce(rawNonce: String): String {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}

