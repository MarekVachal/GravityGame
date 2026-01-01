package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateBorderForPlanetGrowth
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.UpdateDistrictsForSettleUseCase
import javax.inject.Inject

class SettleDistrictUseCase @Inject constructor(
    private val calculateBorderForPlanetGrowth: CalculateBorderForPlanetGrowth,
    private val updateDistrictsForSettle: UpdateDistrictsForSettleUseCase
) {
    operator fun invoke(planet: Planet, id: Int): Planet {
        val newDistricts = planet.districts.toMutableList()
        newDistricts.removeIf { it.districtId == id }
        newDistricts.add(District.Empty(districtId = id))
        val updatedDistricts = updateDistrictsForSettle.invoke(newDistricts, planet)
        val oldGrowthBorder = planet.planetGrowthBorder
        val newBorder = calculateBorderForPlanetGrowth.invoke(planet.level+1)
        val updatedPlanet = planet.copy(
            level = planet.level + 1,
            progress = planet.progress - oldGrowthBorder,
            districts = updatedDistricts,
            planetGrowthBorder = newBorder
        )
        return updatedPlanet
    }
}