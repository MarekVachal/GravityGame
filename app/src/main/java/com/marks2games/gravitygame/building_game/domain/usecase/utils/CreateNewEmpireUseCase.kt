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
        val gamePlanet = Planet(
            id = 0,
            metal = 100,
            districts = listOf(
            District.Capitol(districtId = 0),
            District.Empty(districtId = 1),
            District.Empty(districtId = 2),
            District.Industrial(districtId = 3)
        ),
            type = SmallPlanet
        )
        val testPlanet = Planet(
            id = 0,
            districts = listOf(
                District.Capitol(districtId = 0),
                District.Prospectors(districtId = 1),
                District.Industrial(districtId = 2),
                District.UrbanCenter(districtId = 3),
                District.ExpeditionPlatform(districtId = 4),
                District.Empty(districtId = 5),
                District.Empty(districtId = 6),
            ),
            type = SmallPlanet
        )
        val testPlanet2 = Planet(
            name = "Planet 1",
            id = 1,
            districts = listOf(
                District.Capitol(districtId = 0),
                District.Prospectors(districtId = 1),
                District.Industrial(districtId = 2),
                District.UrbanCenter(districtId = 3),
                District.ExpeditionPlatform(districtId = 4),
                District.Empty(districtId = 5),
                District.Empty(districtId = 6),
            ),
            type = SmallPlanet
        )
        return Empire(
            planets = listOf(gamePlanet),
            borderForNewPlanet = calculatePlanetCost.invoke(1),
            lastGetPlanet = SmallPlanet
        )
    }
}