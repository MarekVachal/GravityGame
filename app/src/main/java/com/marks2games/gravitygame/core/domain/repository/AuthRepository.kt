package com.marks2games.gravitygame.core.domain.repository

interface AuthRepository {
    suspend fun signInWithGoogle()
    suspend fun linkQuestAccountWithGoogle(onUserUpdated: () -> Unit)
    suspend fun logout()
    suspend fun anonymousSignIn()
    suspend fun deleteUser()
}