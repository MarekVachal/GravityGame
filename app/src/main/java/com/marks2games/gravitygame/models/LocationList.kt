package com.marks2games.gravitygame.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationList(
    val locationList: List<Location> = emptyList()
)
        