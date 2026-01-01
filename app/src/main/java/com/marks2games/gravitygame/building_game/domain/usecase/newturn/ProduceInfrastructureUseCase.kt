package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.CHANGE_DISTRICT_MODE_COST
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.ResourceChange
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetMaintenanceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.ceil
import kotlin.math.floor

data class ProductionWithBonus(val base: Int, val bonus: Int)

class ProduceInfrastructureUseCase @Inject constructor(
    private val maintenanceCost: CalculatePlanetMaintenanceUseCase,
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
){
    operator fun invoke(planet: Planet, actions: List<Action>, isPlanning: Boolean, technologies: List<Technology>): ProduceInfraResult {
        val capitolDistrict = planet.districts.filterIsInstance<District.Capitol>().first()
        val capitolResources = capitolDistrict.generateResources()
        val planetMetals = planet.planetMetal
        val possibleInfraProducedByCapitol = capitolResources.produced[Resource.INFRASTRUCTURE] ?: 0
        val infraProducedByCapitol = min(possibleInfraProducedByCapitol, planetMetals)
        Log.d("ProduceInfra", "Infra produced by capitol: $infraProducedByCapitol")
        val industrialDistricts = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.INFRASTRUCTURE && it.isWorking }
        if(industrialDistricts.isEmpty() || planet.infrastructureSetting == InfrastructureSetting.NOTHING){
            return ProduceInfraResult.Success(infraProducedByCapitol, planet.metal, planetMetals - infraProducedByCapitol)
        }
        val industrialProductions: List<ProductionWithBonus> = industrialDistricts.map { district ->
            val baseProduction = district.generateResources().produced[Resource.INFRASTRUCTURE] ?: 0
            val synergyBonus = applySynergy.invoke(
                resource = Resource.INFRASTRUCTURE,
                districtId = district.districtId,
                planet = planet,
                technology = TechnologyEnum.INDUSTRIAL_SYNERGY,
                technologies = technologies
            )
            ProductionWithBonus(baseProduction, synergyBonus)
        }

        val totalBaseProduction = industrialProductions.sumOf { it.base }


        val industrialDistrict = industrialDistricts.firstOrNull() ?: return ProduceInfraResult.Error.NoIndustrialsProducingInfra
        val industrialResources = industrialDistrict.generateResources()
        val productionRate = industrialResources.produced[Resource.INFRASTRUCTURE] ?: 0
        val consumptionRate = industrialResources.consumed[Resource.METAL] ?: 0

        val productionCappedByConsumedResource = min(totalBaseProduction, floor((planet.metal.toDouble() / consumptionRate) * productionRate).toInt())
        val totalSynergyBonus = industrialProductions.sumOf { it.bonus }
        val diversityCoefficient = applyDiversity.invoke(technologies, DistrictEnum.INDUSTRIAL, planet)
        val maxProduction = floor((productionCappedByConsumedResource + totalSynergyBonus) * diversityCoefficient).toInt() + infraProducedByCapitol

        val infraUsageResult = infraNeeded(planet, actions, maintenanceCost, capitolResources)
        val infraNeeded = infraUsageResult.first
        val totalNeededInfra = infraUsageResult.second

        Log.d("ProduceInfra", "Total infra needed: $totalNeededInfra (infraNeeded=$infraNeeded)")

        return if(planet.infrastructureSetting == InfrastructureSetting.USAGE){
            val realProduction = max(min(totalNeededInfra, maxProduction), infraProducedByCapitol)
            val baseProduction = floor(((realProduction/diversityCoefficient) - totalSynergyBonus) - infraProducedByCapitol).toInt()
            val cappedBaseProduction = if(realProduction == infraProducedByCapitol) {
                0
            } else if (baseProduction < 0){
                0
            } else {
                baseProduction
            }
            val metalConsumption = ceil((cappedBaseProduction.toDouble() / productionRate) * consumptionRate).toInt()
            Log.d("ProduceInfra", "USAGE setting: realProduction=$realProduction, metalConsumption=$metalConsumption")
            if(infraNeeded <= maxProduction){
                Log.d("ProduceInfra", "USAGE: All infra needs met")
                ProduceInfraResult.Success(
                    realProduction,
                    planet.metal - metalConsumption,
                    planetMetals - infraProducedByCapitol
                )
            } else if (isPlanning){
                Log.d("ProduceInfra", "USAGE: Planning mode, not enough infra")
                val cappedPlanningBase = (infraNeeded - infraProducedByCapitol + totalSynergyBonus).toFloat() / diversityCoefficient
                val planningConsumption = ((cappedPlanningBase / productionRate) * consumptionRate).toInt()
                ProduceInfraResult.FailureWihSuccess(
                    success = ProduceInfraResult.Success(
                        infraNeeded,
                        planet.metal - planningConsumption,
                        planetMetals - infraProducedByCapitol
                    ),
                    error = ProduceInfraResult.Error.MissingInfra(
                        infraNeeded - maxProduction
                    )
                )
            } else {
                Log.d("ProduceInfra", "USAGE: Not enough infra, but there is a new turn")
                ProduceInfraResult.Success(
                    realProduction,
                    planet.metal - metalConsumption,
                    planetMetals - infraProducedByCapitol
                )
            }
        } else {
            Log.d("ProduceInfra", "Maximum setting")
            val cappedBaseProduction = totalBaseProduction
            val metalUsed = ((cappedBaseProduction / productionRate) * consumptionRate).toInt()
            if(infraNeeded > maxProduction && isPlanning){
                Log.d("ProduceInfra", "Planning mode: Not enough infra, fallback success")
                val cappedPlanningBase = (infraNeeded - infraProducedByCapitol).toFloat()
                val planningConsumption = ceil((cappedPlanningBase / productionRate) * consumptionRate).toInt()
                ProduceInfraResult.FailureWihSuccess(
                    success = ProduceInfraResult.Success(
                        infraNeeded,
                        planet.metal - planningConsumption,
                        planetMetals - infraProducedByCapitol
                    ),
                    error = ProduceInfraResult.Error.MissingInfra(
                        infraNeeded - maxProduction
                    )
                )
            } else {
                Log.d("ProduceInfra", "Success with maxProduction=$maxProduction, metalUsed=$metalUsed")
                ProduceInfraResult.Success(
                    maxProduction,
                    planet.metal - metalUsed,
                    planetMetals - infraProducedByCapitol
                )
            }
        }
    }
}

private fun infraNeeded(
    planet: Planet,
    actions: List<Action>,
    maintenanceCost: CalculatePlanetMaintenanceUseCase,
    capitolResources: ResourceChange
): Pair<Int, Int> {
    val infraForMaintenance = maintenanceCost.invoke(planet.level)
    Log.d("ProduceInfra", "Infra needed for maintenance: $infraForMaintenance")
    val progressProductionRate = capitolResources.produced[Resource.PROGRESS] ?:0
    val progressInfraConsumptionRate = capitolResources.consumed[Resource.INFRASTRUCTURE] ?: 0
    val infraForProgress = (planet.progressSetting.toFloat() / progressProductionRate * progressInfraConsumptionRate).toInt()
    Log.d("ProduceInfra", "Infra for progress: $infraForProgress")

    var infraForBuilding = 0
    planet.districts
        .filterIsInstance<District.InConstruction>()
        .forEach { district ->
            val needed = DISTRICT_BUILD_COST - district.infra
            infraForBuilding += needed
            Log.d("ProduceInfra", "Infra needed for building ${district.type}: $needed")
        }
    infraForBuilding += actions.filterIsInstance<Action.DistrictAction.BuildDistrict>()
        .count() * DISTRICT_BUILD_COST

    val infraForModeChanging = actions
        .filterIsInstance<Action.DistrictAction.ChangeDistrictMode>()
        .count() * CHANGE_DISTRICT_MODE_COST
    Log.d("ProduceInfra", "Infra for mode changes: $infraForModeChanging")

    val infraNeeded =
        infraForMaintenance +
        infraForProgress +
        infraForModeChanging

    val totalNeededInfra = infraNeeded + infraForBuilding
    return Pair(infraNeeded, totalNeededInfra)
}