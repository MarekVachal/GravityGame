package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.domain.error.NewTurnError

data class EmpireUiState(
    val errors: List<NewTurnError> = emptyList(),
    val actions: List<Action> = emptyList(),
    val isErrorsShown: Boolean = false,
    val isActionsShown: Boolean = false
)
