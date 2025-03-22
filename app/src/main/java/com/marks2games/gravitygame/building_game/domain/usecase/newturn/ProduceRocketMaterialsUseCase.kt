package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class ProduceRocketMaterialsUseCase @Inject constructor(
    private val produceArmyUnitUseCase: ProduceArmyUnitUseCase,
    private val produceExpeditionsUseCase: ProduceExpeditionsUseCase
) {
    operator fun invoke(planet: Planet): Triple <Int, Float, Float> {
        val activeRocketMaterialsFactories = planet.districts
            .filterIsInstance<District.Industrial>()
            .count { it.isWorking && it.mode == IndustrialMode.ROCKET_MATERIALS }
        if(activeRocketMaterialsFactories == 0 || planet.rocketMaterialsSetting == RocketMaterialsSetting.NOTHING){
            return Triple(planet.rocketMaterials, planet.biomass, planet.organicSediment)

        }

        val exampleDistrict = planet.districts
            .filterIsInstance<District.Industrial>()
            .firstOrNull() ?: return Triple(planet.rocketMaterials, planet.biomass, planet.organicSediment)

        val productionRate = exampleDistrict.generateResources().produced[Resource.ROCKET_MATERIALS] ?: 1
        val consumptionBiomassRate = exampleDistrict.generateResources().consumed[Resource.BIOMASS] ?: 1
        val consumptionSedimentsRate = exampleDistrict.generateResources().consumed[Resource.ORGANIC_SEDIMENTS] ?: 1

        if(planet.rocketMaterialsSetting == RocketMaterialsSetting.USAGE){
            val armyResult = produceArmyUnitUseCase.invoke(planet)
            val updatedPlanet = planet.copy(
                rocketMaterials = armyResult.second
            )
            val expeditions = produceExpeditionsUseCase.invoke(updatedPlanet).second
            val rocketMaterialsNeeded = planet.rocketMaterials - expeditions
            val minimalConsumedResource = min(planet.biomass / consumptionBiomassRate, planet.organicSediment / consumptionSedimentsRate)
            val maxPossibleProduction = min(rocketMaterialsNeeded * productionRate, floor(minimalConsumedResource).toInt() * productionRate)
            return Triple (maxPossibleProduction * productionRate, planet.biomass - (maxPossibleProduction * consumptionBiomassRate), planet.organicSediment - (maxPossibleProduction * consumptionSedimentsRate))
        }
        val minimalConsumedResource = min(planet.biomass / consumptionBiomassRate, planet.organicSediment / consumptionSedimentsRate)
        val maxPossibleProduction = min(activeRocketMaterialsFactories * productionRate, floor(minimalConsumedResource).toInt() * productionRate)
        return Triple (maxPossibleProduction * productionRate, planet.biomass - (maxPossibleProduction * consumptionBiomassRate), planet.organicSediment - (maxPossibleProduction * consumptionSedimentsRate))
    }

}