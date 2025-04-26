package com.marks2games.gravitygame.core.domain.error

import androidx.compose.runtime.Composable
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Resource

sealed class NewTurnError(
    open val planetId: Int
){
    data class BuildDistrictError(override val planetId: Int, val error: BuildDistrictResult.Error) : NewTurnError(planetId)
    data class PlanetMaintenanceError(override val planetId: Int, val error: PlanetMaintenanceResult.Error) : NewTurnError(planetId)
    data class ArmyMaintenanceError(override val planetId: Int, val error: ArmyMaintenanceResult.Error) : NewTurnError(planetId)
    data class TransportOutError(override val planetId: Int, val error: TransportOutResult.Error) : NewTurnError (planetId)
    data class ChangeDistrictModeError(override val planetId: Int, val districtId: Int, val error: ChangeDistrictModeResult.Error) : NewTurnError(planetId)
    data class ProduceInfraError(override val planetId: Int, val error: ProduceInfraResult.Error) : NewTurnError(planetId)
    data class ProduceRocketMaterialsError(override val planetId: Int, val error: RocketMaterialsResult.Error): NewTurnError(planetId)
    data class ProduceProgressError(override val planetId: Int, val error: ProduceProgressResult.Error): NewTurnError(planetId)
}

@Composable
fun NewTurnError.displayError(empire: Empire): String {
    val planet = empire.planets.find { it.id == planetId } ?: return "Unknown planet"
    val planetName = planet.name
    var string = ""
    when(this){
        is NewTurnError.BuildDistrictError -> {
            string = when(error){
                is BuildDistrictResult.Error.CapitolNotAllowed -> "Cannot build second Capitol on Planet $planetName"
                is BuildDistrictResult.Error.ExpeditionPlatformExists -> "Cannot build second expedition platform on Planet $planetName"
                is BuildDistrictResult.Error.DistrictNotFound -> "District where to build not found on Planet $planetName"
            }
        }
        is NewTurnError.PlanetMaintenanceError -> {
            val missingInfra = error.missingResources[Resource.INFRASTRUCTURE]?: 0
            val missingBiomass = error.missingResources[Resource.BIOMASS]?: 0
            val missingInfluence = error.missingResources[Resource.INFLUENCE]?: 0
            string = buildString {
                "Not enough resources for planet maintenance on planet $planetName: "
                if(missingInfra < 0) append("missing $missingInfra infrastructure")
                if(missingBiomass < 0){
                    if(missingInfra < 0) append(", ")
                    append("missing $missingBiomass biomass")
                }
                if(missingInfluence < 0){
                    if(missingInfra < 0 || missingBiomass < 0) append (", ")
                    append("missing $missingInfluence influence")
                }
                append(".")
            }
        }
        is NewTurnError.ArmyMaintenanceError -> {
            string = when(error){
                is ArmyMaintenanceResult.Error.InsufficientResourcesForArmyMaintenance -> "Insufficient resources for army maintenance on Planet $planetName"
            }
        }
        is NewTurnError.TransportOutError -> {
            val missingMetal = error.missingResources[Resource.METAL]
            val missingOrganic = error.missingResources[Resource.ORGANIC_SEDIMENTS]
            val missingRocket = error.missingResources[Resource.ROCKET_MATERIALS]
            string = buildString {
                "Not enough resources for transport id ${error.transportId} on planet $planetName:  "
                if (missingMetal != null) append("missing $missingMetal metal")
                if (missingOrganic != null) {
                    if (missingMetal != null) append(", ")
                    append("missing $missingOrganic organic sediments")
                }
                if (missingRocket != null) {
                    if (missingMetal != null || missingOrganic != null) append(", ")
                    append("missing $missingRocket rocket materials")
                }
                append(".")
            }
        }
        is NewTurnError.ChangeDistrictModeError -> {
            when(error){
                ChangeDistrictModeResult.Error.InsufficientInfrastructureForModeChange -> {
                    val district = empire.planets.find { it.id == planetId }?.districts?.find { it.districtId == districtId }?.type ?: return "Unknown district"
                    string = "Insufficient infrastructure for changing mode of $district on Planet $planetName"
                }
            }
        }
        is NewTurnError.ProduceInfraError -> {
            string = when(error){
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure -> "Insufficient planet resources of metal for infrastructure produced by Capitol on planet $planetName"
                ProduceInfraResult.Error.NoIndustrialsProducingInfra -> "No industrial districts for infrastructure on planet $planetName"
                is ProduceInfraResult.Error.MissingInfra -> "Missing ${error.lacking} infrastructure on planet $planetName"
            }
        }
        is NewTurnError.ProduceRocketMaterialsError -> {
            string = when (error){
                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmy -> "Lacking ${error.lacking} rocket materials for army production on planet $planetName"
                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmyAndExpedition -> "Lacking ${error.lackingForArmy} rocket materials for army and ${error.lackingForExpedition} for expedition production on planet $planetName"
                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForExpedition -> "Lacking ${error.lacking} rocket materials for expedition production on planet $planetName"
            }
        }

        is NewTurnError.ProduceProgressError -> {
            string = when(error){
                is ProduceProgressResult.Error.InsufficientResources -> {
                    buildString {
                        append("Insufficient resources for Progress on planet: $planetName")
                        if (error.missingBiomass > 0) {
                            append(", missing ${error.missingBiomass} biomass")
                        }
                        if (error.missingInfra > 0) {
                            append(", missing ${error.missingInfra} infrastructure")
                        }
                    }
                }
                ProduceProgressResult.Error.MaximumLvlOfPlanet -> "Maximum level of planet $planetName has been reached. No need to generate progress points."
            }

        }
    }
    return string
}


