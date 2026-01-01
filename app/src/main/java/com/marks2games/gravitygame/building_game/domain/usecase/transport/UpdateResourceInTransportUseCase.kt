package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class UpdateResourceInTransportUseCase @Inject constructor(
    private val calculatePaymentForTransport: CalculatePaymentForTransport
) {
    operator fun invoke(
        resource: Resource,
        isPlanet1: Boolean,
        isAdding: Boolean,
        transport: Transport
    ): Transport {
        val originalMap = if (isPlanet1) transport.exportFromPlanet1 else transport.exportFromPlanet2
        val currentAmount = originalMap[resource] ?: 0

        if (!isAdding && currentAmount <= 0) return transport
        val newAmount = if (isAdding) currentAmount + 1 else currentAmount - 1
        val newMap = originalMap + (resource to newAmount)
        val updatedTransport = if (isPlanet1) {
            transport.copy(exportFromPlanet1 = newMap)
        } else {
            transport.copy(exportFromPlanet2 = newMap)
        }
        return updatedTransport.copy(cost = calculatePaymentForTransport(updatedTransport))
    }
}