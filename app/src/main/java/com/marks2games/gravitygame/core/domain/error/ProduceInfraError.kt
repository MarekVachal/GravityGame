package com.marks2games.gravitygame.core.domain.error

sealed class ProduceInfraResult{
    data class Success(val newInfra: Int, val newMetal: Int, val newPlanetMetal: Int): ProduceInfraResult()
    sealed class Error: ProduceInfraResult(){
        object InsufficientPlanetMetalsForInfrastructure: Error()
    }

}