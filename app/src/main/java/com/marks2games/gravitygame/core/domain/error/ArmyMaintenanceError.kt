package com.marks2games.gravitygame.core.domain.error

sealed class ArmyMaintenanceResult{
    data class Success(val influence: Int): ArmyMaintenanceResult()
    data class FailureWithSuccess(val error: Error, val success: Success) : ArmyMaintenanceResult()
    sealed class Error: ArmyMaintenanceResult(){
        object InsufficientResourcesForArmyMaintenance: Error()
    }
}