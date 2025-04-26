package com.marks2games.gravitygame.core.domain.error

import com.marks2games.gravitygame.building_game.data.model.Resource

sealed class PlanetMaintenanceResult{
    data class Success(val infrastructure: Int, val influence: Int, val biomass: Float): PlanetMaintenanceResult()
    data class FailureWithSuccess(val error: Error, val success: Success): PlanetMaintenanceResult()
    data class Error (val missingResources: Map<Resource, Int>): PlanetMaintenanceResult()
}