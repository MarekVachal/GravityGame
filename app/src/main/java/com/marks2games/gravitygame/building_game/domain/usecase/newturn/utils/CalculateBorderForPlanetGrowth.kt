package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject

class CalculateBorderForPlanetGrowth @Inject constructor() {
    operator fun invoke(planetLevel: Int): Int {
        val newBorder = 100 * (planetLevel - 3)
        return newBorder
    }
}