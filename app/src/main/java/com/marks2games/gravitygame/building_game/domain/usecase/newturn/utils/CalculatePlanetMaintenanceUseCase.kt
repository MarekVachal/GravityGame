package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.pow

class CalculatePlanetMaintenanceUseCase @Inject constructor() {
    operator fun invoke(level: Int): Int {
        return maxOf(0, ceil(((level - 9).toDouble()).pow(2) / 10).toInt())
    }
}