package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class UpdateTransportUiUseCase @Inject constructor() {
    operator fun invoke(
        isCostChange: Boolean,
        resource: Resource,
        transport: Transport,
        isForPlanet1: Boolean,
        isAdding: Boolean,
        modifiedEmpire: Empire?
    ): Empire? {
        if(modifiedEmpire != null){
            val planet1 = modifiedEmpire.planets.firstOrNull { it.id == transport.planet1Id }?: return modifiedEmpire
            val planet2 = modifiedEmpire.planets.firstOrNull { it.id == transport.planet2Id }?: return modifiedEmpire
            var updatedEmpire = modifiedEmpire

            val (exported, imported) = if (isForPlanet1) planet1 to planet2 else planet2 to planet1
            val d = delta(isAdding)
            val updatedExported = exported.updateStock(resource, -d)
            val updatedImported = if (!isCostChange) {
                imported.updatePossibleIncome(resource, +d)
            } else {
                imported
            }

            val newPlanets = modifiedEmpire.planets.map {
                when (it.id) {
                    updatedExported.id -> updatedExported
                    updatedImported.id -> updatedImported
                    else               -> it
                }
            }
            updatedEmpire = updatedEmpire.copy(planets = newPlanets)
            return updatedEmpire
        } else {
            return null
        }

    }
}

private fun delta(isAdding: Boolean) : Int{
    return if (isAdding) +1 else -1
}

private fun Map<Resource, Int>.withDelta(resource: Resource, d: Int) =
    toMutableMap().apply { this[resource] = (this[resource] ?: 0) + d }.toMap()

private fun Planet.updateStock(res: Resource, d: Int) = when (res) {
    Resource.METAL              -> copy(metal = metal + d)
    Resource.ORGANIC_SEDIMENTS  -> copy(organicSediment = organicSediment + d)
    Resource.ROCKET_MATERIALS   -> copy(rocketMaterials = rocketMaterials + d)
    else -> this
}

private fun Planet.updatePossibleIncome(res: Resource, d: Int): Planet {
    val newIncome = planetResourcesPossibleIncome.resources.withDelta(res, d)
    val newPossible = planetResourcesPossibleIncome.copy(resources = newIncome)
    return copy(planetResourcesPossibleIncome = newPossible)
}