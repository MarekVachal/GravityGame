package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class GetPlanetUseCase @Inject constructor() {
    operator fun invoke(planets: List<Planet>, planetId: Int): Planet? {
        return planets.find { it.id == planetId }
    }
}