package com.marks2games.gravitygame.core.data.model.enum_class

enum class ShipType{
    CRUISER, DESTROYER, GHOST, WARPER
}

fun String.toShipType(): ShipType? {
    return try {
        ShipType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

