package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import javax.inject.Inject

class UpdateMapSizeUseCase @Inject constructor() {
    operator fun invoke(newScreenSize: IntSize, spaceBetweenNodes: Float): Size{
        val mapWidth = newScreenSize.width * spaceBetweenNodes
        val mapHeight = newScreenSize.height * spaceBetweenNodes
        return Size(mapWidth, mapHeight)
    }
}