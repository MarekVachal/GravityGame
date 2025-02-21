package com.marks2games.gravitygame.tutorial

import com.marks2games.gravitygame.models.Tasks

data class TutorialUiState(
    val showTutorialDialog: Boolean = false,
    val typeTaskToShow: Tasks? = null,
    val battleOverviewTask: Boolean = false,
    val infoShipTask: Boolean = false,
    val numberShipsTask: Boolean = false,
    val timerTask: Boolean = false,
    val movementTask: Boolean = false,
    val locationInfoTask: Boolean = false,
    val locationOwnerTask: Boolean = false,
    val sendShipsTask: Boolean = false,
    val acceptableLostTask: Boolean = false,
    val battleInfoTask: Boolean = false
)