package com.example.gravitygame.models

data class MovementRecord(
    val movementRecordOfTurn: List<Map<Ship, Int>> = listOf(),
    val enemyRecord: List<Ship> = listOf()
)
