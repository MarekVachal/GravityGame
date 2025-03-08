package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResource
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class AccumulateDevelopmentUseCase @Inject constructor(
    private val planetRepository: PlanetRepository
) {
    suspend operator fun invoke(value: Int, planet: Planet): Planet {
        var infrastructure = planet.infrastructure
        var development = planet.development
        if (infrastructure >= value) {
            infrastructure -= value
            development += value
            planetRepository.updatePlanetResource(
                planetId = planet.id,
                resource = PlanetResource.INFRASTRUCTURE,
                value = infrastructure.toDouble()
            )
            planetRepository.updatePlanetResource(
                planetId = planet.id,
                resource = PlanetResource.DEVELOPMENT,
                value = development.toDouble()
            )
        }
        return planet.copy(infrastructure = infrastructure, development = development)
    }
}