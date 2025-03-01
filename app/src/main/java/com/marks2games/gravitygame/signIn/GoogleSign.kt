package com.marks2games.gravitygame.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.models.SharedPreferencesRepository
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
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
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuth", "Reauthentication successful")
                        deleteUser()
                    } else {
                        Log.e("FirebaseAuth", "Reauthentication failed", task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.e("GoogleSign", "Reauthentication failed: ${e.message}")
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

    suspend fun linkGuestAccountWithGoogle(updateUserName:()-> Unit, updateUserEmail:() -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val token = getGoogleIdToken()
                val credential = GoogleAuthProvider.getCredential(token, null)

                withContext(Dispatchers.Main) {
                    auth.currentUser?.linkWithCredential(credential)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = task.result?.user
                                if (user != null) {
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.providerData.find { it.providerId == "google.com" }?.displayName)
                                        .setPhotoUri(user.providerData.find { it.providerId == "google.com" }?.photoUrl)
                                        .build()

                                    user.updateProfile(profileUpdates)
                                        .addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                sharedPreferences.setHasSignIn(true)
                                                updateUserEmail()
                                                updateUserName()
                                            } else {
                                                Log.e("FirebaseAuth", "Updating profile failed", updateTask.exception)
                                            }
                                        }
                                }
                            } else {
                                Log.e("FirebaseAuth", "Linking guest account failed", task.exception)
                            }
                        }
                }
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }
    }

    private suspend fun getGoogleIdToken(): String?{
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(rawNonce)
        var token: String? = null

        withContext(Dispatchers.IO){
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(webClientId)
                    .setNonce(hashedNonce)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = runCatching{
                    credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                }.onFailure { e ->
                    if(e is NoCredentialException){
                        AnonymousSign(auth).signInAnonymously()
                    } else {
                        Sentry.captureException(e)
                    }
                }.getOrNull()
                val credential = result?.credential ?: return@withContext

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    token = googleIdTokenCredential.idToken
                } else {
                    Log.e("GoogleSign", "Unexpected type of credential")
                    token = null
                }

            } catch (e: GetCredentialException) {
                Sentry.captureException(e)
                try {
                    val newGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setNonce(hashedNonce)
                        .build()

                    val newRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(newGoogleIdOption)
                        .build()

                    val newResult = runCatching {
                        credentialManager.getCredential(
                            context = context,
                            request = newRequest
                        )
                    }.onFailure { error ->
                        if(error is NoCredentialException){
                            AnonymousSign(auth).signInAnonymously()
                        } else {
                            Sentry.captureException(error)
                        }
                    }.getOrNull()
                    val newCredential = newResult?.credential ?: return@withContext

                    if (newCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(newCredential.data)
                        token = googleIdTokenCredential.idToken
                    } else {
                        Log.e("GoogleSign", "Unexpected type of credential")
                        token = null
                    }

                } catch (signupException: GetCredentialException) {
                    Sentry.captureException(signupException)
                    AnonymousSign(auth).signInAnonymously()
                }
            }
        }
        return token
    }

    private fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                sharedPreferences.setHasSignIn(true)
            }
            .addOnFailureListener { e ->
                Sentry.captureException(e)
            }
    }

    private fun hashNonce(rawNonce: String): String {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /*
    private fun signInPlayGamesServices() {
        val signInClient = PlayGames.getGamesSignInClient(activity)

        signInClient.signIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                PlayGames.getPlayersClient(activity).currentPlayer
                    .addOnSuccessListener { player ->
                        val playerName = player.displayName
                        val playerId = player.playerId
                        Log.d("PlayGames", "Player Name: $playerName")
                        Log.d("PlayGames", "Player ID: $playerId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("PlayGames", "Failed to get player details", e.cause)
                    }
            } else {
                Log.e("PlayGames", "Sign-in failed", task.exception)
            }
        }
    }

     */
}

