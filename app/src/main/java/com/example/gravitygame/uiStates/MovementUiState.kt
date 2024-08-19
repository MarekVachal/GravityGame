package com.example.gravitygame.uiStates

data class MovementUiState(

    val acceptableLost: Float = 1f,
    val startPositionSelected: Boolean = false,
    val endPositionSelected: Boolean = false,
    val startPosition: Int? = null,
    val endPosition: Int? = null,
    val showArmyDialog: Boolean = false,
    val isWarperPresent: Boolean = false,
    val showCruiserInfoDialog: Boolean = false,
    val showDestroyerInfoDialog: Boolean = false,
    val showGhostInfoDialog: Boolean = false,
    val showWarperInfoDialog: Boolean = false,
    val showEndOfGameDialog: Boolean = false,

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
