package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.core.domain.TimeProvider
import javax.inject.Inject

class SaveOneTurnToBankUseCase @Inject constructor(
    private val empireRepository: EmpireRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(empire: Empire): Pair <Int, Long> {
        val newTime = timeProvider.getCurrentTimeMillis()
        val newTurns = empire.savedTurns + 1
        empireRepository.saveTurn(newTurns)
        empireRepository.updateUpdateTime(newTime)

        return Pair (empire.savedTurns, newTime)
    }
}