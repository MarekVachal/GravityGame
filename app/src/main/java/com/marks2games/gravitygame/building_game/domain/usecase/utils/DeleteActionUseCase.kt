package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class DeleteActionUseCase @Inject constructor() {
    operator fun invoke(action: Action, actions: List<Action>): List<Action> {
        val newActions = actions.toMutableList()
        newActions.remove(action)
        return newActions.toList()

    }
}