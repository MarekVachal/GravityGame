package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import javax.inject.Inject

/**
 * Use case responsible for checking if the empire has enough expeditions to acquire a new planet.
 *
 * This class determines if a given [Empire] object has sufficient expeditions to purchase a new planet,
 * based on a dynamically calculated planet acquisition cost. The cost increases with each planet the
 * empire already possesses.
 *
 * @constructor Creates a [CheckForNewPlanetUseCase] instance.
 */
class CheckForNewPlanetUseCase @Inject constructor(
    private val calculatePlanetCost: CalculatePlanetCost
) {
    /**
     * Checks if an empire has enough expeditions to colonize a new planet.
     *
     * The cost of colonizing a new planet increases exponentially with the number of planets
     * the empire already controls. The cost is calculated as 50 * 1.15^n round up, where n is the
     * number of planets the empire owns.
     *
     * @param empire The [Empire] to check.
     * @return `true` if the empire has enough expeditions to colonize a new planet, `false` otherwise.
     *
     * @sample
     *  val empire1 = Empire(planetsCount = 1, expeditions = 58)
     *  val empire2 = Empire(planetsCount = 5, expeditions = 100)
     *  val empire3 = Empire(planetsCount = 10, expeditions = 200)
     *
     *  invoke(empire1) // true, cost is ~58
     *  invoke(empire2) // false, cost is ~101
     *  invoke(empire3) // false, cost is ~203
     */
    operator fun invoke(empire: Empire): Boolean {
        val planetCost = calculatePlanetCost.invoke(empire.planetsCount, empire.technologies)
        return empire.expeditions >= planetCost
    }
}