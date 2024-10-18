package com.example.gravitygame.models

import com.example.gravitygame.maps.BattleMapEnum
import com.example.gravitygame.ui.utils.BattleResultEnum

data class PlayerData(
    var player: Players = Players.PLAYER1,
    var opponent: Players = Players.PLAYER2,
    var playerBattleResult: BattleResultEnum = BattleResultEnum.LOSE,
    var battleMap: BattleMapEnum = BattleMapEnum.TINY,
    var gameType: GameType = GameType.FREE,
    var isOnline: Boolean = false
)

enum class Players{
    PLAYER1, PLAYER2, NONE
}

