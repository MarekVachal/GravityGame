package com.marks2games.gravitygame.ui.screens.statisticScreen

import com.marks2games.gravitygame.database.BattleResult

data class StatisticUiState (
    val battleResult: BattleResult? = null,
    val totalMyShipLost: Int = 0,
    val totalEnemyShipDestroyed: Int = 0,
    val averageTurn: Int = 0,
    val totalBattle: Int = 0,
    val countOfWins: Int = 0,
    val countOfLost: Int = 0,
    val countOfDraw: Int = 0,
    val listBattleResult: List<BattleResult> = emptyList()
)