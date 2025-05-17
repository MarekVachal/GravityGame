package com.marks2games.gravitygame.core.domain.repository

interface AuthRepository {
    suspend fun signInWithGoogle()
    suspend fun linkQuestAccountWithGoogle(onUserUpdated: () -> Unit)
    suspend fun logout()
    suspend fun anonymousSignIn()
    suspend fun deleteUser()
    suspend fun registerWithEmail(email: String, password: String)
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun resetPassword(email: String)
}