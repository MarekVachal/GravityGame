package com.marks2games.gravitygame.core.domain.usecases.sharedRepository

import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    suspend operator fun invoke(language: String) {
        sharedPreferencesRepository.setLanguage(language)
    }
}