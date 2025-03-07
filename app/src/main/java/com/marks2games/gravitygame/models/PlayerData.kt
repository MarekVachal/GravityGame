package com.marks2games.gravitygame.models

import com.google.firebase.database.DatabaseReference

data class PlayerData(
    val player: Players = Players.PLAYER1,
    val opponent: Players = Players.PLAYER2,
    val playerBattleResult: BattleResultEnum = BattleResultEnum.LOSE,
    val battleMap: BattleMapEnum = BattleMapEnum.TINY,
    val gameType: GameType = GameType.FREE,
    val isOnline: Boolean = false,
    val roomRef: DatabaseReference? = null
)