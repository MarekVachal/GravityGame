package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import com.marks2games.gravitygame.core.domain.error.RocketMaterialsResult
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class ProduceRocketMaterialsUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
) {
    operator fun invoke(planet: Planet, technologies: List<Technology>): RocketMaterialsResult {
        val rocketMaterialsDistricts = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.isWorking && it.mode == IndustrialMode.ROCKET_MATERIALS }
        if(rocketMaterialsDistricts.isEmpty() || planet.rocketMaterialsSetting == RocketMaterialsSetting.NOTHING){
            return RocketMaterialsResult.Success(planet.rocketMaterials, planet.metal, planet.organicSediment)
        }
        val exampleDistrict = rocketMaterialsDistricts
            .firstOrNull() ?: return RocketMaterialsResult.Success(planet.rocketMaterials, planet.metal, planet.organicSediment)
        val exampleResource = exampleDistrict.generateResources()
        val productionRate = exampleResource.produced[Resource.ROCKET_MATERIALS] ?: 0
        val consumptionMetalRate = exampleResource.consumed[Resource.METAL] ?: 0
        val consumptionSedimentsRate = exampleResource.consumed[Resource.ORGANIC_SEDIMENTS] ?: 0
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
            val productionRateForArmy = expeditionResource.produced[Resource.ARMY]?: 0
            val consumptionRateForArmy = expeditionResource.consumed[Resource.ARMY]?: 0
            val productionRateForExpedition = expeditionResource.produced[Resource.EXPEDITIONS]?: 0
            val consumptionRateForExpedition = expeditionResource.consumed[Resource.EXPEDITIONS]?: 0
            rocketMaterialsForArmy = armyUnitsWannaProduce/productionRateForArmy * consumptionRateForArmy
            rocketMaterialsForExpedition = expeditionsWannaProduce/productionRateForExpedition * consumptionRateForExpedition
            rocketMaterialsNeeded = rocketMaterialsForArmy + rocketMaterialsForExpedition
        }

        val minimalConsumedResource = min(
            floor(planet.metal.toFloat() / consumptionMetalRate).toInt(),
            floor(planet.organicSediment / consumptionSedimentsRate).toInt()
        )
        Log.d("RocketMaterials", "minimalConsumedResource: $minimalConsumedResource")

        val industrialProductions: List<ProductionWithBonus> = rocketMaterialsDistricts.map{ district ->
            val baseProduction = district.generateResources().produced[Resource.ROCKET_MATERIALS] ?: 0
            val synergyBonus = applySynergy.invoke(
                resource = Resource.ROCKET_MATERIALS,
                districtId = district.districtId,
                planet = planet,
                technology = TechnologyEnum.INDUSTRIAL_SYNERGY,
                technologies = technologies
            )
            ProductionWithBonus(baseProduction, synergyBonus)
        }
        val baseProduction = industrialProductions.sumOf { it.base }
        val totalSynergyBonus = industrialProductions.sumOf { it.bonus }
        val diversityCoefficient = applyDiversity.invoke(technologies, DistrictEnum.INDUSTRIAL, planet)

        val totalBaseProduction = min(
                baseProduction,
                minimalConsumedResource * productionRate
        )

        val finalProduction = floor((totalBaseProduction + totalSynergyBonus).toFloat() * diversityCoefficient).toInt()
        val lackingForArmy = planet.rocketMaterials + finalProduction - rocketMaterialsForArmy
        val lackingForExpedition = lackingForArmy - rocketMaterialsForExpedition

        var consumedMetal = ceil(totalBaseProduction.toFloat()/productionRate * consumptionMetalRate).toInt()
        var consumedBiomass = totalBaseProduction.toFloat()/productionRate * consumptionSedimentsRate

        when{
            lackingForArmy < 0 && lackingForExpedition < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmyAndExpedition(
                    lackingForArmy = lackingForArmy, lackingForExpedition = lackingForExpedition),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + finalProduction,
                    updatedMetal = planet.metal - consumedMetal,
                    updatedOrganicSediments = planet.organicSediment - consumedBiomass
                )
            )
            lackingForArmy < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmy(lackingForArmy),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + finalProduction,
                    updatedMetal = planet.metal - consumedMetal,
                    updatedOrganicSediments = planet.organicSediment - consumedBiomass
                )
            )
            lackingForExpedition < 0 -> return RocketMaterialsResult.FailureWithSuccess(
                error = RocketMaterialsResult.Error.InsufficientRocketMaterialsForExpedition(lackingForExpedition),
                success = RocketMaterialsResult.Success(
                    updatedRocketMaterials = planet.rocketMaterials + finalProduction,
                    updatedMetal = planet.metal - consumedMetal,
                    updatedOrganicSediments = planet.organicSediment - consumedBiomass
                )
            )
        }

        var finalCappedProduction = finalProduction

        if (planet.rocketMaterialsSetting == RocketMaterialsSetting.USAGE) {
            val neededForUsage = max(0, rocketMaterialsNeeded - planet.rocketMaterials)
            finalCappedProduction = min(finalCappedProduction, neededForUsage)
        }
        var finalCappedBaseProduction = floor((finalCappedProduction.toFloat()/diversityCoefficient) - totalSynergyBonus)
        finalCappedBaseProduction = if (finalCappedBaseProduction < 0f) 0f else finalCappedBaseProduction

        consumedMetal = ceil(finalCappedBaseProduction/productionRate * consumptionMetalRate).toInt()
        consumedBiomass = finalCappedBaseProduction/productionRate * consumptionSedimentsRate

        return RocketMaterialsResult.Success(
            updatedRocketMaterials = planet.rocketMaterials + finalCappedProduction,
            updatedMetal = planet.metal - consumedMetal,
            updatedOrganicSediments = planet.organicSediment - consumedBiomass
        )
    }
}