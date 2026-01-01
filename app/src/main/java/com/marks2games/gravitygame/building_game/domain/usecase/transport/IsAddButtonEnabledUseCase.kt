package com.marks2games.gravitygame.building_game.domain.usecase.transport

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject
import kotlin.collections.set

private const val TAG = "Enabled"

class IsAddButtonEnabledUseCase @Inject constructor(
    private val calculatePaymentForTransport: CalculatePaymentForTransport
) {
    operator fun invoke(
        exportedPlanet: Planet?,
        planet: Planet?,
        resource: Resource,
        isPlanet1: Boolean,
        transport: Transport
    ): Boolean {
        if (planet == null) {
            Log.d(TAG, "Planet is null – button disabled")
            return false
        }

        val originalMap = if (isPlanet1) transport.exportFromPlanet1 else transport.exportFromPlanet2
        val currentAmount = originalMap[resource] ?: 0

        val newCost = calculateNewCost(
            originalMap = originalMap,
            resource = resource,
            currentAmount = currentAmount,
            transport = transport,
            isPlanet1 = isPlanet1,
            calculatePaymentForTransport = calculatePaymentForTransport
        )
        val amountOS = transport.exportFromPlanet1[Resource.ORGANIC_SEDIMENTS] ?: 0
        if (exportedPlanet != null && exportedPlanet.organicSediment < newCost + amountOS){
            Log.d(TAG, "Not enough Organic Sediments on exported planet.\n" +
                    "Needed: ${newCost + amountOS}, Available: ${exportedPlanet.organicSediment}")
            return false
        }
        val enabled =  when (resource) {
            Resource.ORGANIC_SEDIMENTS -> {
                planet.organicSediment > newCost + currentAmount
            }
            Resource.METAL -> {
                planet.metal > currentAmount
            }
            Resource.ROCKET_MATERIALS -> {
                planet.rocketMaterials > currentAmount
            }
            else -> false
        }
        if (resource != Resource.ROCKET_MATERIALS) {
            val available = when (resource) {
                Resource.ORGANIC_SEDIMENTS -> planet.organicSediment
                Resource.METAL             -> planet.metal
                else                       -> 0        // sem by ses neměl dostat
            }

            Log.d(TAG,
                "Resource: $resource\n" +
                        "Available on planet: $available\n" +
                        "Currently exported: $currentAmount\n" +
                        "New cost after +1: $newCost\n" +
                        "Button ${if (enabled) "ENABLED" else "DISABLED"}"
            )
        }
        return enabled
    }
}

private fun calculateNewCost(
    originalMap: Map<Resource, Int>,
    resource: Resource,
    currentAmount: Int,
    transport: Transport,
    isPlanet1: Boolean,
    calculatePaymentForTransport: CalculatePaymentForTransport
): Float {
    val newMap = originalMap.toMutableMap().apply {
        this[resource] = currentAmount + 1
    }
    val newTransport = if (isPlanet1) {
        transport.copy(exportFromPlanet1 = newMap)
    } else {
        transport.copy(exportFromPlanet2 = newMap)
    }
    return calculatePaymentForTransport(newTransport)
}