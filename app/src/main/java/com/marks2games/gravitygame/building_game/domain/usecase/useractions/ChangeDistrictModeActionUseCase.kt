package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class ChangeDistrictModeActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, districtId: Int, districtType: DistrictEnum, newMode: Enum<*>?): List<Action> {
        if(newMode == null) return actions
        val action = Action.DistrictAction.ChangeDistrictMode(planetId = planetId, districtId = districtId, districtType = districtType, newMode = newMode)
        if(actions.contains(action)) return actions
        val newActions = actions.toMutableList()
        newActions.add(action)
        return newActions.toList()
    }
}