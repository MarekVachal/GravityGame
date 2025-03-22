package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetMaintenanceUseCase
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult
import javax.inject.Inject

class PlanetMaintenanceUseCase @Inject constructor(
    private val calculatePlanetMaintenance: CalculatePlanetMaintenanceUseCase
){
    operator fun invoke(planet: Planet): PlanetMaintenanceResult{
        var maintenanceCost = calculatePlanetMaintenance(planet.level)
        if (planet.infrastructure < maintenanceCost || planet.biomass < maintenanceCost.toFloat() || planet.influence < maintenanceCost) {
            return PlanetMaintenanceResult.Error.InsufficientResourcesForPlanetMaintenance
        }
        val infrastructure = planet.infrastructure - maintenanceCost
        val biomass = planet.biomass  - maintenanceCost.toFloat()
        val influence = planet.influence - maintenanceCost

        return PlanetMaintenanceResult.Success(infrastructure, influence, biomass)
    }
}