package com.marks2games.gravitygame.core.data.datasource

import android.content.Context
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
import com.marks2games.gravitygame.core.domain.usecases.authentication.AnonymousSignInUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.Sentry
import java.security.MessageDigest
import javax.inject.Inject
import java.util.UUID

class GoogleAuthHelper @Inject constructor(
    @ApplicationContext private val context: Context
){

    private val webClientId = context.getString(R.string.default_web_client_id)
    private val credentialManager = CredentialManager.create(context)

    fun getCredentialManager(): CredentialManager{
        return credentialManager
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

    private fun extractIdToken(credential: Credential): String? {
        return if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            googleIdTokenCredential.idToken
        } else {
            Log.e("GoogleSign", "Unexpected type of credential: ${credential.type}")
            null
        }
    }

    private suspend fun getCredential(request: GetCredentialRequest): GetCredentialResponse? {
        return runCatching {
            credentialManager.getCredential(
                request = request,
                context = context
            )
        }.onFailure { e ->
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