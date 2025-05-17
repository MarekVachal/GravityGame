package com.marks2games.gravitygame.core.domain.usecases.authentication

import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email:String, password: String){
        authRepository.registerWithEmail(email, password)
    }
}