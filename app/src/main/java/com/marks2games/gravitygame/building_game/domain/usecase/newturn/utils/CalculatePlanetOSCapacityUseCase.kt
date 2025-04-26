package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.min

class CalculatePlanetOSCapacityUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        return min(planet.planetOrganicSediments.toInt(), planet.capacityPlanetOS)
    }
}