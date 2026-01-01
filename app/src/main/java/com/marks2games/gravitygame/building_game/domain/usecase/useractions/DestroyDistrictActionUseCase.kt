package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import javax.inject.Inject

class DestroyDistrictActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int?, districtId: Int): List<Action> {
        val action = planetId?.let{
            Action.DistrictAction.DestroyDistrict(planetId = it, districtId = districtId, setting = districtId)
        }?: return actions
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.DESTROY_DISTRICT_ACTION && it.setting == districtId }
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.add(action)
            newActions.remove(isActionSet)
        }
        return newActions
    }
}