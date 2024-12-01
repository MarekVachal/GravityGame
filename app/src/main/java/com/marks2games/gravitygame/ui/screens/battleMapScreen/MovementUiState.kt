package com.marks2games.gravitygame.ui.screens.battleMapScreen

import androidx.compose.ui.geometry.Rect
import com.marks2games.gravitygame.models.ShipType
import com.marks2games.gravitygame.ui.utils.ProgressIndicatorType

data class MovementUiState(

    val acceptableLost: Float = 1f,
    val startPosition: Int? = null,
    val endPosition: Int? = null,
    val showArmyDialog: Boolean = false,
    val isWarperPresent: Boolean = false,
    val showShipInfoDialog: Boolean = false,
    val shipTypeToShow: ShipType = ShipType.CRUISER,
    val showEndOfGameDialog: Boolean = false,
    val showLocationInfoDialog: Boolean = false,
    val showCapitulateInfoDialog: Boolean = false,
    val locationForInfo : Int = 0,
    val mapBoxCoordinates: Map<Int, Rect> = mutableMapOf(),
    val turn: Int = 0,
    val endOfGame: Boolean = false,
    val myLostShips: Int = 0,
    val enemyShipsDestroyed: Int = 0,
    val showBattleInfoOnLocation: Boolean = false,
    val indexOfBattleLocationToShow: Int = 0,
    val showProgressIndicator: Boolean = false,
    val progressIndicatorType: ProgressIndicatorType = ProgressIndicatorType.NEW_TURN,

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
