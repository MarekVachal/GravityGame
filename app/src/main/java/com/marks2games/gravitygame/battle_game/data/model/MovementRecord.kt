package com.marks2games.gravitygame.battle_game.data.model

import com.marks2games.gravitygame.core.data.model.Ship

data class MovementRecord(
    val movementRecordOfTurn: List<Map<Ship, Int>> = listOf(),
    val enemyRecord: List<Ship> = listOf()
)
