package com.example.gravitygame.uiStates

import com.example.gravitygame.models.Ship

data class MovementRecord(
    val movementRecordOfTurn: List<Map<Ship, Int>> = listOf()
)
