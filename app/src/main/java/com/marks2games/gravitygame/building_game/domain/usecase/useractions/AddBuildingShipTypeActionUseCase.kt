package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import javax.inject.Inject

class AddBuildingShipTypeActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int?, value: ShipType): List<Action>{
        val action = planetId?.let {
            Action.SetProduction.ShipTypeBuild(value, it)
        }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.SHIP_TYPE_ACTION && it.planetId == planetId }
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