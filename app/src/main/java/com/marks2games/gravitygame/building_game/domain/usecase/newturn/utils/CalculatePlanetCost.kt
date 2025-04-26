package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.pow

/**
 * Calculates the cost of creating a new planet based on the current number of planets in the empire.
 *
 * The cost increases exponentially with the number of planets, following the formula:
 * cost = ceil(50 * 1.15^planetsCount)
 */
class CalculatePlanetCost @Inject constructor() {
    /**
     * Calculates the approximate resource requirement based on the number of planets.
     *
     * This function estimates the resource needed using an exponential growth model.
     * The base resource requirement is 50, and it increases by 15% for each additional planet.
     * The result is rounded up to the nearest integer.
     *
     * @param planetsCount The number of planets for which to calculate the resource requirement.
     *                     Must be a non-negative integer.
     * @return The estimated resource requirement as an integer.
     * @throws IllegalArgumentException if planetsCount is negative.
     */
    operator fun invoke(planetsCount: Int): Int {
        return ceil(100 * 1.4.pow(planetsCount - 1)).toInt()
    }
}