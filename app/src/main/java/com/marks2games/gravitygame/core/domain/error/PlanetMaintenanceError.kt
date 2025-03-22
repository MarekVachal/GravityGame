package com.marks2games.gravitygame.core.domain.error

sealed class PlanetMaintenanceResult{
    data class Success(val infrastructure: Int, val influence: Int, val biomass: Float): PlanetMaintenanceResult()
    sealed class Error: PlanetMaintenanceResult(){
        object InsufficientResourcesForPlanetMaintenance: Error()
    }

    companion object{
        fun Error.toMap(): Map<String, Any> {
            return when (this) {
                Error.InsufficientResourcesForPlanetMaintenance -> mapOf("type" to "InsufficientResourcesForPlanetMaintenance")
            }
        }
        fun planetMaintenanceErrorFomMap(map: Map<String, Any>): Error? {
            return when (map["type"]) {
                "InsufficientResourcesForPlanetMaintenance" -> Error.InsufficientResourcesForPlanetMaintenance
                else -> null
            }
        }
    }

}