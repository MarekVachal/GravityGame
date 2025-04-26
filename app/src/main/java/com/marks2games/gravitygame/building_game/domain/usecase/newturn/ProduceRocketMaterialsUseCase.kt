package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.core.domain.error.RocketMaterialsResult
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class ProduceRocketMaterialsUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): RocketMaterialsResult {
        val activeRocketMaterialsFactories = planet.districts
            .filterIsInstance<District.Industrial>()
            .count { it.isWorking && it.mode == IndustrialMode.ROCKET_MATERIALS }
        if(activeRocketMaterialsFactories == 0 || planet.rocketMaterialsSetting == RocketMaterialsSetting.NOTHING){
            return RocketMaterialsResult.Success(planet.rocketMaterials, planet.metal, planet.organicSediment)

        }

        val exampleDistrict = planet.districts
            .filterIsInstance<District.Industrial>()
            .firstOrNull{ it.mode == IndustrialMode.ROCKET_MATERIALS} ?: return RocketMaterialsResult.Success(planet.rocketMaterials, planet.metal, planet.organicSediment)

        val exampleResource = exampleDistrict.generateResources()
        val productionRate = exampleResource.produced[Resource.ROCKET_MATERIALS] ?: 1
        val consumptionMetalRate = exampleResource.consumed[Resource.METAL] ?: 1
        val consumptionSedimentsRate = exampleResource.consumed[Resource.ORGANIC_SEDIMENTS] ?: 1
        val armyUnitsWannaProduce = planet.armyConstructionSetting
        val expeditionsWannaProduce = planet.expeditionsSetting
        val expeditionDistrict = planet.districts
            .filterIsInstance<District.ExpeditionPlatform>()
            .firstOrNull()
        var rocketMaterialsNeeded = 0
        var rocketMaterialsForArmy = 0
        var rocketMaterialsForExpedition = 0
        if(expeditionDistrict != null){
            val expeditionResource = expeditionDistrict.generateResources()
            val productionRateForArmy = expeditionResource.produced[Resource.ARMY]?: 1
            val consumptionRateForArmy = expeditionResource.consumed[Resource.ARMY]?: 1
            val productionRateForExpedition = expeditionResource.produced[Resource.EXPEDITIONS]?: 1
            val consumptionRateForExpedition = expeditionResource.consumed[Resource.EXPEDITIONS]?: 1
            rocketMaterialsForArmy = armyUnitsWannaProduce/productionRateForArmy * consumptionRateForArmy
            rocketMaterialsForExpedition = expeditionsWannaProduce/productionRateForExpedition * consumptionRateForExpedition
            rocketMaterialsNeeded = rocketMaterialsForArmy + rocketMaterialsForExpedition
        }
        val minimalConsumedResource = min(planet.metal / consumptionMetalRate, floor(planet.organicSediment).toInt() / consumptionSedimentsRate)
        Log.d("RocketMaterials", "minimalConsumedResource: $minimalConsumedResource")
        val maxPossibleProduction = min(activeRocketMaterialsFactories * productionRate, minimalConsumedResource * productionRate)
        Log.d("RocketMaterials", "maxPossibleProduction: $maxPossibleProduction")
        val lackingForArmy = planet.rocketMaterials + maxPossibleProduction - rocketMaterialsForArmy
        val lackingForExpedition = lackingForArmy - rocketMaterialsForExpedition
        when{
            lackingForArmy < 0 && lackingForExpedition < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmyAndExpedition(
                    lackingForArmy = lackingForArmy, lackingForExpedition = lackingForExpedition),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + maxPossibleProduction,
                    updatedMetal = planet.metal - (maxPossibleProduction/productionRate * consumptionMetalRate),
                    updatedOrganicSediments = planet.organicSediment - (maxPossibleProduction/productionRate * consumptionSedimentsRate)
                )
            )
            lackingForArmy < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmy(lackingForArmy),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + maxPossibleProduction,
                    updatedMetal = planet.metal - (maxPossibleProduction/productionRate * consumptionMetalRate),
                    updatedOrganicSediments = planet.organicSediment - (maxPossibleProduction/productionRate * consumptionSedimentsRate)
                )
            )
            lackingForExpedition < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForExpedition(lackingForExpedition),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + maxPossibleProduction,
                    updatedMetal = planet.metal - (maxPossibleProduction/productionRate * consumptionMetalRate),
                    updatedOrganicSediments = planet.organicSediment - (maxPossibleProduction/productionRate * consumptionSedimentsRate)
                )
            )
        }

        var finalProduction = maxPossibleProduction
        Log.d("RocketMaterials", "finalProduction: $finalProduction")

        if (planet.rocketMaterialsSetting == RocketMaterialsSetting.USAGE) {
            val neededForUsage = max(0, rocketMaterialsNeeded - planet.rocketMaterials)
            finalProduction = min(finalProduction, neededForUsage)
        }

        return RocketMaterialsResult.Success(
            updatedRocketMaterials = planet.rocketMaterials + finalProduction,
            updatedMetal = planet.metal - (finalProduction/productionRate * consumptionSedimentsRate),
            updatedOrganicSediments = planet.organicSediment - (finalProduction/productionRate * consumptionSedimentsRate)
        )
    }
}