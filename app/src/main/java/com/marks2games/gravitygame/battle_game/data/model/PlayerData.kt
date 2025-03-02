package com.marks2games.gravitygame.battle_game.data.model

import com.google.firebase.database.DatabaseReference
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleMapEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.GameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players

data class PlayerData(
    val player: Players = Players.PLAYER1,
    val opponent: Players = Players.PLAYER2,
    val playerBattleResult: BattleResultEnum = BattleResultEnum.LOSE,
    val battleMap: BattleMapEnum = BattleMapEnum.TINY,
    val gameType: GameType = GameType.FREE,
    val isOnline: Boolean = false,
    val roomRef: DatabaseReference? = null
)