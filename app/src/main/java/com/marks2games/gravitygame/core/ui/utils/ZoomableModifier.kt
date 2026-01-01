package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.zoomable(
    getScale: () -> Float,
    getOffset: () -> Offset,
    maxScale: Float = 3f,
    minScale: Float = 0.5f,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit,
) = pointerInput(Unit) {
    detectTransformGestures { _, pan, zoom, _ ->
        val currentScale = getScale()
        val currentOffset = getOffset()

        val newScale = (currentScale * zoom).coerceIn(minScale, maxScale)
        val newOffset = currentOffset + pan * newScale

        onScaleChange(newScale)
        onOffsetChange(newOffset)
    }
}