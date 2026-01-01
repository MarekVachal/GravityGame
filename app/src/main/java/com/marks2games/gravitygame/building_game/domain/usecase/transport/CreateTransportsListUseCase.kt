package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class CreateTransportListUseCase @Inject constructor() {
    operator fun invoke(empire: Empire?, planetId: Int? = null): List<Transport> {
        val plannedTransports = empire?.actions
            ?.filterIsInstance<Action.TransportAction>()
            ?.map { it.setting } ?: emptyList()
        val scheduledTransports = empire?.transports ?: emptyList()
        return if(planetId == null) {
            plannedTransports + scheduledTransports
        } else {
            val updatedPlannedTransports = plannedTransports.filter {
                it.planet1Id == planetId || it.planet2Id == planetId
            }
            val setTransports = scheduledTransports.filter {
                it.planet1Id == planetId || it.planet2Id == planetId
            }
            updatedPlannedTransports + setTransports
        }
    }
}