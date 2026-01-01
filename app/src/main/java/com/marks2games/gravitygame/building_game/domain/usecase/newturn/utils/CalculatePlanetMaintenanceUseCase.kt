package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject

class CalculatePlanetMaintenanceUseCase @Inject constructor() {
    operator fun invoke(level: Int): Int {
        return if(level > 8) 5 * (level - 8) else 0
    }
}