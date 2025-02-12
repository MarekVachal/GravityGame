package com.marks2games.gravitygame.signIn

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AnonymousSign(
    private val auth: FirebaseAuth
){

    suspend fun signInAnonymously() {
        withContext(Dispatchers.IO) {
            try {
                val result = auth.signInAnonymously().await()
                val user = result.user
                Log.d("FirebaseAuth", "Guest login successful: ${user?.uid}")
            } catch (e: Exception) {
                Log.e("FirebaseAuth", "Guest login failed", e)
            }
        }
    }
}