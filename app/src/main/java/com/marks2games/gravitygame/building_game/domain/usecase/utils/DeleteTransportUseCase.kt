package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class DeleteTransportUseCase @Inject constructor() {
    operator fun invoke(transport: Transport, transports: List<Transport>): List<Transport> {
        val newTransports = transports.toMutableList()
        newTransports.remove(transport)
        return newTransports.toList()
    }
}