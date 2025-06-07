package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import javax.inject.Inject

class AddArmyProductionActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int, value: Int): List<Action> {
        val action = Action.SetProduction.ArmyProduction(value, planetId)
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.ARMY_ACTION && it.setting == value }
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.add(action)
            newActions.remove(isActionSet)
        }
        return newActions.toList()
    }
}