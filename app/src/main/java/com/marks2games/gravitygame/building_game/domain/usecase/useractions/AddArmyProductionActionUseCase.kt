package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import javax.inject.Inject

class AddArmyProductionActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int?, value: Int): List<Action> {
        val action = planetId?.let { Action.SetProduction.ArmyProduction(value, it) }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.ARMY_ACTION && it.planetId == planetId }
        action?.let {
            if(isActionSet == null){
                newActions.add(it)
            } else {
                newActions.add(it)
                newActions.remove(isActionSet)
            }
        }
        return newActions
    }
}