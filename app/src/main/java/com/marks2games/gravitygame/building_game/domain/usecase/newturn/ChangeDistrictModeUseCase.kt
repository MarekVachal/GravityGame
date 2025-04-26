package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.CHANGE_DISTRICT_MODE_COST
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.core.domain.error.ChangeDistrictModeResult
import javax.inject.Inject

class ChangeDistrictModeUseCase @Inject constructor() {
    operator fun invoke(
        planet: Planet,
        districtId: Int,
        districtForChange: DistrictEnum,
        newMode: Enum<*>,
        continueOnError: Boolean = false
    ): ChangeDistrictModeResult {
        var infrastructure = planet.infrastructure - CHANGE_DISTRICT_MODE_COST
        val updatedDistricts = planet.districts.toMutableList()
        val index = updatedDistricts.indexOfFirst { district ->
            district.districtId == districtId && district.type == districtForChange
        }
        updatedDistricts[index] = when (val district = updatedDistricts[index]) {
            is District.Prospectors -> district.copy(
                mode = newMode as ProspectorsMode,
                isWorking = true
            )
            is District.Industrial -> district.copy(
                mode = newMode as IndustrialMode,
                isWorking = true
            )
            is District.UrbanCenter -> district.copy(
                mode = newMode as UrbanCenterMode,
                isWorking = true
            )
            else -> district
        }
        if(infrastructure < 0) {
            return if(continueOnError) {
                ChangeDistrictModeResult.FailureWithSuccess(
                    ChangeDistrictModeResult.Error.InsufficientInfrastructureForModeChange,
                    ChangeDistrictModeResult.Success(infrastructure, updatedDistricts)
                )
            } else ChangeDistrictModeResult.Error.InsufficientInfrastructureForModeChange
        }
        return ChangeDistrictModeResult.Success(
            updatedInfrastructure = infrastructure,
            districts = updatedDistricts
        )
    }
}