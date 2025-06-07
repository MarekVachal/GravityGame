package com.marks2games.gravitygame.building_game.data.model


data class ResearchUiState(
    val technologies: List<Technology> = emptyList(),
    val isTechnologyInfoDialogShown: Boolean = false,
    val technologyToShowInfo: TechnologyEnum? = null
)