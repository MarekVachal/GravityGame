package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

/**
 * Use case responsible for generating the development progress of a planet.
 *
 * This use case calculates and returns development points for a given planet.
 * Developments points are generated from all remaining infrastructure points.
 * 1 infrastructure point generate 1 development point.
 *
 */
class GenerateDevelopmentUseCase @Inject constructor() {
    /**
     * Generate development points for a given planet, returning a pair of values representing
     * the infrastructure and development levels.
     *
     * @param planet The [Planet] object representing the celestial body on which the operation is performed.
     * @return A [Pair] where:
     *         - The first element (Int) is a fixed value representing the infrastructure level (always 0 in this implementation).
     *         - The second element (Int) is the development level, directly taken from the provided planet's [Planet.infrastructure] property.
     *
     * @sample
     *  val myPlanet = Planet(infrastructure = 5)
     *  val result = myClassInstance(myPlanet) // Equivalent to myClassInstance.invoke(myPlanet)
     *  println("Infrastructure: ${result.first}, Development: ${result.second}") // Output: Infrastructure: 0, Development: 5
     */
    operator fun invoke(planet: Planet): Pair<Int, Int> {
        return Pair(0, planet.infrastructure)
    }
}