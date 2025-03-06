package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class TransportUseCase @Inject constructor(
    private val planetRepository: PlanetRepository
) {
    suspend operator fun invoke(
        transport: Transport,
        planets: List<Planet>,
        updatePlanets: (List<Planet>) -> Unit
    ){
        val planet1Id = transport.planet1Id
        val planet2Id = transport.planet2Id
        if(transport.planet1OrganicSediments-1 < 0) return
        val planet1 = planets.find { it.id == planet1Id } ?: return
        val planet2 = planets.find { it.id == planet2Id } ?: return

        val newPlanet1 = planet1.copy(
            metal = transport.planet1Metal,
            organicSediment = transport.planet1OrganicSediments -1,
            rocketMaterials = transport.planet1RocketMaterials
        )

        val newPlanet2 = planet2.copy(
            metal = transport.planet2Metal,
            organicSediment = transport.planet2OrganicSediments,
            rocketMaterials = transport.planet2RocketMaterials
        )
        planetRepository.updatePlanet(newPlanet1)
        planetRepository.updatePlanet(newPlanet2)
        val updatedPlanets = planets.map {
            when (it.id) {
                planet1Id -> newPlanet1
                planet2Id -> newPlanet2
                else -> it
            }
        }
        updatePlanets(updatedPlanets)

    }
}