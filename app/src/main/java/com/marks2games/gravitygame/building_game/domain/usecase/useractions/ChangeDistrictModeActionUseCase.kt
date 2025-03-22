package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class ChangeDistrictModeActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, districtId: Int, districtType: DistrictEnum, newMode: Enum<*>): List<Action> {
        val action = Action.DistrictAction.ChangeDistrictMode(planetId, districtId, districtType, newMode)
        return actions + action
    }
}