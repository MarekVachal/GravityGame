package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CreateNewPlanetUseCase @Inject constructor() {
    operator fun invoke(planetCount: Int): Planet{
        return Planet(
            id = planetCount -1,
            name = "Planet $planetCount",
        )
    }
}