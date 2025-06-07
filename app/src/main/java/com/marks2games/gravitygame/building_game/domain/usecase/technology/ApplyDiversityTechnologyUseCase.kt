package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import javax.inject.Inject

class ApplyDiversityTechnologyUseCase @Inject constructor(
    private val isTechnologyResearched: IsTechnologyResearchedUseCase
) {
    operator fun invoke(technologies: List<Technology>, districtEnum: DistrictEnum, planet: Planet): Float {
        val isDiversityResearched = isTechnologyResearched.invoke(TechnologyEnum.DIVERSITY, technologies)
        if(!isDiversityResearched) return 1f

        var amountDiversityDistricts = 0f

        if (planet.districts.any { it.type == DistrictEnum.PROSPECTORS } && districtEnum != DistrictEnum.PROSPECTORS) amountDiversityDistricts++
        if (planet.districts.any { it.type == DistrictEnum.EMPTY } && districtEnum != DistrictEnum.EMPTY) amountDiversityDistricts++
        if (planet.districts.any { it.type == DistrictEnum.INDUSTRIAL } && districtEnum != DistrictEnum.INDUSTRIAL) amountDiversityDistricts++
        if (planet.districts.any { it.type == DistrictEnum.EXPEDITION_PLATFORM } && districtEnum != DistrictEnum.EXPEDITION_PLATFORM ) amountDiversityDistricts++
        if (planet.districts.any { it.type == DistrictEnum.URBAN_CENTER }  && districtEnum != DistrictEnum.URBAN_CENTER) amountDiversityDistricts++

        return 1f + 0.01f * amountDiversityDistricts
    }
}