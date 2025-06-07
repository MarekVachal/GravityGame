package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.zoomable(
    getScale: () -> Float,
    getOffset: () -> Offset,
    maxScale: Float = 3f,
    minScale: Float = 0.5f,
    isToroidal: Boolean,
    mapSize: Size,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit,
) = pointerInput(Unit) {
    detectTransformGestures { _, pan, zoom, _ ->
        val currentScale = getScale()
        val currentOffset = getOffset()

        val newScale = (currentScale * zoom).coerceIn(minScale, maxScale)
        var newOffset = currentOffset + pan * newScale

        /*
        if (!isToroidal && mapSize != Size.Zero) {
            // For non-toroidal maps, clamp the offset to prevent going beyond edges
            val minXOffset = size.width - mapSize.width * newScale
            val minYOffset = size.height - mapSize.height * newScale

            newOffset = Offset(
                x = newOffset.x.coerceIn(minXOffset, 0f),
                y = newOffset.y.coerceIn(minYOffset, 0f)
            )
        }
        // For toroidal maps, we don't clamp the offset - it can go indefinitely
        */

        onScaleChange(newScale)
        onOffsetChange(newOffset)
    }
}