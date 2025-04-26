package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CloseDistrictIsWorkingUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, actions: List<Action>): List<District> {
        val listOfId: MutableList<Int> = mutableListOf()
        actions
            .filterIsInstance<Action.DistrictAction>()
            .forEach { action ->
            listOfId.add(action.districtId)
        }
        return planet.districts.map { district ->
            if (listOfId.contains(district.districtId)) {
                district.copyWithUpdatedWorking(false)
            } else {
                district
            }
        }
    }
}