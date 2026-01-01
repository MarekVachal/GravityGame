package com.marks2games.gravitygame.core.data.datasource

import android.app.Activity
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.marks2games.gravitygame.R
import io.sentry.Sentry
import java.security.MessageDigest
import javax.inject.Inject
import java.util.UUID

class GoogleAuthHelper @Inject constructor(){

    private var activity: Activity? = null

    fun setActivity(activity: Activity) {
        Log.d("GoogleSign", "Setting activity: ${activity.localClassName}")
        this.activity = activity
    }

    private fun getWebClientId(): String {
        Log.d("GoogleSign", "Retrieving Web Client ID")
        val clientId = activity?.getString(R.string.default_web_client_id)
        if (clientId == null) {
            Log.e("GoogleSign", "Activity is not set! Cannot retrieve Web Client ID")
            throw IllegalStateException("Activity is not set in GoogleAuthHelper!")
        }
        Log.d("GoogleSign", "Retrieved Web Client ID: $clientId")
        return clientId
        /*
        return activity?.getString(R.string.default_web_client_id)
            ?: throw IllegalStateException("Activity is not set in GoogleAuthHelper!")

         */
    }

    fun getCredentialManager(): CredentialManager{
        Log.d("GoogleSign", "Retrieving CredentialManager")
        val manager = activity?.let { CredentialManager.create(it) }
        if (manager == null) {
            Log.e("GoogleSign", "Activity is not set! Cannot create CredentialManager")
            throw IllegalStateException("Activity is not set in GoogleAuthHelper!")
        }
        Log.d("GoogleSign", "CredentialManager created successfully")
        return manager
        /*
        return activity?.let { CredentialManager.create(it) }
            ?: throw IllegalStateException("Activity is not set in GoogleAuthHelper!")

         */

    }

    suspend fun getGoogleIdToken(): String? {
        val nonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(nonce)
        Log.d("GoogleSign", "Generated hashed nonce: $hashedNonce")
        // First attempt with filtering by authorized accounts
        return tryGetGoogleIdToken(hashedNonce, true) ?: tryGetGoogleIdToken(hashedNonce, false)
    }

    private suspend fun tryGetGoogleIdToken(
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): String? {
        return try {
            Log.d("GoogleSign", "Attempting to get Google ID token (filterByAuthorizedAccounts=$filterByAuthorizedAccounts)")
            val googleIdOption = buildGoogleIdOption(hashedNonce, filterByAuthorizedAccounts)
            val request = buildGetCredentialRequest(googleIdOption)
            val result = getCredential(request)

            val credential = result?.credential
            if (credential == null) {
                Log.e("GoogleSign", "No credential received")
                return null
            }
            val idToken = extractIdToken(credential)
            Log.d("GoogleSign", "Extracted ID token: ${idToken?.take(10)}...") // Maskujeme pro bezpečnost
            idToken
        } catch (e: GetCredentialException) {
            handleCredentialException(e)
            null
        } catch(e: GetPublicKeyCredentialException){
            handleCredentialException(e)
            null
        }
    }

    private fun extractIdToken(credential: Credential): String? {
        return if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("GoogleSign", "Successfully extracted Google ID token")
            googleIdTokenCredential.idToken
        } else {
            Log.e("GoogleSign", "Unexpected type of credential: ${credential.type}")
            null
        }
    }

    private suspend fun getCredential(request: GetCredentialRequest): GetCredentialResponse? {
        return runCatching {
            Log.d("GoogleSign", "Requesting credentials...")
            getCredentialManager().getCredential(
                request = request,
                context = activity?: throw IllegalStateException("Activity is not set in GoogleAuthHelper!")
            )
        }.onFailure { e ->
            Log.e("GoogleSign", "Error getting credential", e)
            if (e !is NoCredentialException) {
                Sentry.captureException(e)
            }
        }.getOrNull()
    }

    private fun buildGoogleIdOption(
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(getWebClientId())
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(true)
            .build()
        Log.d("GoogleSign", "Built Google ID option (filterByAuthorizedAccounts=$filterByAuthorizedAccounts)")
    }

    private fun buildGetCredentialRequest(credentialOption: GetGoogleIdOption): GetCredentialRequest {
        Log.d("GoogleSign", "Building GetCredentialRequest")
        return GetCredentialRequest.Builder()
            .addCredentialOption(credentialOption)
            .build()
    }

    private fun hashNonce(rawNonce: String): String {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun handleCredentialException(e: Exception) {
        Sentry.captureException(e)
        Log.e("GoogleSign", "Error getting credential", e)
    }
}