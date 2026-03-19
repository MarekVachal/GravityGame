package com.marks2games.gravitygame.core.data.repository

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.marks2games.gravitygame.core.data.datasource.GoogleAuthHelper
import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetHasSignInUseCase
import io.sentry.Sentry
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * ============================================================
 *  LOGGING GUIDE
 * ============================================================
 *  Filter in Logcat:  "GG_AUTH"
 *
 *  Common failure hotspots:
 *   - Activity not set in GoogleAuthHelper (call setActivity() early)
 *   - Wrong default_web_client_id (must be Web client ID from Firebase config)
 *   - SHA-1 / SHA-256 missing in Firebase for your debug/release keystore
 *   - Credential Manager returns NoCredentialException
 *   - Returned Credential is not GoogleIdTokenCredential
 *   - Firebase signInWithCredential fails (developer error / invalid token)
 */
private const val TAG_AUTH = "GG_AUTH"
private const val TAG_FB = "GG_AUTH/FIREBASE"

// Mask helper for safe logs (never print full tokens / IDs)
private fun mask(value: String?, keepStart: Int = 6, keepEnd: Int = 4): String {
    if (value.isNullOrBlank()) return "<null>"
    if (value.length <= keepStart + keepEnd) return "****"
    return value.take(keepStart) + "…" + value.takeLast(keepEnd)
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleAuthHelper: GoogleAuthHelper,
    private val setHasSignInUseCase: SetHasSignInUseCase
) : AuthRepository {

    override suspend fun signInWithGoogle() {
        val trace = "signInWithGoogle:${UUID.randomUUID().toString().take(8)}"
        Log.i(TAG_AUTH, "[$trace] START Google sign-in")

        try {
            val token = googleAuthHelper.getGoogleIdToken(trace)
                ?: throw IllegalStateException("[$trace] Google ID token is null")

            Log.i(TAG_AUTH, "[$trace] Got Google ID token (masked=${mask(token)}) -> authenticateWithFirebase")
            authenticateWithFirebase(idToken = token, trace = trace)

        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[$trace] FAIL Google sign-in", e)
            Sentry.captureException(e)
        } finally {
            Log.i(TAG_AUTH, "[$trace] END Google sign-in")
        }
    }

    override suspend fun registerWithEmail(email: String, password: String) {
        try {
            Log.i(TAG_AUTH, "[emailRegister] START email=${mask(email, 3, 10)}")
            auth.createUserWithEmailAndPassword(email, password).await()
            setHasSignInUseCase(true)
            Log.i(TAG_AUTH, "[emailRegister] SUCCESS")
        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[emailRegister] FAIL", e)
            Sentry.captureException(e)
            throw e
        }
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        try {
            Log.i(TAG_AUTH, "[emailSignIn] START email=${mask(email, 3, 10)}")
            auth.signInWithEmailAndPassword(email, password).await()
            setHasSignInUseCase(true)
            Log.i(TAG_AUTH, "[emailSignIn] SUCCESS")
        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[emailSignIn] FAIL", e)
            Sentry.captureException(e)
            throw e
        }
    }

    override suspend fun resetPassword(email: String) {
        try {
            Log.i(TAG_AUTH, "[resetPassword] START email=${mask(email, 3, 10)}")
            auth.sendPasswordResetEmail(email).await()
            Log.i(TAG_AUTH, "[resetPassword] SUCCESS")
        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[resetPassword] FAIL", e)
            Sentry.captureException(e)
            throw e
        }
    }

    override suspend fun linkQuestAccountWithGoogle(onUserUpdated: () -> Unit) {
        val trace = "linkWithGoogle:${UUID.randomUUID().toString().take(8)}"
        Log.i(TAG_AUTH, "[$trace] START link currentUser=${auth.currentUser?.uid ?: "<null>"}")

        try {
            val token = googleAuthHelper.getGoogleIdToken(trace)
                ?: throw IllegalStateException("[$trace] Google ID token is null")

            val credential = GoogleAuthProvider.getCredential(token, null)
            auth.currentUser?.linkWithCredential(credential)?.await()
            Log.i(TAG_AUTH, "[$trace] LINK SUCCESS -> calling onUserUpdated()")
            onUserUpdated()

        } catch (_: FirebaseAuthUserCollisionException) {
            // account already exists -> fallback to sign-in
            Log.w(TAG_AUTH, "[$trace] COLLISION -> fallback to authenticateWithFirebase()")
            val token = googleAuthHelper.getGoogleIdToken(trace)
                ?: throw IllegalStateException("[$trace] Google ID token is null")
            authenticateWithFirebase(idToken = token, trace = trace)

        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[$trace] LINK FAIL", e)
            Sentry.captureException(e)
        } finally {
            Log.i(TAG_AUTH, "[$trace] END link flow")
        }
    }

    override suspend fun logout() {
        val trace = "logout:${UUID.randomUUID().toString().take(8)}"
        Log.i(TAG_AUTH, "[$trace] START logout uid=${auth.currentUser?.uid ?: "<null>"}")

        runCatching { auth.signOut() }
            .onFailure { Log.e(TAG_AUTH, "[$trace] FirebaseAuth.signOut failed", it) }

        runCatching {
            googleAuthHelper.getCredentialManager(trace)
                .clearCredentialState(ClearCredentialStateRequest())
            Log.i(TAG_AUTH, "[$trace] CredentialManager.clearCredentialState SUCCESS")
        }.onFailure {
            Log.e(TAG_AUTH, "[$trace] CredentialManager.clearCredentialState FAIL", it)
            Sentry.captureException(it)
        }

        setHasSignInUseCase(false)
        Log.i(TAG_AUTH, "[$trace] END logout")
    }

    override suspend fun anonymousSignIn() {
        val trace = "anon:${UUID.randomUUID().toString().take(8)}"
        Log.i(TAG_AUTH, "[$trace] START anonymousSignIn")

        try {
            auth.signInAnonymously().await()
            Log.i(TAG_AUTH, "[$trace] SUCCESS anonymousSignIn uid=${auth.currentUser?.uid}")
        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[$trace] FAIL anonymousSignIn", e)
            Sentry.captureException(e)
        }
    }

    override suspend fun deleteUser() {
        val trace = "deleteUser:${UUID.randomUUID().toString().take(8)}"
        Log.i(TAG_AUTH, "[$trace] START deleteUser uid=${auth.currentUser?.uid ?: "<null>"}")

        try {
            val googleIdToken = googleAuthHelper.getGoogleIdToken(trace)
                ?: throw IllegalStateException("[$trace] Google ID token is null (reauth needed)")
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.currentUser?.reauthenticate(credential)?.await()
            auth.currentUser?.delete()?.await()
            setHasSignInUseCase(false)

            Log.i(TAG_AUTH, "[$trace] SUCCESS deleteUser")
        } catch (e: Exception) {
            Log.e(TAG_AUTH, "[$trace] FAIL deleteUser", e)
            Sentry.captureException(e)
        }
    }

    private suspend fun authenticateWithFirebase(idToken: String, trace: String) {
        Log.i(TAG_FB, "[$trace] START Firebase signInWithCredential (token=${mask(idToken)})")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid
            Log.i(TAG_FB, "[$trace] SUCCESS Firebase signIn uid=$uid isNewUser=${result.additionalUserInfo?.isNewUser}")
            setHasSignInUseCase(true)

        } catch (e: Exception) {
            // If this fails, it’s usually SHA / OAuth mismatch or invalid token.
            Log.e(TAG_FB, "[$trace] FAIL Firebase signInWithCredential -> fallback anonymousSignIn()", e)
            Sentry.captureException(e)
            anonymousSignIn()
        } finally {
            Log.i(TAG_FB, "[$trace] END Firebase signIn")
        }
    }
}
