package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.core.domain.error.NewTurnError
import com.marks2games.gravitygame.core.domain.error.NewTurnError.TransportOutError
import com.marks2games.gravitygame.core.domain.error.TransportOutResult
import javax.inject.Inject
import kotlin.math.floor

class TransportOutUseCase @Inject constructor() {
    operator fun invoke(
        transports: List<Transport>,
        planets: List<Planet>
    ): Triple<List<Planet>, List<Transport>, List<NewTurnError>> {
        val errors: MutableList<NewTurnError> = mutableListOf()

        val updatedPlanets = planets.map { planet ->
            var updatedPlanet = planet

            val updatedTransports = transports.map { transport ->
                if (transport.planet1Id == planet.id) {
                    val missingResources: MutableMap<Resource, Int> = mutableMapOf()

                    val newMetal = planet.metal - (transport.exportFromPlanet1[Resource.METAL] ?: 0)
                    val newOrganic = planet.organicSediment - (transport.exportFromPlanet1[Resource.ORGANIC_SEDIMENTS]?.toFloat() ?: 0f)
                    val newRocketMaterials = planet.rocketMaterials - (transport.exportFromPlanet1[Resource.ROCKET_MATERIALS] ?: 0)

                    if (newMetal < 0) missingResources[Resource.METAL] = -newMetal
                    if (newOrganic < 0f) missingResources[Resource.ORGANIC_SEDIMENTS] = -floor(newOrganic).toInt()
                    if (newRocketMaterials < 0) missingResources[Resource.ROCKET_MATERIALS] = -newRocketMaterials

                    if (missingResources.isNotEmpty()) {
                        errors.add(
                            TransportOutError(
                                planetId = planet.id,
                                error = TransportOutResult.Error(
                                    transportId = transport.transportId,
                                    missingResources = missingResources
                                )
                            )
                        )
                        transport.copy(isSuccessOut = false)
                    } else {
                        updatedPlanet = updatedPlanet.copy(
                            metal = newMetal,
                            organicSediment = newOrganic,
                            rocketMaterials = newRocketMaterials
                        )
                        transport.copy(isSuccessOut = true)
                    }
                } else {
                    transport
                }
            }

            updatedPlanet to updatedTransports
        }

        val finalPlanets = updatedPlanets.map { it.first }
        val finalTransports = updatedPlanets.flatMap { it.second }

        return Triple(
            first = finalPlanets,
            second = finalTransports,
            third = errors
        )
    }
}