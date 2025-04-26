package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class UpdateModifiedEmpireUseCase @Inject constructor() {
    operator fun invoke(empire: Empire, planet1Id: Int?, planet2Id: Int?): Empire {
        var planet1: Planet? = null
        var planet2: Planet? = null

        if(planet1Id != null){
            planet1 = empire.planets.firstOrNull { it.id == planet1Id }
            if(planet1 != null){
                val incomeOsPlanet1 = planet1.planetResourcesPossibleIncome.resources[Resource.ORGANIC_SEDIMENTS] ?: 0
                val incomeMetalPlanet1 = planet1.planetResourcesPossibleIncome.resources[Resource.METAL] ?: 0
                val incomeRocketMaterialsPlanet1 = planet1.planetResourcesPossibleIncome.resources[Resource.ROCKET_MATERIALS] ?: 0

                planet1 = planet1.copy(
                    organicSediment = planet1.organicSediment - incomeOsPlanet1,
                    metal = planet1.metal - incomeMetalPlanet1,
                    rocketMaterials = planet1.rocketMaterials - incomeRocketMaterialsPlanet1
                )
            }
        }

        if(planet2Id != null){
            planet2 = empire.planets.firstOrNull { it.id == planet2Id }
            if(planet2 != null){
                val incomeOsPlanet2 = planet2.planetResourcesPossibleIncome.resources[Resource.ORGANIC_SEDIMENTS] ?: 0
                val incomeMetalPlanet2 = planet2.planetResourcesPossibleIncome.resources[Resource.METAL] ?: 0
                val incomeRocketMaterialsPlanet2 = planet2.planetResourcesPossibleIncome.resources[Resource.ROCKET_MATERIALS] ?: 0

                planet2 = planet2.copy(
                    organicSediment = planet2.organicSediment - incomeOsPlanet2,
                    metal = planet2.metal - incomeMetalPlanet2,
                    rocketMaterials = planet2.rocketMaterials - incomeRocketMaterialsPlanet2
                )
            }
        }


        return if(planet2 != null && planet1 != null) {
            val updatedPlanets = empire.planets.map {
                if (it.id == planet1.id) {
                    planet1
                } else if (it.id == planet2.id) {
                    planet2
                } else {
                    it
                }
            }
            val updatedEmpire = empire.copy(
                planets = updatedPlanets
            )
            updatedEmpire
        } else if(planet2 == null && planet1 != null) {
            val updatedPlanets = empire.planets.map {
                if (it.id == planet1.id) {
                    planet1
                } else {
                    it
                }
            }
            val updatedEmpire = empire.copy(
                planets = updatedPlanets
            )
            updatedEmpire
        } else if(planet2 != null){
            val updatedPlanets = empire.planets.map {
                if (it.id == planet2.id) {
                    planet2
                } else {
                    it
                }
            }
            val updatedEmpire = empire.copy(
                planets = updatedPlanets
            )
            updatedEmpire
        } else {
            empire
        }
    }
}