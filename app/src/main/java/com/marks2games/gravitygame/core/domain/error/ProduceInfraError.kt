package com.marks2games.gravitygame.core.domain.error

sealed class ProduceInfraResult{
    data class Success(val newInfra: Int, val newMetal: Int, val newPlanetMetal: Int): ProduceInfraResult()
    data class FailureWihSuccess(val success: Success, val error: Error): ProduceInfraResult()
    sealed class Error: ProduceInfraResult(){
        object InsufficientPlanetMetalsForInfrastructure: Error()
        object NoIndustrialsProducingInfra: Error()
        data class MissingInfra (val lacking: Int): Error()
    }

}