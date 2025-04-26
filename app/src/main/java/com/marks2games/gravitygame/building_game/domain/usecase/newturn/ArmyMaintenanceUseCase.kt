package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.core.domain.error.ArmyMaintenanceResult
import javax.inject.Inject

class ArmyMaintenanceUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, continueOnError: Boolean = false): ArmyMaintenanceResult {
        if (planet.influence < planet.army) {
            return if (continueOnError) {
                ArmyMaintenanceResult.FailureWithSuccess(
                    ArmyMaintenanceResult.Error.InsufficientResourcesForArmyMaintenance,
                    ArmyMaintenanceResult.Success(planet.influence - planet.army)
                )
            } else {
                ArmyMaintenanceResult.Error.InsufficientResourcesForArmyMaintenance
            }
        }
        return ArmyMaintenanceResult.Success(planet.influence - planet.army)
    }
}