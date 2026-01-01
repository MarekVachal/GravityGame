package com.marks2games.gravitygame.core.domain.usecases.sharedRepository

import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

class SetShowTutorialUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    suspend operator fun invoke(showTutorial: Boolean){
        sharedPreferencesRepository.setShowTutorial(showTutorial)
    }
}