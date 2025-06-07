package com.marks2games.gravitygame.core.data.model.enum_class

import androidx.annotation.StringRes
import com.marks2games.gravitygame.R

enum class ShipType (@StringRes val nameNominative: Int){
    CRUISER (R.string.cruiser),
    DESTROYER (R.string.destroyer),
    GHOST(R.string.ghost),
    WARPER(R.string.warper)
}

fun String.toShipType(): ShipType? {
    return try {
        ShipType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

