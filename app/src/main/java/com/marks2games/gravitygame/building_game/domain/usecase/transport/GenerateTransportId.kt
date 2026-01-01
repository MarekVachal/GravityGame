package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Empire
import javax.inject.Inject

class GenerateTransportId @Inject constructor() {
    operator fun invoke(empire: Empire) : Int {
        val usedIds = empire.transports.map { it.transportId }.toSet()
        return generateSequence(1) { it + 1 }.first { it !in usedIds }
    }

}