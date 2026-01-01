package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Action.DistrictAction
import com.marks2games.gravitygame.building_game.data.model.Action.DistrictAction.BuildDistrict
import com.marks2games.gravitygame.building_game.data.model.Action.DistrictAction.DestroyDistrict
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.util.ActionDescriptionData
import javax.inject.Inject

class GetActionDescriptionUseCase @Inject constructor() {
    operator fun invoke(action: Action, empire: Empire?): ActionDescriptionData {
        val planet = empire?.planets?.find { it.id == action.planetId }
        val planetName = planet?.name.orEmpty()
        val districtName = when(action){
            is BuildDistrict -> action.district.nameIdNominative
            is DestroyDistrict -> planet?.districts?.find { it.districtId == action.districtId }?.type?.nameIdNominative
                ?: R.string.unknown_district
            is DistrictAction.ChangeDistrictMode -> action.districtType.nameIdNominative
            else -> R.string.unknown_district
        }
        return when(action){
            is DistrictAction -> {
                ActionDescriptionData.DistrictDescription(
                    actionNameRes = action.name,
                    districtNameRes = districtName,
                    planetName = planetName
                )
            }
            else -> {
                ActionDescriptionData.GenericDescription(
                    actionNameRes = action.name,
                    planetName = planetName
                )
            }
        }

    }
}