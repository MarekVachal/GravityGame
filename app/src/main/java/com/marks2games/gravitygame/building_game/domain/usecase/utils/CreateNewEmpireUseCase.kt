package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetCost
import javax.inject.Inject

class CreateNewEmpireUseCase @Inject constructor(
    private val calculatePlanetCost: CalculatePlanetCost
) {
    operator fun invoke(): Empire {
        val startingDistricts = SmallPlanet.districts.map {
            if( it.districtId == 1){
                District.Industrial(districtId = 1)
            } else {
                it
            }
        }
        val gamePlanet = Planet(
            id = 0,
            metal = 100,
            type = SmallPlanet,
            isInnerSpherePlanet = true,
            districts = startingDistricts
        )
        return Empire(
            planets = listOf(gamePlanet),
            borderForNewPlanet = calculatePlanetCost.invoke(1, emptyList()),
            lastGetPlanet = SmallPlanet
        )
    }
}