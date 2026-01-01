package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.BIOMASS_CONTRIBUTION_COEFFICIENT
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateBiomassCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import javax.inject.Inject

class GenerateBiomassUseCase @Inject constructor(
    private val calculateBiomassCapacityUseCase: CalculateBiomassCapacityUseCase,
    private val generatePlanetOrganicSedimentsUseCase: GeneratePlanetOrganicSedimentsUseCase,
    private val applyDiversity: ApplyDiversityTechnologyUseCase
) {
    operator fun invoke(planet: Planet, technologies: List<Technology>): Float{
        val capacity = calculateBiomassCapacityUseCase.invoke(planet) * planet.biomassCapacityBonus
        val planetOSIncome = generatePlanetOrganicSedimentsUseCase.invoke(planet)
        val basicProduction = capacity / BIOMASS_CONTRIBUTION_COEFFICIENT - planetOSIncome
        val updatedProductionByDiversity = basicProduction * applyDiversity.invoke(technologies, DistrictEnum.EMPTY,planet)

        return updatedProductionByDiversity
    }
}