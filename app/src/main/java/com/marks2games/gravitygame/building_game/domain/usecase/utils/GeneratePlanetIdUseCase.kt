package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class GeneratePlanetIdUseCase @Inject constructor() {
    operator fun invoke(planets: List<Planet>): Int {
        val usedIds = planets.map { it.id }.toSet()
        var id = 0
        while (id in usedIds) {
            id++
        }
        return id
    }
}