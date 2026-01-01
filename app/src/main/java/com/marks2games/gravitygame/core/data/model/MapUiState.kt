package com.marks2games.gravitygame.core.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize

data class MapUiState<T: MapNode> (
    val nodes: List<T> = emptyList(),
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val screenSize: IntSize = IntSize.Zero,
    val mapSize: Size = Size.Zero,
    val buttonSize: Float = 100f,
    val minScale: Float = 0.5f
)