package com.marks2games.gravitygame.signIn

import com.google.firebase.auth.FirebaseAuth
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AnonymousSign(
    private val auth: FirebaseAuth
){

    suspend fun signInAnonymously() {
        withContext(Dispatchers.IO) {
            try {
                auth.signInAnonymously().await()
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }
    }
}