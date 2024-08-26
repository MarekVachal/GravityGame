package com.example.gravitygame.uiStates

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class MovementUiState(

    val acceptableLost: Float = 1f,
    val startPosition: Int? = null,
    val endPosition: Int? = null,
    val showArmyDialog: Boolean = false,
    val isWarperPresent: Boolean = false,
    val showCruiserInfoDialog: Boolean = false,
    val showDestroyerInfoDialog: Boolean = false,
    val showGhostInfoDialog: Boolean = false,
    val showWarperInfoDialog: Boolean = false,
    val showEndOfGameDialog: Boolean = false,
    val showLocationInfoDialog: Boolean = false,
    val locationForInfo : Int = 0,
    val mapBoxCoordinates: Map<Int, Rect> = mutableMapOf(),
    val lastTouchPosition: Offset? = null,
    val turn: Int = 1,

    val cruiserOnPosition: Int = 0,
    val destroyerOnPosition: Int = 0,
    val ghostOnPosition: Int = 0,
    val warperOnPosition: Int = 0,
    val cruiserToMove: Int = 0,
    val destroyerToMove: Int = 0,
    val ghostToMove: Int = 0,
    val warperToMove: Int = 0,
    val movingCruisers: Int = 0,
    val movingDestroyers: Int = 0,
    val movingGhosts: Int = 0,
    val movingWarpers: Int = 0

)
