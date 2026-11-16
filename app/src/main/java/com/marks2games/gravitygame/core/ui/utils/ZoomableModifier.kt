package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.zoomable(
    getScale: () -> Float,
    getOffset: () -> Offset,
    maxScale: Float,
    minScale: Float = 0.5f,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit,
) = pointerInput(minScale, maxScale) {
    detectTransformGestures { centroid, pan, zoom, _ ->
        val currentScale = getScale()
        val currentOffset = getOffset()

        val newScale = (currentScale * zoom).coerceIn(minScale, maxScale)
        // Keep the same map point under the gesture centroid. Pan is already in pixels.
        val newOffset = centroid - (centroid - currentOffset) * (newScale / currentScale) + pan

        onScaleChange(newScale)
        onOffsetChange(newOffset)
    }
}