package com.marks2games.gravitygame.building_game.data.util

import androidx.annotation.StringRes

sealed class ActionDescriptionData {
    data class GenericDescription(
        @get:StringRes val actionNameRes: Int,
        val planetName: String
    ): ActionDescriptionData()

    data class DistrictDescription(
        @get:StringRes val actionNameRes: Int,
        @get:StringRes val districtNameRes: Int,
        val planetName: String
    ): ActionDescriptionData()
}