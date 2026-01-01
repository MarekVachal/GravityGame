package com.marks2games.gravitygame.core.domain.usecases.authentication

import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        authRepository.signInWithEmail(email, password)
    }
}