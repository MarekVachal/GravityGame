package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class AccumulateDevelopmentUseCase @Inject constructor() {
    operator fun invoke(value: Int, planet: Planet): Planet {
        var infrastructure = planet.infrastructure
        var development = planet.development
        if (infrastructure >= value) {
            infrastructure -= value
            development += value
        }
        return planet.copy(infrastructure = infrastructure, development = development)
    }
}