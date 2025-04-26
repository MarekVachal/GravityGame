package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import javax.inject.Inject

class AddRocketMaterialsProductionActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, value: RocketMaterialsSetting): List<Action> {
        val action = Action.SetProduction.RocketMaterialsProduction(value, planetId)
        val newActions = actions.toMutableList()
        newActions.add(action)
        return newActions.toList()
    }
}