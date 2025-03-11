package com.marks2games.gravitygame.core.data.repository

import androidx.credentials.ClearCredentialStateRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.marks2games.gravitygame.core.data.datasource.GoogleAuthHelper
import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetHasSignInUseCase
import io.sentry.Sentry
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleAuthHelper: GoogleAuthHelper,
    private val setHasSignInUseCase: SetHasSignInUseCase
): AuthRepository {
    override suspend fun signInWithGoogle() {
        try {
            val token = googleAuthHelper.getGoogleIdToken()
                ?: throw Exception("Google ID token is null")
            authenticateWithFirebase(token)
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    override suspend fun linkQuestAccountWithGoogle(onUserUpdated: () -> Unit) {
        try {
            val token = googleAuthHelper.getGoogleIdToken()
                ?: throw Exception("Google ID token is null")
            val credential = GoogleAuthProvider.getCredential(token, null)
            auth.currentUser?.linkWithCredential(credential)?.await()
            onUserUpdated
        } catch (_: FirebaseAuthUserCollisionException){
            val token = googleAuthHelper.getGoogleIdToken()
                ?: throw Exception("Google ID token is null")
            authenticateWithFirebase(token)
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        googleAuthHelper.getCredentialManager().clearCredentialState(ClearCredentialStateRequest())
        setHasSignInUseCase.invoke(false)
    }

    override suspend fun anonymousSignIn() {
        try {
            auth.signInAnonymously().await()
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }


    override suspend fun deleteUser() {
        val googleIdToken = googleAuthHelper.getGoogleIdToken()
            ?: throw Exception("Google ID token is null")
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        try {
            auth.currentUser?.reauthenticate(credential)?.await()
            auth.currentUser?.delete()?.await()
            setHasSignInUseCase.invoke(false)
        } catch (e: Exception ) {
            Sentry.captureException(e)
        }
    }

    private suspend fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            auth.signInWithCredential(credential).await()
            setHasSignInUseCase.invoke(true)
        } catch (e: Exception){
            Sentry.captureException(e)
            anonymousSignIn()
        }
    }
}