package com.marks2games.gravitygame.building_game.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize

data class ResearchUiState(
    val technologies: List<Technology> = emptyList(),
    val currentResearchId: TechnologyEnum? = null,
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val screenSize: IntSize = IntSize.Zero,
    val mapSize: Size = Size.Zero,
    val buttonSize: Float = 100f * scale,
    val minScale: Float = 0.5f,
    val isTechnologyInfoDialogShown: Boolean = false,
    val technologyToShowInfo: TechnologyEnum? = null,
    val spaceBetweenNodes: Float = 1f,
    val nodesPadding: Float = 5f,
    val defaultButtonSize: Float = 100f,
    val nodeCenterXCorrection: Float = 1.5f,
    val nodeCenterYCorrection: Float = 1f
)