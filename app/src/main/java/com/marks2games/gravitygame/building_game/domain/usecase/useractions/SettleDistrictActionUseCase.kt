package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import javax.inject.Inject

class SettleDistrictActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int?, districtId: Int): List<Action> {
        val action = planetId?.let{
            Action.DistrictAction.SettleDistrict(planetId = planetId, districtId = districtId, setting = districtId)
        }?: return actions
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.SETTLE_DISTRICT_ACTION && it.setting == districtId}
        if(isActionSet == null){
            newActions.add(action)
        } else {
            newActions.remove(isActionSet)
            newActions.add(action)
        }
        return newActions
    }
}