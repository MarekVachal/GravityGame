package com.example.gravitygame.uiStates

data class SelectArmyUiState(
    val numberCruisers: Int = 0,
    val numberDestroyers: Int = 0,
    val numberGhosts: Int = 0,
    val showShipInfoDialog: Boolean = false
)