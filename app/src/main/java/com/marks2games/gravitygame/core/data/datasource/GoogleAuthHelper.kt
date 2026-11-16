package com.marks2games.gravitygame.core.data.datasource

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.marks2games.gravitygame.R
import kotlinx.coroutines.CancellationException
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

private const val TAG_AUTH = "GG_AUTH"

class GoogleAuthHelper @Inject constructor() {
    private var activity: Activity? = null

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun getCredentialManager(trace: String): CredentialManager {
        val act = activity ?: error("[$trace] Activity not set in GoogleAuthHelper")
        return CredentialManager.create(act)
    }

    suspend fun getGoogleIdToken(trace: String): String? {
        val act = activity ?: error("[$trace] Activity not set in GoogleAuthHelper")
        val clientId = act.getString(R.string.default_web_client_id)
        val nonce = MessageDigest.getInstance("SHA-256")
            .digest(UUID.randomUUID().toString().toByteArray())
            .joinToString("") { "%02x".format(it) }

        // All callers initiate sign-in explicitly: use the Google button flow.
        // This option must be the only credential option in the request.
        val option = GetSignInWithGoogleOption.Builder(clientId)
            .setNonce(nonce)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        return try {
            Log.i(TAG_AUTH, "[$trace] Starting explicit Google account selection")
            val response = getCredentialManager(trace).getCredential(act, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                error("[$trace] Unexpected credential type: ${credential.type}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG_AUTH, "[$trace] Credential Manager cancelled sign-in: ${e.message}")
            null
        } catch (e: GetCredentialException) {
            Log.e(TAG_AUTH, "[$trace] Google account selection failed: ${e.type}", e)
            throw e
        }
    }
}
