package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
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
    private val calculatePlanetCost: CalculatePlanetCost
) {
    /**
     *  This function simulates the process of acquiring a new planet by an empire.
     *  It calculates the cost of the new planet based on the empire's current planet count,
     *  deducts the cost from the empire's expeditions, and adds the new planet to the empire's list of planets.
     *
     * @param empire The [com.marks2games.gravitygame.building_game.data.model.Empire] object representing the empire that is acquiring a new planet.
     * @return A [Pair] containing:
     *         - A [List] of [Planet] representing the updated list of planets after acquiring the new planet.
     *         - An [Int] representing the updated number of expeditions remaining after the purchase.
     */
    operator fun invoke(empire: Empire): Pair<List<Planet>, Int>{
        val planetCost = calculatePlanetCost.invoke(empire.planetsCount)
        val planetId = empire.planetsCount -1
        val updatedExpeditions = empire.expeditions - planetCost
        val newPlanet = Planet(id = planetId, name = "Planet ${empire.planetsCount}")
        val updatedPlanets = empire.planets.toMutableList().apply {
            add(newPlanet)
        }
        return Pair(updatedPlanets, updatedExpeditions)
    }
}