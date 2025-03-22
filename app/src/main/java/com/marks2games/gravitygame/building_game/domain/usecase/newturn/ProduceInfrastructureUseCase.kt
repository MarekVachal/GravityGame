package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.CHANGE_DISTRICT_MODE_COST
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult.Success
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min

/**
 * Use case responsible for calculating the growth of infrastructure on a planet.
 */
class ProduceInfrastructureUseCase @Inject constructor(
    private val maintenanceUseCase: PlanetMaintenanceUseCase,
    private val produceProgressUseCase: ProduceProgressUseCase,
){

    /**
     * Calculates the growth of infrastructure on a planet based on available metal and working industrial districts.
     *
     * Each working industrial district in INFRASTRUCTURE mode can contribute to infrastructure growth if there is enough metal.
     * Each industrial district consumes 5 metal to increase the infrastructure by 5.
     *
     * Player can set infrastructure production to two modes: USAGE and MAXIMUM.
     * In USAGE mode industrials districts produce just enough infrastructure to cover planet needs.
     * MAXIMUM mode produces all available infrastructure.
     *
     * @param planet The planet on which to grow infrastructure.
     * @return A pair containing the updated infrastructure level and remaining metal on the planet.
     */
    operator fun invoke(planet: Planet): ProduceInfraResult {
        val infraProducedByCapitol = District.Capitol().generateResources().produced[Resource.INFRASTRUCTURE] ?: 1
        var planetMetals = planet.planetMetal
        val maxProductionFromIndustrials = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.INFRASTRUCTURE && it.isWorking }
            .sumOf { it.generateResources().produced[Resource.INFRASTRUCTURE] ?: 1 }
        if(maxProductionFromIndustrials == 0){
            return if(planetMetals < infraProducedByCapitol){
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure
            } else {
                ProduceInfraResult.Success(infraProducedByCapitol, planet.metal, planetMetals - infraProducedByCapitol)
            }
        }
        val exampleDistrict = planet.districts
            .filterIsInstance<District.Industrial>()
            .firstOrNull() ?: return if(planetMetals < infraProducedByCapitol){
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure
            } else {
                ProduceInfraResult.Success(infraProducedByCapitol, planet.metal, planetMetals - infraProducedByCapitol)
            }

        val productionRate = exampleDistrict.generateResources().produced[Resource.INFRASTRUCTURE] ?: 1
        val consumptionRate = exampleDistrict.generateResources().consumed[Resource.METAL] ?: 1

        if(planet.infrastructureSetting == InfrastructureSetting.USAGE){
            val maxPossibleProduction = min((planet.metal / consumptionRate) * productionRate, maxProductionFromIndustrials)
            val updatedPlanet = planet.copy(
                infrastructure = maxPossibleProduction
            )
            val resultOfMaintenance = maintenanceUseCase.invoke(updatedPlanet)
            val newInfraAfterMaintenance = when(resultOfMaintenance){
                is PlanetMaintenanceResult.Error -> 0
                is Success -> resultOfMaintenance.infrastructure
            }
            val infraNeededForMaintenance = maxPossibleProduction - newInfraAfterMaintenance
            val secondUpdatedPlanet = updatedPlanet.copy(infrastructure = newInfraAfterMaintenance)
            val infraAfterProgress = produceProgressUseCase.invoke(secondUpdatedPlanet).second
            val infraNeededForProgress = newInfraAfterMaintenance - infraAfterProgress

            val infraForBuilding = planet.actions
                    .filterIsInstance<Action.DistrictAction.BuildDistrict>()
                    .count { it.district != DistrictEnum.EMPTY }
            val infraForModeChanging = planet.actions
                .filterIsInstance<Action.DistrictAction.ChangeDistrictMode>()
                .count()

            val infraNeeded =
                infraNeededForMaintenance +
                infraNeededForProgress +
                (infraForBuilding * DISTRICT_BUILD_COST) +
                (infraForModeChanging * CHANGE_DISTRICT_MODE_COST)

            val unitsNeeded = ceil((infraNeeded - infraProducedByCapitol).toFloat() / productionRate).toInt()
            val isEnoughPlanetMetal = planetMetals > (unitsNeeded * consumptionRate + infraProducedByCapitol)

            return if(!isEnoughPlanetMetal){
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure
            } else {
                ProduceInfraResult.Success(
                    newInfra = infraProducedByCapitol + (unitsNeeded * productionRate),
                    newMetal = planet.metal - (unitsNeeded * consumptionRate),
                    newPlanetMetal = planetMetals - (unitsNeeded * consumptionRate) - infraProducedByCapitol
                )
            }
        }

        val maxPossibleProduction = planet.metal / consumptionRate
        val availableProduction = min(maxPossibleProduction * productionRate, maxProductionFromIndustrials)
        val isEnoughPlanetMetal = planetMetals > (availableProduction + infraProducedByCapitol)

        return if(!isEnoughPlanetMetal){
            ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure
        } else {
            ProduceInfraResult.Success(
                newInfra = infraProducedByCapitol + availableProduction,
                newMetal = planet.metal - (availableProduction * consumptionRate),
                newPlanetMetal = planetMetals - (availableProduction * consumptionRate) - infraProducedByCapitol
            )
        }
    }
}