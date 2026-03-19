package com.marks2games.gravitygame.core.data.datasource

import android.app.Activity
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.marks2games.gravitygame.R
import io.sentry.Sentry
import java.security.MessageDigest
import javax.inject.Inject
import java.util.UUID

/**
 * Helper – low-level Credential Manager / GoogleIdToken handling.
 * The important part is: you MUST have Activity set before calling getGoogleIdToken().
 */

private const val TAG_AUTH = "GG_AUTH"
class GoogleAuthHelper @Inject constructor() {

    private var activity: Activity? = null

    fun setActivity(activity: Activity) {
        Log.i(TAG_AUTH, "[activity] setActivity class=${activity.localClassName}")
        this.activity = activity
    }

    private fun mask(value: String?, keepStart: Int = 6, keepEnd: Int = 4): String {
        if (value.isNullOrBlank()) return "<null>"
        if (value.length <= keepStart + keepEnd) return "****"
        return value.take(keepStart) + "…" + value.takeLast(keepEnd)
    }

    /**
     * Use this only for debugging. Web client ID must come from google-services.json
     * (string: default_web_client_id). MUST be the "Web client" from Firebase/Google Cloud.
     */
    private fun getWebClientId(trace: String): String {
        val act = activity ?: throw IllegalStateException("[$trace] Activity not set in GoogleAuthHelper")
        val clientId = act.getString(R.string.default_web_client_id)

        Log.i(TAG_AUTH, "[$trace] webClientId=${mask(clientId, 10, 6)} (len=${clientId.length})")
        return clientId
    }

    fun getCredentialManager(trace: String): CredentialManager {
        val act = activity ?: throw IllegalStateException("[$trace] Activity not set in GoogleAuthHelper")
        Log.d(TAG_AUTH, "[$trace] CredentialManager.create()")
        return CredentialManager.create(act)
    }

    /**
     * Main entry: tries first "authorized accounts only", then fallback to any Google account.
     */
    suspend fun getGoogleIdToken(trace: String): String? {
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(rawNonce)

        Log.i(TAG_AUTH, "[$trace] START getGoogleIdToken nonceHash=${mask(hashedNonce, 8, 6)}")

        // Attempt #1: only accounts already authorized for this app
        val first = tryGetGoogleIdToken(trace, hashedNonce, filterByAuthorizedAccounts = true)
        if (first != null) {
            Log.i(TAG_AUTH, "[$trace] SUCCESS token from authorized accounts")
            return first
        }

        // Attempt #2: allow any Google account on device
        val second = tryGetGoogleIdToken(trace, hashedNonce, filterByAuthorizedAccounts = false)
        if (second != null) {
            Log.i(TAG_AUTH, "[$trace] SUCCESS token from any accounts")
            return second
        }

        Log.w(TAG_AUTH, "[$trace] NO TOKEN (both attempts returned null)")
        return null
    }

    private suspend fun tryGetGoogleIdToken(
        trace: String,
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): String? {
        Log.i(TAG_AUTH, "[$trace] tryGetGoogleIdToken(filterByAuthorizedAccounts=$filterByAuthorizedAccounts)")

        return try {
            val option = buildGoogleIdOption(trace, hashedNonce, filterByAuthorizedAccounts)
            val request = buildGetCredentialRequest(trace, option)
            val response = getCredential(trace, request)

            val credential = response?.credential
            if (credential == null) {
                Log.w(TAG_AUTH, "[$trace] Credential response is null (no UI? cancelled?)")
                return null
            }

            val idToken = extractIdToken(trace, credential)
            if (idToken == null) {
                Log.w(TAG_AUTH, "[$trace] extractIdToken returned null")
            } else {
                Log.i(TAG_AUTH, "[$trace] Got idToken=${mask(idToken)}")
            }
            idToken

        } catch (e: NoCredentialException) {
            // Normal: no suitable accounts, or user has none configured
            Log.w(TAG_AUTH, "[$trace] NoCredentialException (no suitable credentials)", e)
            null

        } catch (e: GetCredentialException) {
            // Important: includes user cancel, developer errors, etc.
            Log.e(TAG_AUTH, "[$trace] GetCredentialException", e)
            Sentry.captureException(e)
            null

        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[$trace] Unexpected error in tryGetGoogleIdToken()", e)
            Sentry.captureException(e)
            null
        }
    }

    private suspend fun getCredential(trace: String, request: GetCredentialRequest): GetCredentialResponse? {
        val act = activity ?: throw IllegalStateException("[$trace] Activity not set in GoogleAuthHelper")

        Log.d(TAG_AUTH, "[$trace] CredentialManager.getCredential() requestCreated")
        return runCatching {
            getCredentialManager(trace).getCredential(
                request = request,
                context = act
            )
        }.onFailure { e ->
            // If user cancels, this can still throw
            Log.e(TAG_AUTH, "[$trace] CredentialManager.getCredential FAILED", e)
            if (e !is NoCredentialException) {
                Sentry.captureException(e)
            }
        }.getOrNull()
    }

    private fun extractIdToken(trace: String, credential: Credential): String? {
        Log.d(TAG_AUTH, "[$trace] extractIdToken credentialClass=${credential::class.java.name} type=${credential.type}")

        return if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            runCatching {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            }.onFailure { e ->
                Log.e(TAG_AUTH, "[$trace] GoogleIdTokenCredential.createFrom FAILED", e)
                Sentry.captureException(e)
            }.getOrNull()
        } else {
            Log.w(TAG_AUTH, "[$trace] Unexpected credential type/class -> cannot extract Google ID token")
            null
        }
    }

    private fun buildGoogleIdOption(
        trace: String,
        hashedNonce: String,
        filterByAuthorizedAccounts: Boolean
    ): GetGoogleIdOption {
        val clientId = getWebClientId(trace)

        Log.d(TAG_AUTH, "[$trace] buildGoogleIdOption filter=$filterByAuthorizedAccounts nonceHash=${mask(hashedNonce, 8, 6)} clientId=${mask(clientId, 10, 6)}")

        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(clientId)
            .setNonce(hashedNonce)
            // If this is too aggressive during debugging, temporarily set false.
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun buildGetCredentialRequest(trace: String, option: GetGoogleIdOption): GetCredentialRequest {
        Log.d(TAG_AUTH, "[$trace] buildGetCredentialRequest()")
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private fun hashNonce(rawNonce: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}