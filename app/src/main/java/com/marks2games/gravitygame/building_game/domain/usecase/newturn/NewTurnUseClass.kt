package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateMetalCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateOrganicSedimentsCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateRocketMaterialCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CheckForNewPlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CheckForPlanetsProgressUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CloseDistrictIsWorkingUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CreateNewPlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.DegradePlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.DestroyArmyUnitUseCase
import com.marks2games.gravitygame.core.domain.error.ArmyMaintenanceResult
import com.marks2games.gravitygame.core.domain.error.BuildDistrictResult
import com.marks2games.gravitygame.core.domain.error.ChangeDistrictModeResult
import com.marks2games.gravitygame.core.domain.error.NewTurnError
import com.marks2games.gravitygame.core.domain.error.PlanetMaintenanceResult
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.min

class NewTurnUseClass @Inject constructor(
    private val checkForPlanetProgressUseCase: CheckForPlanetsProgressUseCase,
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
    private val checkForNewPlanetUseCase: CheckForNewPlanetUseCase,
    private val createNewPlanetUseCase: CreateNewPlanetUseCase,
    private val generateOrganicSedimentsUseCase: GenerateOrganicSedimentsUseCase
) {
    operator fun invoke(empire: Empire, isPlanning: Boolean): Pair<Empire, List<NewTurnError>> {
        Log.d("NewTurnUseClass", "Starting new turn for empire: $empire")
        val errors: MutableList<NewTurnError> = mutableListOf()
        var expeditions = empire.expeditions
        var tradepower = 0
        var research = empire.research
        var scheduledTransports = empire.transports.toMutableList()
        val updatedTransports = empire.transports.toMutableList()
        var updatedPlanets = empire.planets

        //TransportsOut
        updatedPlanets.forEach { planet ->
            planet.actions.forEach { action ->
                when(action){
                    is Action.TransportAction -> {
                        scheduledTransports.add(action.transport)
                        if(!isPlanning && action.transport.isLongTime){
                            updatedTransports.add(action.transport)
                        }
                    }
                    else -> println("Don't need")
                }
            }
        }

        val transportResult = transportOutUseCase.invoke(scheduledTransports, updatedPlanets)
        scheduledTransports = transportResult.second.toMutableList()
        errors.addAll(transportResult.third)
        updatedPlanets = transportResult.first



        updatedPlanets = updatedPlanets.map { planet ->
            Log.d("NewTurnUseClass", "Processing planet: ${planet.id}")
            var updatedPlanet = planet

            //1 Close districts
            updatedPlanet = updatedPlanet.copy(
                districts = closeDistrictIsWorkingUseCase.invoke(updatedPlanet)
            )

            //2 Transport out - viz up

            //3 Set resources production by user
            updatedPlanet = setResourcesProduceUseCase.invoke(updatedPlanet)

            //4 Generate OS
            updatedPlanet = updatedPlanet.copy(
                planetOrganicSediments = generatePlanetOrganicSedimentsUseCase.invoke(updatedPlanet)
            )
            val generateOSResult = generateOrganicSedimentsUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                organicSediment = generateOSResult.first,
                planetOrganicSediments = generateOSResult.second
            )

            //5 Generate BIOMASS
            updatedPlanet = updatedPlanet.copy(
                biomass = generateBiomassUseCase.invoke(updatedPlanet)
            )

            //6 Generate METALS
            val metalProductionResult = generateMetalUseCase(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                metal = metalProductionResult.first,
                planetMetal = metalProductionResult.second
            )

            //7 Generate INFLUENCE
            updatedPlanet = updatedPlanet.copy(
                influence = generateInfluenceUseCase.invoke(updatedPlanet)
            )

            //8 Produce INFRASTRUCTURE
            val infraResult = produceInfrastructureUseCase.invoke(updatedPlanet)
            when(infraResult){
                is ProduceInfraResult.Error -> {
                    if (isPlanning) {
                        errors.add(
                            NewTurnError.ProduceInfraError(
                                planetId = planet.id,
                                error = infraResult
                            )
                        )
                    }
                }
                is ProduceInfraResult.Success -> {
                    updatedPlanet = updatedPlanet.copy(
                        infrastructure = infraResult.newInfra,
                        metal = infraResult.newMetal,
                        planetMetal = infraResult.newPlanetMetal
                    )
                }
            }

            //9 Planet maintenance
            if (planet.level > 9){
                val maintenanceResult = planetMaintenanceUseCase.invoke(updatedPlanet)
                when (maintenanceResult) {
                    is PlanetMaintenanceResult.Error -> {
                        if(isPlanning){
                            errors.add(
                                NewTurnError.PlanetMaintenanceError(
                                    planetId = planet.id,
                                    error = maintenanceResult
                                )
                            )
                        }
                        updatedPlanet = degradePlanetUseCase.invoke(updatedPlanet)
                    }
                    is PlanetMaintenanceResult.Success -> {
                        updatedPlanet = updatedPlanet.copy(
                            infrastructure = maintenanceResult.infrastructure,
                            influence = maintenanceResult.influence,
                            biomass = maintenanceResult.biomass
                        )
                    }
                }
            }
            //10 Army maintenance
            val armyMaintenanceResult = armyMaintenanceUseCase.invoke(updatedPlanet)
            when (armyMaintenanceResult) {
                is ArmyMaintenanceResult.Error -> {
                    if(isPlanning){
                        errors.add(
                            NewTurnError.ArmyMaintenanceError(
                                planetId = planet.id,
                                error = armyMaintenanceResult
                            )
                        )
                    }
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
            }

            //11 Produce ROCKET MATERIALS
            val rocketMaterialsResult = produceRocketMaterialsUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                rocketMaterials = rocketMaterialsResult.first,
                biomass = rocketMaterialsResult.second,
                organicSediment = rocketMaterialsResult.third
            )

            //12 ProduceArmyUnit
            val armyResult = produceArmyUnitUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                army = armyResult.first,
                rocketMaterials = armyResult.second
            )

            //13 Produce EXPEDITIONS
            val expeditionsResult = produceExpeditionsUseCase.invoke(updatedPlanet)
            expeditions = expeditions + expeditionsResult.first
            updatedPlanet = updatedPlanet.copy(
                rocketMaterials = expeditionsResult.second
            )

            //14 Produce RESEARCH
            val researchResult = produceResearchUseCase.invoke(updatedPlanet)
            research += researchResult.first
            updatedPlanet = updatedPlanet.copy(
                biomass = researchResult.second
            )

            //15 Produce PROGRESS
            val progressResult = produceProgressUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                progress = progressResult.first,
                infrastructure = progressResult.second,
                biomass = progressResult.third
            )

            //16 Generate DEVELOPMENT
            val developmentResult = generateDevelopmentUsaCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                infrastructure = developmentResult.first,
                development = developmentResult.second
            )

            //17 Generate TRADEPOWER
            val tradepowerResult = generateTradepowerUseCase.invoke(updatedPlanet)
            tradepower += tradepowerResult.first
            updatedPlanet = updatedPlanet.copy(
                influence = tradepowerResult.second
            )

            //18 Trade

            //19 Districts changes
            //19a Build new districts
            planet.actions
                .filterIsInstance<Action.DistrictAction.BuildDistrict>()
                .forEach { action ->
                    val buildDistrictResult = buildDistrictUseCase.invoke(
                        updatedPlanet, action.district, action.districtId)
                    when (buildDistrictResult) {
                        is BuildDistrictResult.Error -> errors.add(
                            NewTurnError.BuildDistrictError(action.planetId,buildDistrictResult)
                        )
                        is BuildDistrictResult.Success -> {
                            updatedPlanet = updatedPlanet.copy(
                                infrastructure = buildDistrictResult.updatedInfrastructure,
                                districts = buildDistrictResult.districts
                            )

                        }
                    }
                }

            //19b Destroy districts
            planet.actions
                .filterIsInstance<Action.DistrictAction.DestroyDistrict>()
                .forEach { action ->
                    val destroyDistrictResult = destroyDistrictUseCase.invoke(updatedPlanet, action.districtId)
                    updatedPlanet = updatedPlanet.copy(
                        planetMetal = destroyDistrictResult.first,
                        districts = destroyDistrictResult.second
                    )
                }

            //19c Change district mode
            planet.actions
                .filterIsInstance<Action.DistrictAction.ChangeDistrictMode>()
                .forEach { action ->
                    val changeDistrictModeResult = changeDistrictModeUseCase.invoke(
                        planet = updatedPlanet,
                        districtId = action.districtId,
                        districtForChange = action.districtType,
                        newMode = action.newMode
                    )
                    when(changeDistrictModeResult){
                        is ChangeDistrictModeResult.Error -> errors.add(
                            NewTurnError.ChangeDistrictModeError(
                                planetId = planet.id,
                                error = changeDistrictModeResult
                            )
                        )
                        is ChangeDistrictModeResult.Success -> updatedPlanet = updatedPlanet.copy(
                            infrastructure = changeDistrictModeResult.updatedInfrastructure,
                            districts = changeDistrictModeResult.districts
                        )
                    }

                }

            //20 Transport in viz down

            //21 Planet growth
            val resultOfProgress = checkForPlanetProgressUseCase.invoke(updatedPlanet)
            resultOfProgress.second?.let {
                errors.add(NewTurnError.BuildDistrictError(planet.id, it))
            }
            updatedPlanet = resultOfProgress.first

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
            Log.d("NewTurnUseClass", "Updated planet: $updatedPlanet")
            updatedPlanet
        }

        //Transport in
        updatedPlanets = updatedPlanets.map { planet ->
            val transportsForPlanet = updatedTransports.filter { it.planet1Id == planet.id && it.isSuccessOut }
            transportInUseCase.invoke(transportsForPlanet, planet)
        }

        var updatedEmpire = empire.copy(
            expeditions = expeditions,
            planets = updatedPlanets,
            tradePower = tradepower,
            research = research,
            transports = updatedTransports.toList()
        )

        if(checkForNewPlanetUseCase.invoke(updatedEmpire)){
            val result = createNewPlanetUseCase.invoke(updatedEmpire)
            updatedEmpire = updatedEmpire.copy(
                expeditions = result.second,
                planets = result.first
            )
        }

        Log.d("NewTurnUseClass", "Final empire state: $updatedEmpire")
        return Pair(
            updatedEmpire,
            errors.toList()
        )
    }
}