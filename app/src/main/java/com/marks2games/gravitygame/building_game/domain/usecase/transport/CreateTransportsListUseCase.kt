package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class GetAllTransports @Inject constructor() {
    operator fun invoke(empire: Empire): List<Transport> {
        val plannedTransports = empire.actions
            .filterIsInstance<Action.TransportAction>()
            .map { it.transport }
        return plannedTransports + empire.transports

    }
}