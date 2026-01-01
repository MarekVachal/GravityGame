package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import javax.inject.Inject

class AddRocketMaterialsProductionActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int?, value: RocketMaterialsSetting): List<Action> {
        val action = planetId?.let { Action.SetProduction.RocketMaterialsProduction(value, it) }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.ROCKET_MATERIALS_ACTION && it.planetId == planetId }
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