package com.example.gravitygame.ui.screens.selectArmyScreen

import com.example.gravitygame.models.ShipType

data class SelectArmyUiState(
    val numberCruisers: Int = 0,
    val numberDestroyers: Int = 0,
    val numberGhosts: Int = 0,
    val showShipInfoDialog: Boolean = false,
    val shipType: ShipType = ShipType.CRUISER
)