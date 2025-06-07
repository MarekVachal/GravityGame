package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.core.domain.error.BuildDistrictResult
import javax.inject.Inject

class BuildDistrictUseCase @Inject constructor() {
    operator fun invoke(
        planet: Planet,
        districtToBuild: DistrictEnum,
        idOfNewDistrict: Int
    ): BuildDistrictResult {
        var updatedDistricts = planet.districts

        if (districtToBuild == DistrictEnum.CAPITOL) {
            return BuildDistrictResult.Error.CapitolNotAllowed
        }

        if (districtToBuild == DistrictEnum.EXPEDITION_PLATFORM && planet.districts.any {
                it.type == DistrictEnum.EXPEDITION_PLATFORM
            }) {
            return BuildDistrictResult.Error.ExpeditionPlatformExists
        }

        if (districtToBuild == DistrictEnum.UNNOCUPATED) {
            return BuildDistrictResult.Error.UnnocupatedNotAllowed
        }

        val districtToChange = planet.districts.find { it.districtId == idOfNewDistrict }
        if (districtToChange == null) {
            return BuildDistrictResult.Error.DistrictNotFound
        } else {
            updatedDistricts = updatedDistricts.map {
                if (it.districtId == idOfNewDistrict) {
                    District.InConstruction(
                        districtId = idOfNewDistrict,
                        buildingDistrict = districtToBuild,
                        infra = 0
                    )
                } else {
                    it
                }
            }
        }
        return BuildDistrictResult.Success(updatedDistricts)
    }
}