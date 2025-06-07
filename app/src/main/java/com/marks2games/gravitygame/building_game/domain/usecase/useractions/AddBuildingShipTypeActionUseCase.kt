package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import javax.inject.Inject

class AddBuildingShipTypeActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int, value: ShipType): List<Action>{
        val action = Action.SetProduction.ShipTypeBuild(value, planetId)
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.ARMY_ACTION && it.setting == value }
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.add(action)
            newActions.remove(isActionSet)
        }
        return newActions
    }
}