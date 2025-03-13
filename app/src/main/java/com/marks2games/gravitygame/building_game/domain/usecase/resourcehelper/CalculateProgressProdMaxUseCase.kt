package com.marks2games.gravitygame.building_game.domain.usecase.resourcehelper

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class CalculateProgressProdMaxUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        return min(planet.infrastructure, floor(planet.biomass).toInt())
    }
}