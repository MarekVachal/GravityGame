package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class AddProgressProductionActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, value: Int): List<Action> {
        val action = Action.SetProduction.ProgressProduction(value, planetId)
        val newActions = actions.toMutableList()
        newActions.add(action)
        return newActions.toList()
    }
}