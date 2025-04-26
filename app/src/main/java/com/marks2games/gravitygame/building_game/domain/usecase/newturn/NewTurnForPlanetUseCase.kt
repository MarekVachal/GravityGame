package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateMetalCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateOrganicSedimentsCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateBorderForPlanetGrowth
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateRocketMaterialCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.PlanetGrowthUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CloseDistrictIsWorkingUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.DegradePlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.DestroyArmyUnitUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.UpdatePossibleEmpireResourcesIncomeUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.UpdatePossiblePlanetResourcesIncomeUseCase
import com.marks2games.gravitygame.core.domain.error.ArmyMaintenanceResult
import com.marks2games.gravitygame.core.domain.error.BuildDistrictResult
import com.marks2games.gravitygame.core.domain.error.ChangeDistrictModeResult
import com.marks2games.gravitygame.core.domain.error.NewTurnError
import com.marks2games.gravitygame.core.domain.error.NewTurnError.*
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.min
/*
class NewTurnForPlanetUseCase @Inject constructor(
    private val planetGrowthProgress: PlanetGrowthUseCase,
    private val generateBiomassUseCase: GenerateBiomassUseCase,
    private val generateInfluenceUseCase: GenerateInfluenceUseCase,
    private val generateDevelopmentUsaCase: GenerateDevelopmentUseCase,
    private val produceExpeditionsUseCase: ProduceExpeditionsUseCase,
    private val produceProgressUseCase: ProduceProgressUseCase,
    private val generateTradepowerUseCase: GenerateTradepowerUseCase,
    private val calculateMetalCapacityUseCase: CalculateMetalCapacityUseCase,
    private val generateMetalUseCase: GenerateMetalUseCase,
    private val calculateOrganicSedimentsCapacityUseCase: CalculateOrganicSedimentsCapacityUseCase,
    private val generatePlanetOrganicSedimentsUseCase: GeneratePlanetOrganicSedimentsUseCase,
    private val produceResearchUseCase: ProduceResearchUseCase,
    private val produceArmyUnitUseCase: ProduceArmyUnitUseCase,
    private val planetMaintenanceUseCase: PlanetMaintenanceUseCase,
    private val degradePlanetUseCase: DegradePlanetUseCase,
    private val armyMaintenanceUseCase: ArmyMaintenanceUseCase,
    private val destroyArmyUnitUseCase: DestroyArmyUnitUseCase,
    private val produceInfrastructureUseCase: ProduceInfrastructureUseCase,
    private val calculateRocketMaterialCapacityUseCase: CalculateRocketMaterialCapacityUseCase,
    private val produceRocketMaterialsUseCase: ProduceRocketMaterialsUseCase,
    private val closeDistrictIsWorkingUseCase: CloseDistrictIsWorkingUseCase,
    private val setResourcesProduceUseCase: SetResourcesProduceUseCase,
    private val transportOutUseCase: TransportOutUseCase,
    private val transportInUseCase: TransportInUseCase,
    private val buildDistrictUseCase: BuildDistrictUseCase,
    private val changeDistrictModeUseCase: ChangeDistrictModeUseCase,
    private val destroyDistrictUseCase: DestroyDistrictUseCase,
    private val generateOrganicSedimentsUseCase: GenerateOrganicSedimentsUseCase,
    private val finishDistrictConstructionUseCase: FinishDistrictConstructionUseCase,
    private val updatePossiblePlanetResourcesIncomeUseCase: UpdatePossiblePlanetResourcesIncomeUseCase,
    private val updatePossibleEmpireResourcesIncomeUseCase: UpdatePossibleEmpireResourcesIncomeUseCase,
    private val calculateBorderForPlanetGrowth: CalculateBorderForPlanetGrowth
) {
    operator fun invoke(
        empire: Empire,
        errors: List<NewTurnError>,
        planetId: Int
    ): Pair<Empire, List<NewTurnError>> {
        Log.d("NewTurnForPlanet", "Starting new turn for planet: $planetId")
        val newErrors = errors.toMutableList()
        newErrors.removeAll(newErrors.filter { error -> error.planetId == planetId })
        var expeditions = empire.expeditions
        var tradepower = empire.tradePower
        var research = empire.research
        var scheduledTransports = empire.transports.toMutableList()
        val updatedTransports = empire.transports.toMutableList()
        val initialPlanet = empire.planets.firstOrNull { it.id == planetId } ?: return Pair(empire, newErrors)
        var updatedPlanet = initialPlanet
        Log.d("NewTurnForPlanet", "Initial planet state: $updatedPlanet")
        val planetActions = empire.actions.filter { it.planetId == planetId }
        Log.d("NewTurnForPlanet", "Actions: $planetActions")


        //TransportsOut
        planetActions.forEach { action ->
            when (action) {
                is Action.TransportAction -> {
                    scheduledTransports.add(action.transport)
                }

                else -> println("Don't need")
            }
        }


        val transportResult = transportOutUseCase.invoke(scheduledTransports, empire.planets)
        scheduledTransports = transportResult.second.toMutableList()
        newErrors.addAll(transportResult.third)
        updatedPlanet = transportResult.first.firstOrNull { it.id == planetId } ?: return Pair(
            empire,
            newErrors
        )


        //1 Close districts
        updatedPlanet = updatedPlanet.copy(
            districts = closeDistrictIsWorkingUseCase.invoke(updatedPlanet, planetActions)
        )

        //2 Transport out - viz up

        //3 Set resources production by user
        updatedPlanet = setResourcesProduceUseCase.invoke(updatedPlanet, planetActions)
        Log.d("NewTurnForPlanet", "After setting resource production: $updatedPlanet")

        //4 Generate OS
        updatedPlanet = updatedPlanet.copy(
            planetOrganicSediments = generatePlanetOrganicSedimentsUseCase.invoke(updatedPlanet)
        )

        val generateOSResult = generateOrganicSedimentsUseCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            organicSediment = generateOSResult.first,
            planetOrganicSediments = generateOSResult.second
        )
        Log.d("NewTurnForPlanet", "After organic sediments generation: $updatedPlanet")

        //5 Generate BIOMASS
        updatedPlanet = updatedPlanet.copy(
            biomass = generateBiomassUseCase.invoke(updatedPlanet)
        )
        Log.d("NewTurnForPlanet", "After biomass generation: $updatedPlanet")

        //6 Generate METALS
        val metalProductionResult = generateMetalUseCase(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            metal = metalProductionResult.first,
            planetMetal = metalProductionResult.second
        )
        Log.d("NewTurnForPlanet", "After metal generation: $updatedPlanet")

        //7 Generate INFLUENCE
        updatedPlanet = updatedPlanet.copy(
            influence = generateInfluenceUseCase.invoke(updatedPlanet)
        )
        Log.d("NewTurnForPlanet", "After influence generation: $updatedPlanet")

        //8 Produce INFRASTRUCTURE
        val infraResult = produceInfrastructureUseCase.invoke(updatedPlanet, planetActions)
        Log.d("NewTurnForPlanet", "Infrastructure result: $infraResult")
        when (infraResult) {
            is ProduceInfraResult.Error -> {
                newErrors.add(
                    ProduceInfraError(
                        planetId = planetId,
                        error = infraResult
                    )
                )
            }

            is ProduceInfraResult.Success -> {
                updatedPlanet = updatedPlanet.copy(
                    infrastructure = infraResult.newInfra,
                    metal = infraResult.newMetal,
                    planetMetal = infraResult.newPlanetMetal
                )
            }
        }
        Log.d("NewTurnForPlanet", "After infrastructure production: $updatedPlanet")

        //9 Planet maintenance
        if (updatedPlanet.level > 4) {
            val maintenanceResult = planetMaintenanceUseCase.invoke(updatedPlanet, true)
            Log.d("NewTurnForPlanet", "Maintenance planet result: $maintenanceResult")
            when (maintenanceResult) {
                is PlanetMaintenanceResult.Error -> {
                    newErrors.add(
                        PlanetMaintenanceError(
                            planetId = planetId,
                            error = maintenanceResult
                        )
                    )

                    updatedPlanet = degradePlanetUseCase.invoke(updatedPlanet)
                }

                is PlanetMaintenanceResult.Success -> {
                    updatedPlanet = updatedPlanet.copy(
                        infrastructure = maintenanceResult.infrastructure,
                        influence = maintenanceResult.influence,
                        biomass = maintenanceResult.biomass
                    )
                }

                is PlanetMaintenanceResult.FailureWithSuccess -> {
                    newErrors.add(
                        PlanetMaintenanceError(
                            planetId = planetId,
                            error = maintenanceResult.error
                        )
                    )
                    updatedPlanet = updatedPlanet.copy(
                        infrastructure = maintenanceResult.success.infrastructure,
                        influence = maintenanceResult.success.influence,
                        biomass = maintenanceResult.success.biomass
                    )
                }
            }
        }
        Log.d("NewTurnForPlanet", "After planet maintenance: $updatedPlanet")

        //10 Army maintenance
        val armyMaintenanceResult = armyMaintenanceUseCase.invoke(updatedPlanet, true)
        Log.d("NewTurnForPlanet", "Army maintenance result: $armyMaintenanceResult")
        when (armyMaintenanceResult) {
            is ArmyMaintenanceResult.Error -> {
                newErrors.add(
                    ArmyMaintenanceError(
                        planetId = planetId,
                        error = armyMaintenanceResult
                    )
                )
                val resultDestroyArmy = destroyArmyUnitUseCase.invoke(updatedPlanet)
                updatedPlanet.copy(
                    army = resultDestroyArmy.first,
                    influence = resultDestroyArmy.second
                )
            }

            is ArmyMaintenanceResult.Success -> {
                updatedPlanet = updatedPlanet.copy(
                    influence = armyMaintenanceResult.influence
                )
            }

            is ArmyMaintenanceResult.FailureWithSuccess -> {
                newErrors.add(
                    ArmyMaintenanceError(
                        planetId = planetId,
                        error = armyMaintenanceResult.error
                    )
                )
                val resultDestroyArmy = destroyArmyUnitUseCase.invoke(updatedPlanet)
                updatedPlanet.copy(
                    army = resultDestroyArmy.first,
                    influence = resultDestroyArmy.second
                )
                updatedPlanet = updatedPlanet.copy(
                    influence = armyMaintenanceResult.success.influence
                )
            }
        }
        Log.d("NewTurnForPlanet", "After army maintenance: $updatedPlanet")

        //11 Produce ROCKET MATERIALS
        /*
        val rocketMaterialsResult = produceRocketMaterialsUseCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            rocketMaterials = rocketMaterialsResult.first,
            biomass = rocketMaterialsResult.second,
            organicSediment = rocketMaterialsResult.third
        )
        Log.d("NewTurnForPlanet", "After rocket materials production: $updatedPlanet")

         */

        //12 ProduceArmyUnit
        val armyResult = produceArmyUnitUseCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            army = armyResult.first,
            rocketMaterials = armyResult.second
        )
        Log.d("NewTurnForPlanet", "After army production: $updatedPlanet")

        //13 Produce EXPEDITIONS
        val expeditionsResult = produceExpeditionsUseCase.invoke(updatedPlanet)
        expeditions = expeditions + expeditionsResult.first
        updatedPlanet = updatedPlanet.copy(
            rocketMaterials = expeditionsResult.second
        )
        Log.d("NewTurnForPlanet", "After expeditions production: $expeditions")

        //14 Produce RESEARCH
        val researchResult = produceResearchUseCase.invoke(updatedPlanet)
        research += researchResult.first
        updatedPlanet = updatedPlanet.copy(
            biomass = researchResult.second
        )
        Log.d("NewTurnForPlanet", "After research production: $research")

        //15 Produce PROGRESS
        val progressResult = produceProgressUseCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            progress = progressResult.first,
            infrastructure = progressResult.second,
            biomass = progressResult.third
        )
        Log.d("NewTurnForPlanet", "After progress production: $updatedPlanet")



        //17 Generate TRADEPOWER
        val tradepowerResult = generateTradepowerUseCase.invoke(updatedPlanet)
        tradepower += tradepowerResult.first
        updatedPlanet = updatedPlanet.copy(
            influence = tradepowerResult.second
        )
        Log.d("NewTurnForPlanet", "After tradepower generation: $tradepower")

        //18 Trade

        //19 Districts changes
        //19a Build new districts
        planetActions
            .filterIsInstance<Action.DistrictAction.BuildDistrict>()
            .forEach { action ->
                val buildDistrictResult = buildDistrictUseCase.invoke(
                    updatedPlanet, action.district, action.districtId
                )
                when (buildDistrictResult) {
                    is BuildDistrictResult.Error -> newErrors.add(
                        BuildDistrictError(action.planetId, buildDistrictResult)
                    )

                    is BuildDistrictResult.Success -> {
                        updatedPlanet = updatedPlanet.copy(
                            districts = buildDistrictResult.districts
                        )

                    }
                }
            }
        Log.d("NewTurnForPlanet", "After building new districts: $updatedPlanet")

        //19b Destroy districts
        planetActions
            .filterIsInstance<Action.DistrictAction.DestroyDistrict>()
            .forEach { action ->
                val destroyDistrictResult =
                    destroyDistrictUseCase.invoke(updatedPlanet, action.districtId)
                updatedPlanet = updatedPlanet.copy(
                    planetMetal = destroyDistrictResult.first,
                    districts = destroyDistrictResult.second
                )
            }
        Log.d("NewTurnForPlanet", "After destroying districts: $updatedPlanet")

        //19c Change district mode
        planetActions
            .filterIsInstance<Action.DistrictAction.ChangeDistrictMode>()
            .forEach { action ->
                val changeDistrictModeResult = changeDistrictModeUseCase.invoke(
                    planet = updatedPlanet,
                    districtId = action.districtId,
                    districtForChange = action.districtType,
                    newMode = action.newMode,
                    continueOnError = true
                )
                when (changeDistrictModeResult) {
                    is ChangeDistrictModeResult.Error -> newErrors.add(
                        ChangeDistrictModeError(
                            planetId = planetId,
                            error = changeDistrictModeResult,
                            districtId = action.districtId
                        )
                    )

                    is ChangeDistrictModeResult.Success -> updatedPlanet = updatedPlanet.copy(
                        infrastructure = changeDistrictModeResult.updatedInfrastructure,
                        districts = changeDistrictModeResult.districts
                    )

                    is ChangeDistrictModeResult.FailureWithSuccess -> {
                        newErrors.add(
                            ChangeDistrictModeError(
                                planetId = planetId,
                                error = changeDistrictModeResult.error,
                                districtId = action.districtId
                            )
                        )
                        updatedPlanet = updatedPlanet.copy(
                            infrastructure = changeDistrictModeResult.success.updatedInfrastructure,
                            districts = changeDistrictModeResult.success.districts
                        )
                    }
                }
            }
        Log.d("NewTurnForPlanet", "After changing district mode: $updatedPlanet")

        val finishBuildingResult = finishDistrictConstructionUseCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            districts = finishBuildingResult.first,
            infrastructure = finishBuildingResult.second
        )
        Log.d("NewTurnForPlanet", "After finishing district construction: $updatedPlanet")

        //20 Transport in viz down

        //16 Generate DEVELOPMENT
        val developmentResult = generateDevelopmentUsaCase.invoke(updatedPlanet)
        updatedPlanet = updatedPlanet.copy(
            infrastructure = developmentResult.first,
            development = developmentResult.second
        )
        Log.d("NewTurnForPlanet", "After development generation: $updatedPlanet")

        //21 Planet growth
        if (updatedPlanet.progress >= calculateBorderForPlanetGrowth.invoke(updatedPlanet.level)) {
            val resultOfProgress = planetGrowthProgress.invoke(updatedPlanet)
            resultOfProgress.second?.let {
                newErrors.add(BuildDistrictError(planetId, it))
            }
            updatedPlanet = resultOfProgress.first
            Log.d("NewTurnForPlanet", "After planet growth: $updatedPlanet")
        }

        //22 Check for new planet expedition

        //23 Check resources capacities
        updatedPlanet = updatedPlanet.copy(
            metal = min(
                updatedPlanet.metal,
                calculateMetalCapacityUseCase.invoke(updatedPlanet)
            ),
            organicSediment = min(
                updatedPlanet.organicSediment,
                calculateOrganicSedimentsCapacityUseCase.invoke(updatedPlanet)
            ),
            rocketMaterials = min(
                updatedPlanet.rocketMaterials,
                calculateRocketMaterialCapacityUseCase.invoke(updatedPlanet)
            )
        )
        Log.d("NewTurnForPlanet", "After checking resources capacities: $updatedPlanet")


        //Transport in
        val transportsForPlanet =
            updatedTransports.filter { it.planet1Id == planetId && it.isSuccessOut }
        updatedPlanet = transportInUseCase.invoke(transportsForPlanet, updatedPlanet)

        updatedPlanet = updatedPlanet.copy(
            planetResourcesPossibleIncome = updatePossiblePlanetResourcesIncomeUseCase.invoke(updatedPlanet, initialPlanet)
        )

        val updatedPlanets = empire.planets.map { planet ->
            if (planet.id == planetId) {
                updatedPlanet
            } else {
                planet
            }
        }

        var updatedEmpire = empire.copy(
            expeditions = expeditions,
            planets = updatedPlanets,
            tradePower = tradepower,
            research = research,
            transports = updatedTransports.toList()
        )


        updatedEmpire = updatedEmpire.copy(
            empireResourcesPossibleIncome = updatePossibleEmpireResourcesIncomeUseCase.invoke(updatedEmpire, empire)
        )



        Log.d("NewTurnUseClass", "Final empire state: $updatedEmpire")
        return Pair(
            updatedEmpire,
            newErrors.toList()
        )
    }
}

 */