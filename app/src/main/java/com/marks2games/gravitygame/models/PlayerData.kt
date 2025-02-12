package com.marks2games.gravitygame.models

import com.google.firebase.database.DatabaseReference
import com.marks2games.gravitygame.maps.BattleMapEnum
import com.marks2games.gravitygame.ui.utils.BattleResultEnum

data class PlayerData(
    val player: Players = Players.PLAYER1,
    val opponent: Players = Players.PLAYER2,
    val playerBattleResult: BattleResultEnum = BattleResultEnum.LOSE,
    val battleMap: BattleMapEnum = BattleMapEnum.TINY,
    val gameType: GameType = GameType.FREE,
    val isOnline: Boolean = false,
    val roomRef: DatabaseReference? = null
)

enum class Players{
    PLAYER1, PLAYER2, NONE
}

