package com.marks2games.gravitygame.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

fun determineLocationFromOffset(
    offset: Offset,
    mapBoxPositions: Map<Int, Rect>
): Int? {
    mapBoxPositions.forEach { (location, rect) ->
        if (rect.contains(offset)) {
            return location
        }
    }
    return null
}