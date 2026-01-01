package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.domain.error.NewTurnError

data class EmpireUiState(
    val errors: List<NewTurnError> = emptyList(),
    val isErrorsShown: Boolean = false,
    val isActionsShown: Boolean = false,
    val isTransportMenuShown: Boolean = false,
    val isTransportDialogShown: Boolean = false,
    val planetForTransport: Planet? = null
)
