package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import javax.inject.Inject

class UpdateOffsetUseCase @Inject constructor() {
    operator fun invoke(
        screenSize: IntSize,
        newOffset: Offset,
        scale: Float,
        mapSize: Size,
        nodeSize: Float,
        isMapRotating: Boolean
    ): Offset {
        if (isMapRotating) return newOffset
        val scaledMapWidth = mapSize.width * scale + nodeSize
        val scaledMapHeight = mapSize.height * scale + nodeSize

        val maxX = minOf(0f, screenSize.width - scaledMapWidth)
        val maxY = minOf(0f, screenSize.height - scaledMapHeight)

        return Offset(
            x = newOffset.x.coerceIn(maxX, 0f),
            y = newOffset.y.coerceIn(maxY, 0f)
        )
    }
}