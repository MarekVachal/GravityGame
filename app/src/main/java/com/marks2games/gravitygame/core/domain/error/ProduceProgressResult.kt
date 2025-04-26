package com.marks2games.gravitygame.core.domain.error

sealed class ProduceProgressResult{
    data class Success(val progress: Int, val infrastructure: Int, val biomass: Float): ProduceProgressResult()
    data class FailureWithSuccess(val success: Success, val error: Error): ProduceProgressResult()
    sealed class Error: ProduceProgressResult(){
        data class InsufficientResources(val missingInfra: Int, val missingBiomass: Int): Error()
        object MaximumLvlOfPlanet: Error()
    }

    companion object
}