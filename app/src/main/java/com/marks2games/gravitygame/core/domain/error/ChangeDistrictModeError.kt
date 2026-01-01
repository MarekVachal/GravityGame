package com.marks2games.gravitygame.core.domain.error

import com.marks2games.gravitygame.building_game.data.model.District

sealed class ChangeDistrictModeResult{
    data class Success(val updatedInfrastructure: Int, val districts: List<District>) : ChangeDistrictModeResult()
    data class FailureWithSuccess(val error: Error, val success: Success): ChangeDistrictModeResult()
    sealed class Error : ChangeDistrictModeResult() {
        object InsufficientInfrastructureForModeChange : Error()
    }
}