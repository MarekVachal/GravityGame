package com.marks2games.gravitygame.core.domain.usecases.sharedRepository

import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

class GetKeepScreenOnUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    suspend operator fun invoke(): Boolean {
        return sharedPreferencesRepository.getKeepScreenOn()
    }
}