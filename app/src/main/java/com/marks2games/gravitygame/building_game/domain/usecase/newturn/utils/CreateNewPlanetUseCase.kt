package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.LargePlanet
import com.marks2games.gravitygame.building_game.data.model.MediumPlanet
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GeneratePlanetIdUseCase
import javax.inject.Inject

/**
 * Use case responsible for creating a new planet and updating the empire's state.
 *
 * This class handles the logic of creating a new planet, calculating the cost
 * associated with it, and updating the empire's resources and planet list accordingly.
 *
 * @property calculatePlanetCost An instance of [CalculatePlanetCost] to determine the cost of creating a new planet.
 */
class CreateNewPlanetUseCase @Inject constructor(
    private val calculatePlanetCost: CalculatePlanetCost,
    private val generatePlanetIdUseCase: GeneratePlanetIdUseCase,
    private val calculateBorderForPlanetGrowth: CalculateBorderForPlanetGrowth
) {
    operator fun invoke(empire: Empire): Pair<List<Planet>, Int>{
        val planetCost = calculatePlanetCost.invoke(empire.planetsCount)
        val planetId = generatePlanetIdUseCase.invoke(empire.planets)
        val updatedExpeditions = empire.expeditions - planetCost
        val planetType = when(empire.lastGetPlanet){
            SmallPlanet -> MediumPlanet
            MediumPlanet -> LargePlanet
            LargePlanet -> SmallPlanet
            else -> SmallPlanet
        }
        val newPlanet = Planet(
            id = planetId,
            name = "Planet ${empire.planetsCount}",
            type = planetType,
            planetGrowthBorder = calculateBorderForPlanetGrowth.invoke(planetType.startingLevel)
        )
        val updatedPlanets = empire.planets.toMutableList().apply {
            add(newPlanet)
        }
        return Pair(updatedPlanets.toList(), updatedExpeditions)
    }
}