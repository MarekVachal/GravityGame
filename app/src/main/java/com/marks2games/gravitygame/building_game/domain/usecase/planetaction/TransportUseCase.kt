package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class TransportUseCase @Inject constructor() {
    operator fun invoke(
        transport: Transport,
        planets: List<Planet>,
        updatePlanets: (List<Planet>) -> Unit
    ): Pair<Planet, Planet>{
        val planet1Id = transport.planet1Id
        val planet2Id = transport.planet2Id
        val planet1 = planets.find { it.id == planet1Id } ?: return Pair(Planet(), Planet())
        val planet2 = planets.find { it.id == planet2Id } ?: return Pair(Planet(), Planet())
        if(transport.planet1OrganicSediments-1 < 0) return Pair(planet1, planet2)

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
        val updatedPlanets = planets.map {
            when (it.id) {
                planet1Id -> newPlanet1
                planet2Id -> newPlanet2
                else -> it
            }
        }
        updatePlanets(updatedPlanets)
        return Pair(newPlanet1, newPlanet2)
    }
}