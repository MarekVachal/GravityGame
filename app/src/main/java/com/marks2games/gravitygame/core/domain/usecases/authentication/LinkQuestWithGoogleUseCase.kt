package com.marks2games.gravitygame.core.domain.usecases.authentication

import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import javax.inject.Inject

class LinkQuestWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(onUserUpdated: () -> Unit) {
        authRepository.linkQuestAccountWithGoogle(onUserUpdated)
    }
}