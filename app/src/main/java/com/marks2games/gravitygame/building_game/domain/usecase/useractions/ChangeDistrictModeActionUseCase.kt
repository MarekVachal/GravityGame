package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class ChangeDistrictModeActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int?, districtId: Int, districtType: DistrictEnum, newMode: Enum<*>?): List<Action> {
        if(newMode == null) return actions
        val action = planetId?.let{
            Action.DistrictAction.ChangeDistrictMode(planetId = it, districtId = districtId, districtType = districtType, newMode = newMode, setting = districtId)
        }?: return actions
        if(actions.contains(action)) return actions
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.CHANGE_MODE_ACTION && it.setting == districtId }
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.add(action)
            newActions.remove(isActionSet)
        }
        return newActions
    }
}