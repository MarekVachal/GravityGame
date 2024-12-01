package com.marks2games.gravitygame.models

data class MovementRecord(
    val movementRecordOfTurn: List<Map<Ship, Int>> = listOf(),
    val enemyRecord: List<Ship> = listOf()
)
