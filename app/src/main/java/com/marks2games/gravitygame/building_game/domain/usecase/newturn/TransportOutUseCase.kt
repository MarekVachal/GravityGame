package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.domain.usecase.transport.CalculatePaymentForTransport
import com.marks2games.gravitygame.core.domain.error.NewTurnError
import com.marks2games.gravitygame.core.domain.error.NewTurnError.TransportOutError
import com.marks2games.gravitygame.core.domain.error.TransportOutResult
import javax.inject.Inject
import kotlin.math.floor

class TransportOutUseCase @Inject constructor() {
    operator fun invoke(
        transports: List<Transport>,
        planet: Planet,
        isPlanning: Boolean
    ): Triple<Planet, List<Transport>, List<NewTurnError>> {
        val errors = mutableListOf<NewTurnError>()
        var updatedPlanet = planet
        var updatedTransports = transports

        transports.forEach { transport ->
            val (resultPlanet, updatedTransport, maybeError) =
                processTransport(updatedPlanet, transport, isPlanning)
            updatedPlanet = resultPlanet
            Log.d("TransportOutUseCase", "Updated planet use case: $updatedPlanet")
            Log.d("TransportOutUseCase", "Updated transport use case: $updatedTransport")
            updatedTransports = updatedTransports.map {
                if (it.transportId == updatedTransport.transportId) {
                    updatedTransport
                } else {
                    it
                }
            }
            maybeError?.let { errors += it }
        }

        Log.d("TransportOutUseCase", "Updated planet before return: $updatedPlanet")
        return Triple(updatedPlanet, updatedTransports, errors)
    }

    private fun processTransport(
        planet: Planet,
        transport: Transport,
        isPlanning: Boolean
    ): Triple<Planet, Transport, NewTurnError?> {
        val export = if(transport.planet1Id == planet.id) transport.exportFromPlanet1 else transport.exportFromPlanet2
        val missingResources = mutableMapOf<Resource, Int>()

        val newMetal = planet.metal - (export[Resource.METAL] ?: 0)
        val newOrganic =
            planet.organicSediment - (export[Resource.ORGANIC_SEDIMENTS]?.toFloat() ?: 0f)
        val newRocketMaterials = planet.rocketMaterials - (export[Resource.ROCKET_MATERIALS] ?: 0)

        if (newMetal < 0) missingResources[Resource.METAL] = -newMetal
        if (newOrganic < 0f) missingResources[Resource.ORGANIC_SEDIMENTS] =
            -floor(newOrganic).toInt()
        if (newRocketMaterials < 0) missingResources[Resource.ROCKET_MATERIALS] =
            -newRocketMaterials
        
        val payment = if(transport.planet1Id == planet.id) transport.cost else 0f

        return if (missingResources.isNotEmpty()) {
            val updatedTransport = transport.copy(isSuccessOut = false)
            val error = if (isPlanning) {
                TransportOutError(
                    planetId = planet.id,
                    error = TransportOutResult.Error(
                        transportId = transport.transportId,
                        missingResources = missingResources
                    )
                )
            } else null
            val updatedPlanet = if (isPlanning) planet.copy(
                metal = newMetal,
                organicSediment = newOrganic - payment,
                rocketMaterials = newRocketMaterials
            ) else planet

            Triple(updatedPlanet, updatedTransport, error)
        } else {
            val updatedTransport = transport.copy(isSuccessOut = true)
            val updatedPlanet = planet.copy(
                metal = newMetal,
                organicSediment = newOrganic - payment,
                rocketMaterials = newRocketMaterials
            )
            Triple(updatedPlanet, updatedTransport, null)
        }
    }
}