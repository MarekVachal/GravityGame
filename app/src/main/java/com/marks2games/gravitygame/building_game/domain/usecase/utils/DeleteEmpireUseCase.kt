package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import javax.inject.Inject

class DeleteEmpireUseCase @Inject constructor(
    private val empireRepository: EmpireRepository
) {
    suspend operator fun invoke() {
        empireRepository.deleteEmpire()
    }
}