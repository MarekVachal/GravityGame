package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class BuildDistrictActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, districtId: Int, district: DistrictEnum): List<Action> {
        val action = Action.DistrictAction.BuildDistrict(planetId, districtId, district)
        return actions + action
    }
}