package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetMaintenanceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.DecreasePlanetMaintenanceByCyberneticsUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.IsTechnologyResearchedUseCase
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult
import javax.inject.Inject
import kotlin.math.abs

class PlanetMaintenanceUseCase @Inject constructor(
    private val calculatePlanetMaintenance: CalculatePlanetMaintenanceUseCase,
    private val isTechnologyResearched: IsTechnologyResearchedUseCase,
    private val decreaseMaintenance: DecreasePlanetMaintenanceByCyberneticsUseCase
){
    operator fun invoke(planet: Planet, continueOnError: Boolean = false, technologies: List<Technology>): PlanetMaintenanceResult {
        val isCyberneticsResearched =
            isTechnologyResearched.invoke(TechnologyEnum.CYBERNETICS, technologies)
        val basicCost = calculatePlanetMaintenance(planet.level)
        var maintenanceCost =
            if (!isCyberneticsResearched) {
                basicCost
            } else {
                val cyberneticsLevel =
                    (technologies.find { it.type == TechnologyEnum.CYBERNETICS }
                            as Technology.MultiplyTechnology.Cybernetics).level
                val decreaseAmount = decreaseMaintenance.invoke(cyberneticsLevel)
                if (basicCost - decreaseAmount < 0) {
                    0
                } else {
                    basicCost - decreaseAmount
                }
            }
        val infrastructure = planet.infrastructure - maintenanceCost
        val biomass = planet.biomass.toInt()  - maintenanceCost
        val influence = planet.influence - maintenanceCost
        if (infrastructure < 0 || biomass < 0 || influence < 0) {
            val missingResources = mapOf(
                Resource.INFRASTRUCTURE to abs(infrastructure),
                Resource.BIOMASS to abs(biomass),
                Resource.INFLUENCE to abs(influence)
            )
            return if (continueOnError) {
                PlanetMaintenanceResult.FailureWithSuccess(
                    PlanetMaintenanceResult.Error(missingResources),
                    PlanetMaintenanceResult.Success(infrastructure, influence, biomass.toFloat())
                )
            } else {
                PlanetMaintenanceResult.Error(missingResources)
            }
        }
        return PlanetMaintenanceResult.Success(infrastructure, influence, biomass.toFloat())
    }
}