package com.marks2games.gravitygame.battle_game.data.model.realtime_database

import androidx.annotation.Keep

@Keep
data class Capitulation(
    var player1Capitulated: Boolean = false,
    var player2Capitulated: Boolean = false
)
