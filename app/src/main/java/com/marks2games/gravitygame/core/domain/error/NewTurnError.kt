package com.marks2games.gravitygame.core.domain.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Resource
import androidx.compose.ui.res.stringResource

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
    LocalContext.current
    val planet = empire?.planets?.find { it.id == planetId } ?: return stringResource(R.string.unknown_planet)
    val planetName = planet.name

    @Composable
    fun missingResourcesString(missing: Map<Resource, Int>): String {
        return missing
            .filterValues { it < 0 }
            .map {
                stringResource(R.string.missing_resource, -it.value, stringResource(it.key.nameResIdGenitive))
            }
            .joinToString(", ")
    }

    return when (this) {
        is NewTurnError.BuildDistrictError -> {
            when (error) {
                is BuildDistrictResult.Error.CapitolNotAllowed ->
                    stringResource(R.string.errorNotAllowedSecondDistrict, stringResource(DistrictEnum.CAPITOL.nameIdNominative), planetName)

                is BuildDistrictResult.Error.ExpeditionPlatformExists ->
                    stringResource(R.string.errorNotAllowedSecondDistrict, stringResource(DistrictEnum.EXPEDITION_PLATFORM.nameIdNominative), planetName)

                is BuildDistrictResult.Error.DistrictNotFound ->
                    stringResource(R.string.error_district_not_found, planetName)

                is BuildDistrictResult.Error.UnnocupatedNotAllowed ->
                    stringResource(R.string.unnocupatedNotAllowed, stringResource(DistrictEnum.UNNOCUPATED.nameIdNominative), planetName)

                is BuildDistrictResult.Error.DistrictIsUnnocupated ->
                    stringResource(R.string.districtForBuildingIsUnnocupated, planetName)
            }
        }

        is NewTurnError.PlanetMaintenanceError -> {
            val missing = missingResourcesString(error.missingResources)
            stringResource(R.string.error_not_enough_resources_maintenance, planetName, missing)
        }

        is NewTurnError.ArmyMaintenanceError -> {
            stringResource(R.string.error_insufficient_resources_army, planetName)
        }

        is NewTurnError.TransportOutError -> {
            val missing = missingResourcesString(error.missingResources)
            stringResource(R.string.error_transport_resources, error.transportId, planetName, missing)
        }

        is NewTurnError.ChangeDistrictModeError -> {
            val districtType = empire.planets
                .find { it.id == planetId }
                ?.districts
                ?.find { it.districtId == districtId }
                ?.type ?: return stringResource(R.string.unknown_district)

            stringResource(
                R.string.error_insufficient_infra_district_mode,
                stringResource(R.string.infrastructureGenitive),
                stringResource(districtType.nameIdGenitive),
                planetName
            )
        }

        is NewTurnError.ProduceInfraError -> {
            when (error) {
                ProduceInfraResult.Error.InsufficientPlanetMetalsForInfrastructure ->
                    stringResource(R.string.error_insufficient_metals, stringResource(R.string.metalGenitive), stringResource(R.string.infrastructureAccusative), stringResource(DistrictEnum.CAPITOL.nameIdInstrumental), planetName)

                ProduceInfraResult.Error.NoIndustrialsProducingInfra ->
                    stringResource(R.string.error_no_industrials, stringResource(DistrictEnum.INDUSTRIAL.nameIdNominative), stringResource(R.string.infrastructureAccusative), planetName)

                is ProduceInfraResult.Error.MissingInfra ->
                    stringResource(R.string.error_missing_infra, error.lacking, stringResource(R.string.infrastructureGenitive), planetName)
            }
        }

        is NewTurnError.ProduceRocketMaterialsError -> {
            when (error) {
                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmy ->
                    stringResource(
                        R.string.errorLackingRocketMaterialsForOneProduction,
                        error.lacking,
                        stringResource(R.string.rocketMaterialsGenitive),
                        stringResource(R.string.armyProductionAccusative),
                        planetName
                    )

                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForArmyAndExpedition ->
                    stringResource(
                        R.string.errorLackingRocketMaterialsForBothProduction,
                        error.lackingForArmy,
                        stringResource(R.string.rocketMaterialsGenitive),
                        stringResource(R.string.armyProductionAccusative),
                        error.lackingForExpedition,
                        stringResource(R.string.expeditionProductionAccusative),
                        planetName
                    )

                is RocketMaterialsResult.Error.InsufficientRocketMaterialsForExpedition ->
                    stringResource(
                        R.string.errorLackingRocketMaterialsForOneProduction,
                        error.lacking,
                        stringResource(R.string.rocketMaterialsGenitive),
                        stringResource(R.string.expeditionProductionAccusative),
                        planetName
                    )
            }
        }

        is NewTurnError.ProduceProgressError -> {
            when (error) {
                is ProduceProgressResult.Error.InsufficientResources -> {
                    val missing = buildList {
                        if (error.missingBiomass > 0) add(
                            stringResource(
                                R.string.missing_resource,
                                error.missingBiomass,
                                stringResource(Resource.BIOMASS.nameResIdGenitive)
                            )
                        )
                        if (error.missingInfra > 0) add(
                            stringResource(
                                R.string.missing_resource,
                                error.missingInfra,
                                stringResource(Resource.INFRASTRUCTURE.nameResIdGenitive)
                            )
                        )
                    }.joinToString(", ")

                    stringResource(
                        R.string.error_progress_insufficient,
                        stringResource(R.string.progressProductionAccusative),
                        planetName,
                        missing
                    )
                }

                ProduceProgressResult.Error.MaximumLvlOfPlanet ->
                    stringResource(
                        R.string.error_progress_max_level,
                        stringResource(R.string.progress),
                        planetName
                    )
            }
        }
    }
}



