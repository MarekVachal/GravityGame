package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.CHANGE_DISTRICT_MODE_COST
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.ResourceChange
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetMaintenanceUseCase
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class ProduceInfrastructureUseCase @Inject constructor(
    private val maintenanceCost: CalculatePlanetMaintenanceUseCase
){
    operator fun invoke(planet: Planet, actions: List<Action>, isPlanning: Boolean): ProduceInfraResult {
        val capitolDistrict = planet.districts.filterIsInstance<District.Capitol>().first()
        val capitolResources = capitolDistrict.generateResources()
        val infraProducedByCapitol = capitolResources.produced[Resource.INFRASTRUCTURE] ?: 1
        val planetMetals = planet.planetMetal
        val maxProductionFromIndustrials = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.INFRASTRUCTURE && it.isWorking }
            .sumOf { it.generateResources().produced[Resource.INFRASTRUCTURE] ?: 1 }
        Log.d("ProduceInfra", "Max infra from industrials: $maxProductionFromIndustrials")

        if(maxProductionFromIndustrials == 0 || planet.infrastructureSetting == InfrastructureSetting.NOTHING){
            return if(planetMetals < infraProducedByCapitol){
                Log.d("ProduceInfra", "Insufficient planet metals for infra from Capitol only")
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure
            } else {
                Log.d("ProduceInfra", "Only Capitol produces infra: success")
                ProduceInfraResult.Success(infraProducedByCapitol, planet.metal, planetMetals - infraProducedByCapitol)
            }
        }

        val industrialDistrict = planet.districts
            .filterIsInstance<District.Industrial>()
            .firstOrNull { it.mode == IndustrialMode.INFRASTRUCTURE } ?: return ProduceInfraResult.Error.NoIndustrialsProducingInfra
        val industrialResources = industrialDistrict.generateResources()
        val productionRate = industrialResources.produced[Resource.INFRASTRUCTURE] ?: 1
        val consumptionRate = industrialResources.consumed[Resource.METAL] ?: 1


        val maxIndustrialsProduction = min((planet.metal / consumptionRate) * productionRate, maxProductionFromIndustrials)
        val maxProduction = maxIndustrialsProduction + infraProducedByCapitol
        Log.d("ProduceInfra", "maxIndustrialsProduction=$maxIndustrialsProduction, maxProduction=$maxProduction")

        val infraUsageResult = infraNeeded(planet, actions, maintenanceCost, capitolResources)
        val infraNeeded = infraUsageResult.first
        val totalNeededInfra = infraUsageResult.second

        Log.d("ProduceInfra", "Total infra needed: $totalNeededInfra (infraNeeded=$infraNeeded)")

        return if(planet.infrastructureSetting == InfrastructureSetting.USAGE){
            val realProduction = max(min(totalNeededInfra, maxProduction), infraProducedByCapitol)
            val metalConsumption = ((realProduction - infraProducedByCapitol) / productionRate) * consumptionRate
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
                ProduceInfraResult.FailureWihSuccess(
                    success = ProduceInfraResult.Success(
                        infraNeeded,
                        planet.metal - ((infraNeeded - infraProducedByCapitol) / productionRate) * consumptionRate,
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
            val metalUsed = ((maxIndustrialsProduction / productionRate) * consumptionRate)
            if(infraNeeded > maxProduction && isPlanning){
                Log.d("ProduceInfra", "Planning mode: Not enough infra, fallback success")
                ProduceInfraResult.FailureWihSuccess(
                    success = ProduceInfraResult.Success(
                        infraNeeded,
                        planet.metal - (((infraNeeded - infraProducedByCapitol) / productionRate) * consumptionRate),
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
    val infraForProgress = planet.progressSetting / progressProductionRate * progressInfraConsumptionRate
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