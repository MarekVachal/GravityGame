package com.example.gravitygame.tutorial

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
    val battleInfoTask: Boolean = false,
)

enum class Tasks{
    INFO_SHIP,
    NUMBER_SHIPS,
    TIMER,
    MOVEMENT,
    LOCATION_INFO,
    LOCATION_OWNER,
    SEND_SHIPS,
    ACCEPTABLE_LOST,
    BATTLE_OVERVIEW,
    BATTLE_INFO
}