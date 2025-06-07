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
        isToroidal: Boolean
    ): Offset {
        /*
        if (isToroidal) {
            // For toroidal maps, don't limit the offset
            return newOffset
        }
        */

        val totalMapWidth = mapSize.width * scale + nodeSize * 3f
        val totalMapHeight = mapSize.height * scale + nodeSize * 3f

        val minXOffset =
            if (totalMapWidth <= screenSize.width) 0f else screenSize.width - totalMapWidth
        val minYOffset =
            if (totalMapHeight <= screenSize.height) 0f else screenSize.height - totalMapHeight


        return Offset(
            x = newOffset.x.coerceIn(minXOffset, 0f),
            y = newOffset.y.coerceIn(minYOffset, 0f)
        )
    }
}