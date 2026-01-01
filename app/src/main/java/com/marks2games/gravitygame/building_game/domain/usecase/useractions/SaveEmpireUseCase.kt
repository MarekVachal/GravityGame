package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import javax.inject.Inject

class SaveEmpireUseCase @Inject constructor(
    private val empireRepository: EmpireRepository
) {
    suspend fun invoke(empire: Empire) {
        empireRepository.updateEmpire(empire)
    }
}