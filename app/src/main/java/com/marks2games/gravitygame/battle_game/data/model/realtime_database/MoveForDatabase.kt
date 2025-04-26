package com.marks2games.gravitygame.battle_game.data.model.realtime_database

import androidx.annotation.Keep

@Keep
data class SimplifiedMove(
    var acceptableLost: List<AcceptableLost> = emptyList(),
    var simplifiedShipList: List<SimplifiedShip> = emptyList()
)

@Keep
data class AcceptableLost(
    var locationId: Int = 0,
    var lostValue: Int = 0
)

@Keep
data class SimplifiedShip(
    var id: Int = 0,
    var currentPosition: Int? = null,
    var startingPosition: Int? = null,
    var shipType: String = ""
)