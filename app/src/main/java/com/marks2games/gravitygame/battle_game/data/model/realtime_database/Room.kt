package com.marks2games.gravitygame.battle_game.data.model.realtime_database

import androidx.annotation.Keep

@Keep
data class Room(
    var player1Id: String = "",
    var player2Id: String = "",
    var player1Ready: Boolean = false,
    var player2Ready: Boolean = false,
    var player1LocationList: SimplifiedMove? = null,
    var player2LocationList: SimplifiedMove? = null,
    var parameters: String = "",
    var status: String = "",
    var capitulation: Capitulation = Capitulation(),
    var battleMap: String = "TINY",
    var gameType: String = "FREE"
)
