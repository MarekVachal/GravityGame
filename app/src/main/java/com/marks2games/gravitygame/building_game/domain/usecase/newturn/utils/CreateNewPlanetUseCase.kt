package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.LargePlanet
import com.marks2games.gravitygame.building_game.data.model.MediumPlanet
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import com.marks2games.gravitygame.building_game.domain.usecase.technology.CountPlanetsInInnerSphereUseCase
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
    private val countPlanetsInInnerSphere: CountPlanetsInInnerSphereUseCase
) {
    operator fun invoke(empire: Empire): Empire {
        val planetCost = calculatePlanetCost.invoke(empire.planetsCount, empire.technologies)
        val planetId = generatePlanetIdUseCase.invoke(empire.planets)
        val updatedExpeditions = empire.expeditions - planetCost
        val planetType = when (empire.lastGetPlanet) {
            SmallPlanet -> MediumPlanet
            MediumPlanet -> LargePlanet
            LargePlanet -> SmallPlanet
            else -> SmallPlanet
        }
        val countsInnerPlanets = countPlanetsInInnerSphere.invoke(empire.technologies)
        val isInInnerSphere = countsInnerPlanets > empire.planets.size

        val newPlanet = Planet(
            id = planetId,
            name = "Planet ${empire.planetsCount+1}",
            type = planetType,
            isInnerSpherePlanet = isInInnerSphere
        )
        val updatedPlanets = empire.planets.toMutableList().apply {
            add(newPlanet)
        }

        return empire.copy(
            expeditions = updatedExpeditions,
            planets = updatedPlanets,
            borderForNewPlanet = calculatePlanetCost.invoke(empire.planetsCount + 1, empire.technologies),
            lastGetPlanet = planetType,
            planetsCount = empire.planetsCount + 1
        )
    }
}