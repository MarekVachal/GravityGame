package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class GetPlanetUseCase @Inject constructor() {
    operator fun invoke(planetId: Int, empire: Empire?): Planet? {
        return empire?.planets?.firstOrNull { it.id == planetId }
    }
}