package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject

class CalculateBorderForPlanetGrowth @Inject constructor() {
    operator fun invoke(planetLevel: Int): Int {
        val newBorder = (planetLevel + 1) * 100
        return newBorder
    }
}