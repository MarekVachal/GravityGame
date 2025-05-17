package com.marks2games.gravitygame.building_game.data.util

import androidx.annotation.StringRes

sealed class ActionDescriptionData {
    data class GenericDescription(
        @StringRes val actionNameRes: Int,
        val planetName: String
    ): ActionDescriptionData()

    data class DistrictDescription(
        @StringRes val actionNameRes: Int,
        @StringRes val districtNameRes: Int,
        val planetName: String
    ): ActionDescriptionData()
}