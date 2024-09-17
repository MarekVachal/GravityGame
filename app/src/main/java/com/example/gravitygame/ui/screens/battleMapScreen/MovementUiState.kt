package com.example.gravitygame.ui.screens.battleMapScreen

import androidx.compose.ui.geometry.Rect
import com.example.gravitygame.models.ShipType

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
    val turn: Int = 1,
    val endOfGame: Boolean = false,
    val isArmyDialogInitialized: Boolean = false,
    val isBattleScreenInitialized: Boolean = false,
    val isLocationInfoInitialized: Boolean = false,
    val myLostShips: Int = 0,
    val enemyShipsDestroyed: Int = 0,
    val showBattleInfoOnLocation: Boolean = false,
    val battleLocationToShow: Int = 0,

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
