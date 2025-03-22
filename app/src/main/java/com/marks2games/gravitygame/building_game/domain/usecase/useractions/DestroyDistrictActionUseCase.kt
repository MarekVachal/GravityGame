package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class DestroyDistrictActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, districtId: Int): List<Action> {
        val action = Action.DistrictAction.DestroyDistrict(planetId, districtId)
        return actions + action
    }
}