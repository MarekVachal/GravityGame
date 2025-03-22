package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
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
        if (planet.infrastructure < DISTRICT_BUILD_COST && districtToBuild != DistrictEnum.EMPTY) {
            return BuildDistrictResult.Error.InsufficientInfrastructure
        }

        if (districtToBuild == DistrictEnum.CAPITOL) {
            return BuildDistrictResult.Error.CapitolNotAllowed
        }

        if (districtToBuild == DistrictEnum.EXPEDITION_PLATFORM && planet.districts.any {
            it.type == DistrictEnum.EXPEDITION_PLATFORM
        }) {
            return BuildDistrictResult.Error.ExpeditionPlatformExists
        }

        val chosenDistrict = when (districtToBuild) {
            DistrictEnum.PROSPECTORS -> District.Prospectors(districtId = idOfNewDistrict)
            DistrictEnum.EMPTY -> District.Empty(districtId = idOfNewDistrict)
            DistrictEnum.INDUSTRIAL -> District.Industrial(districtId = idOfNewDistrict)
            DistrictEnum.URBAN_CENTER -> District.UrbanCenter(districtId = idOfNewDistrict)
            DistrictEnum.EXPEDITION_PLATFORM -> District.ExpeditionPlatform(districtId = idOfNewDistrict)
            DistrictEnum.CAPITOL -> throw IllegalStateException("Capitol should have been handled earlier")
        }

        val newDistricts = planet.districts.toMutableList()
        newDistricts.add(chosenDistrict)

        val updatedInfrastructure = when {
            chosenDistrict.type == DistrictEnum.EMPTY -> planet.infrastructure
            else -> planet.infrastructure - DISTRICT_BUILD_COST
        }

        return BuildDistrictResult.Success(updatedInfrastructure, newDistricts.toList())

    }


}