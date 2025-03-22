package com.marks2games.gravitygame.core.domain.error

sealed class NewTurnError{
    data class BuildDistrictError(val planetId: Int, val error: BuildDistrictResult.Error) : NewTurnError()
    data class PlanetMaintenanceError(val planetId: Int, val error: PlanetMaintenanceResult.Error) : NewTurnError()
    data class ArmyMaintenanceError(val planetId: Int, val error: ArmyMaintenanceResult.Error) : NewTurnError()
    data class TransportOutError(val planetId: Int, val error: TransportOutResult.Error) : NewTurnError ()
    data class ChangeDistrictModeError(val planetId: Int, val error: ChangeDistrictModeResult.Error) : NewTurnError()
    data class ProduceInfraError(val planetId: Int, val error: ProduceInfraResult.Error) : NewTurnError()
}

