package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class BuildDistrictActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int?, districtId: Int, district: DistrictEnum): List<Action> {
        val action = planetId?.let{
            Action.DistrictAction.BuildDistrict(planetId = planetId, districtId = districtId, district = district, setting = districtId)
        }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.BUILD_DISTRICT_ACTION && it.setting == districtId }
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