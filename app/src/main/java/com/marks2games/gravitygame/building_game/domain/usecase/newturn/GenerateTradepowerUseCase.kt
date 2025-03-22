package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

/**
 * Use case responsible for generating tradepower of the empire.
 *
 * This use case calculates and returns tradepower points for the empire.
 * Tradepower points are generated from all remaining influence points.
 * 1 influence point generate 1 tradepower point.
 *
 */
class GenerateTradepowerUseCase @Inject constructor(){
    /**
     * Generate tradepower points for the empire, returning a pair of values representing
     * the tradepower points and influence.
     *
     * @param planet The [Planet] object representing the celestial body on which the operation is performed.
     * @return A [Pair] where:
     *         - The first element (Int) are generated tradepower points, directly taken from the provided planet's [Planet.influence] property.
     *         - The second element (Int) is fixed value representing the influence level (always 0 in this implementation).
     *
     * @sample
     *  val myPlanet = Planet(influence = 5)
     *  val result = myClassInstance(myPlanet) // Equivalent to myClassInstance.invoke(myPlanet)
     *  println("Tradepower: ${result.first}, Influence: ${result.second}") // Output: Tradepower: 5, Influence: 0
     */
    operator fun invoke(
        planet: Planet
    ): Pair<Int, Int> {
        return Pair(planet.influence, 0)
    }
}