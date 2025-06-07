package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class AddTransportActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, transport: Transport): List<Action> {
        val action = Action.TransportAction(planetId = planetId, setting = transport)
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.ARMY_ACTION && it.setting == transport }
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.add(action)
            newActions.remove(isActionSet)
        }
        return newActions.toList()
    }
}