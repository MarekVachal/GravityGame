package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class ChangeDistrictModeUseCase @Inject constructor() {
    operator fun invoke(
        planet: Planet,
        districtForChange: DistrictEnum,
        newMode: Enum<*>
    ): Planet {
        var infrastructure = planet.infrastructure
        val updatedDistricts = planet.districts.toMutableList()
        val index = updatedDistricts.indexOfFirst { district ->
            when (districtForChange) {
                DistrictEnum.PROSPECTORS -> district is District.Prospectors && district.mode != newMode
                DistrictEnum.INDUSTRIAL -> district is District.Industrial && district.mode != newMode
                DistrictEnum.URBAN_CENTER -> district is District.UrbanCenter && district.mode != newMode
                else -> false
            }
        }
        if (index != -1 && infrastructure >= 1) {
            updatedDistricts[index] = when (val district = updatedDistricts[index]) {
                is District.Prospectors -> district.copy(mode = newMode as ProspectorsMode)
                is District.Industrial -> district.copy(mode = newMode as IndustrialMode)
                is District.UrbanCenter -> district.copy(mode = newMode as UrbanCenterMode)
                else -> district
            }
            infrastructure -= 1
        }
        return planet.copy(
            infrastructure = infrastructure,
            districts = updatedDistricts
        )
    }
}