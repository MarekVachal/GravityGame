package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import javax.inject.Inject

class GetEmpireUseCase @Inject constructor(
    private val empireRepository: EmpireRepository
) {
    suspend operator fun invoke(): Empire {
        return empireRepository.getEmpire()

    }
}