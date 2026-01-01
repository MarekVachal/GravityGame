package com.marks2games.gravitygame.core.domain.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
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
fun NewTurnError.displayError(empire: Empire?): String {
    val context = LocalContext.current
    val planet = empire?.planets?.find { it.id == planetId } ?: return context.getString(R.string.unknown_planet)
    val planetName = planet.name

    fun missingResourcesString(missing: Map<Resource, Int>): String {
        return missing
            .filterValues { it < 0 }
            .map {
                context.getString(R.string.missing_resource, -it.value, context.getString(it.key.nameResIdGenitive))
            }
            .joinToString(", ")
    }

    return when (this) {
        is NewTurnError.BuildDistrictError -> {
            when (error) {
                is BuildDistrictResult.Error.CapitolNotAllowed ->
                    context.getString(R.string.errorNotAllowedSecondDistrict, context.getString(DistrictEnum.CAPITOL.nameIdNominative), planetName)

                is BuildDistrictResult.Error.ExpeditionPlatformExists ->
                    context.getString(R.string.errorNotAllowedSecondDistrict, context.getString(DistrictEnum.EXPEDITION_PLATFORM.nameIdNominative), planetName)

                is BuildDistrictResult.Error.DistrictNotFound ->
                    context.getString(R.string.error_district_not_found, planetName)

                is BuildDistrictResult.Error.UnnocupatedNotAllowed ->
                    context.getString(R.string.unnocupatedNotAllowed, context.getString(DistrictEnum.UNNOCUPATED.nameIdNominative), planetName)

                is BuildDistrictResult.Error.DistrictIsUnnocupated ->
                    context.getString(R.string.districtForBuildingIsUnnocupated, planetName)
            }
        }

        is NewTurnError.PlanetMaintenanceError -> {
            val missing = missingResourcesString(error.missingResources)
            context.getString(R.string.error_not_enough_resources_maintenance, planetName, missing)
        }

        is NewTurnError.ArmyMaintenanceError -> {
            context.getString(R.string.error_insufficient_resources_army, planetName)
        }

        is NewTurnError.TransportOutError -> {
            val missing = missingResourcesString(error.missingResources)
            context.getString(R.string.error_transport_resources, error.transportId, planetName, missing)
        }

        is NewTurnError.ChangeDistrictModeError -> {
            val districtType = empire.planets
                .find { it.id == planetId }
                ?.districts
                ?.find { it.districtId == districtId }
                ?.type ?: return context.getString(R.string.unknown_district)

            context.getString(
                R.string.error_insufficient_infra_district_mode,
                context.getString(R.string.infrastructureGenitive),
                context.getString(districtType.nameIdGenitive),
                planetName
            )
        }

        is NewTurnError.ProduceInfraError -> {
            when (error) {
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure ->
                    context.getString(R.string.error_insufficient_metals, context.getString(R.string.metalGenitive), context.getString(R.string.infrastructureAccusative), context.getString(DistrictEnum.CAPITOL.nameIdInstrumental), planetName)

                ProduceInfraResult.Error.NoIndustrialsProducingInfra ->
                    context.getString(R.string.error_no_industrials, context.getString(DistrictEnum.INDUSTRIAL.nameIdNominative), context.getString(R.string.infrastructureAccusative), planetName)

                is ProduceInfraResult.Error.MissingInfra ->
                    context.getString(R.string.error_missing_infra, error.lacking, context.getString(R.string.infrastructureGenitive), planetName)
            }
        }

        is NewTurnError.ProduceRocketMaterialsError -> {
            when (error) {
                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmy ->
                    context.getString(
                        R.string.errorLackingRocketMaterialsForOneProduction,
                        error.lacking,
                        context.getString(R.string.rocketMaterialsGenitive),
                        context.getString(R.string.armyProductionAccusative),
                        planetName
                    )

                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmyAndExpedition ->
                    context.getString(
                        R.string.errorLackingRocketMaterialsForBothProduction,
                        error.lackingForArmy,
                        context.getString(R.string.rocketMaterialsGenitive),
                        context.getString(R.string.armyProductionAccusative),
                        error.lackingForExpedition,
                        context.getString(R.string.expeditionProductionAccusative),
                        planetName
                    )

                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForExpedition ->
                    context.getString(
                        R.string.errorLackingRocketMaterialsForOneProduction,
                        error.lacking,
                        context.getString(R.string.rocketMaterialsGenitive),
                        context.getString(R.string.expeditionProductionAccusative),
                        planetName
                    )
            }
        }

        is NewTurnError.ProduceProgressError -> {
            when (error) {
                is ProduceProgressResult.Error.InsufficientResources -> {
                    val missing = buildList {
                        if (error.missingBiomass > 0) add(
                            context.getString(
                                R.string.missing_resource,
                                error.missingBiomass,
                                context.getString(Resource.BIOMASS.nameResIdGenitive)
                            )
                        )
                        if (error.missingInfra > 0) add(
                            context.getString(
                                R.string.missing_resource,
                                error.missingInfra,
                                context.getString(Resource.INFRASTRUCTURE.nameResIdGenitive)
                            )
                        )
                    }.joinToString(", ")

                    context.getString(
                        R.string.error_progress_insufficient,
                        context.getString(R.string.progressProductionAccusative),
                        planetName,
                        missing
                    )
                }

                ProduceProgressResult.Error.MaximumLvlOfPlanet ->
                    context.getString(
                        R.string.error_progress_max_level,
                        context.getString(R.string.progress),
                        planetName
                    )
            }
        }
    }
}



