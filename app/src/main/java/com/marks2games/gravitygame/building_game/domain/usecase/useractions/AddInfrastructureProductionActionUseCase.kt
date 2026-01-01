package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import javax.inject.Inject

class AddInfrastructureProductionActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int?, value: InfrastructureSetting): List<Action> {
        val action = planetId?.let {
            Action.SetProduction.InfrastructureProduction(value, it)
        }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.INFRA_ACTION && it.planetId == planetId }
        action?.let{
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