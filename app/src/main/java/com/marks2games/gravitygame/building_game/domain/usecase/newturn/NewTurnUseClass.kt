package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateMetalCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateOrganicSedimentsCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetOSCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateRocketMaterialCapacityUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CheckForNewPlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CheckForResearchFinishUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.PlanetGrowthUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CloseDistrictIsWorkingUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CreateNewPlanetUseCase
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
import com.marks2games.gravitygame.core.domain.error.ProduceProgressResult
import com.marks2games.gravitygame.core.domain.error.RocketMaterialsResult
import javax.inject.Inject
import kotlin.math.min

class NewTurnUseClass @Inject constructor(
    private val planetGrowthUseCase: PlanetGrowthUseCase,
    private val generateBiomassUseCase: GenerateBiomassUseCase,
    private val generateInfluenceUseCase: GenerateInfluenceUseCase,
    private val generateDevelopmentUsaCase: GenerateDevelopmentUseCase,
    private val produceExpeditionsUseCase: ProduceExpeditionsUseCase,
    private val produceProgressUseCase: ProduceProgressUseCase,
    private val generateTradepowerUseCase: GenerateTradepowerUseCase,
    private val calculateMetalCapacityUseCase: CalculateMetalCapacityUseCase,
    private val generateMetalByProspectorsUseCase: GenerateMetalByProspectorsUseCase,
    private val calculateOrganicSedimentsCapacityUseCase: CalculateOrganicSedimentsCapacityUseCase,
    private val generatePlanetOrganicSedimentsUseCase: GeneratePlanetOrganicSedimentsUseCase,
    private val produceResearchUseCase: ProduceResearchUseCase,
    private val produceMilitaryCompoundsUseCase: ProduceMilitaryCompoundsUseCase,
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
    private val generateOrganicSedimentsUseCase: GenerateOrganicSedimentsUseCase,
    private val finishDistrictConstructionUseCase: FinishDistrictConstructionUseCase,
    private val updatePossiblePlanetResourcesIncomeUseCase: UpdatePossiblePlanetResourcesIncomeUseCase,
    private val updatePossibleEmpireResourcesIncomeUseCase: UpdatePossibleEmpireResourcesIncomeUseCase,
    private val calculatePlanetOSCapacityUseCase: CalculatePlanetOSCapacityUseCase,
    private val checkForResearchFinish: CheckForResearchFinishUseCase,
    private val buildShip: BuildNewShipUseCase
) {
    operator fun invoke(empire: Empire, isPlanning: Boolean): Pair<Empire, List<NewTurnError>> {
        Log.d("NewTurn", "Starting new turn for empire: $empire")
        val errors: MutableList<NewTurnError> = mutableListOf()
        var expeditions = empire.expeditions
        var tradepower = 0
        var research = empire.research
        var scheduledTransports = empire.transports.toMutableList()
        var successfulTransports = mutableListOf<Transport>()
        val updatedTransports = empire.transports.toMutableList()
        var updatedPlanets = empire.planets

        Log.d("Transport", "Scheduled transports: $scheduledTransports")
        Log.d("Transport", "Updated transports: $updatedTransports")


        //Create transports lists
        empire.actions.forEach { action ->
            if (action is Action.TransportAction) {
                scheduledTransports.add(action.setting)
                if (!isPlanning && action.setting.isLongTime) {
                    updatedTransports.add(action.setting)
                }
            }
        }
        updatedPlanets.forEach { planet ->
            val result = transportOutUseCase.invoke(scheduledTransports, planet, isPlanning)
            val transportResults = result.second
            Log.d("Transport", "Transport results: $transportResults")
            errors + result.third
            val successful = transportResults
                .filter { it.isSuccessOut && it.planet1Id == planet.id }

            Log.d("Transport", "Successful transports for a planet: $successful" )
            successfulTransports += successful
        }
        Log.d("Transport", "Successful transports: $successfulTransports")

        updatedPlanets = updatedPlanets.map { planet ->
            Log.d("NewTurn", "Processing planet: ${planet.id}")
            var updatedPlanet = planet
            Log.d("NewTurn", "Initial planet state: $updatedPlanet")
            val planetActions = empire.actions.filter { it.planetId == updatedPlanet.id }

            //1 Close districts
            updatedPlanet = updatedPlanet.copy(
                districts = closeDistrictIsWorkingUseCase.invoke(updatedPlanet, planetActions)
            )
            Log.d("NewTurn", "Districts after closing: ${updatedPlanet.districts}")

            //2 Transports Out
            val transportsOutForPlanet = successfulTransports.filter { (
                    it.planet1Id == planet.id || it.planet2Id == planet.id) && it.isSuccessOut
            }
            val transportResult = transportOutUseCase.invoke(transportsOutForPlanet, updatedPlanet, isPlanning)
            updatedPlanet = transportResult.first
            Log.d("NewTurn", "After transports out: $updatedPlanet")

            //3 Set resources production by user
            updatedPlanet = setResourcesProduceUseCase.invoke(updatedPlanet, planetActions)
            Log.d("NewTurn", "After set resources production: $updatedPlanet")

            //4 Generate planet resources of OS
            val incomeOS = generatePlanetOrganicSedimentsUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                planetOrganicSediments = updatedPlanet.planetOrganicSediments + incomeOS
            )
            Log.d("NewTurn", "After planet organic sediments generation: $updatedPlanet")

            //5 Generate OS
            val generateOSResult = generateOrganicSedimentsUseCase.invoke(updatedPlanet, empire.technologies)
            updatedPlanet = updatedPlanet.copy(
                organicSediment = generateOSResult.first,
                planetOrganicSediments = generateOSResult.second
            )
            Log.d("NewTurn", "After organic sediments generation: $updatedPlanet")

            //5 Generate BIOMASS
            updatedPlanet = updatedPlanet.copy(
                biomass = updatedPlanet.biomass + generateBiomassUseCase.invoke(updatedPlanet, empire.technologies)
            )
            Log.d("NewTurn", "After biomass generation: $updatedPlanet")

            //6 Generate METALS
            val metalProductionResult = generateMetalByProspectorsUseCase(updatedPlanet, empire.technologies)
            updatedPlanet = updatedPlanet.copy(
                metal = metalProductionResult.first,
                planetMetal = metalProductionResult.second
            )
            Log.d("NewTurn", "After metal generation: $updatedPlanet")

            //7 Generate INFLUENCE
            updatedPlanet = updatedPlanet.copy(
                influence = generateInfluenceUseCase.invoke(updatedPlanet, empire.technologies)
            )
            Log.d("NewTurn", "After influence generation: $updatedPlanet")

            //8 Produce INFRASTRUCTURE
            val infraResult = produceInfrastructureUseCase.invoke(updatedPlanet, planetActions, isPlanning, empire.technologies)
            Log.d("NewTurn", "Infrastructure result: $infraResult")
            when(infraResult){
                is ProduceInfraResult.Error -> {
                    if (isPlanning) {
                        errors.add(
                            ProduceInfraError(
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

                is ProduceInfraResult.FailureWihSuccess -> {
                    updatedPlanet = updatedPlanet.copy(
                        infrastructure = infraResult.success.newInfra,
                        metal = infraResult.success.newMetal,
                        planetMetal = infraResult.success.newPlanetMetal
                    )
                    errors.add(
                        ProduceInfraError(
                            planetId = planet.id,
                            error = infraResult.error
                        )
                    )
                }
            }
            Log.d("NewTurn", "After infrastructure production: $updatedPlanet")

            //9 Planet maintenance
            if (planet.level > 4){
                val maintenanceResult = planetMaintenanceUseCase.invoke(updatedPlanet, isPlanning, empire.technologies)
                Log.d("NewTurn", "Maintenance planet result: $maintenanceResult")
                when (maintenanceResult) {
                    is PlanetMaintenanceResult.Error -> {
                        if(isPlanning){
                            errors.add(
                                PlanetMaintenanceError(
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

                    is PlanetMaintenanceResult.FailureWithSuccess -> {
                        errors.add(
                            PlanetMaintenanceError(
                                planetId = planet.id,
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
            Log.d("NewTurn", "After planet maintenance: $updatedPlanet")

            //10 Army maintenance
            val armyMaintenanceResult = armyMaintenanceUseCase.invoke(updatedPlanet, isPlanning)
            Log.d("NewTurn", "Army maintenance result: $armyMaintenanceResult")
            when (armyMaintenanceResult) {
                is ArmyMaintenanceResult.Error -> {
                    if(isPlanning){
                        errors.add(
                            ArmyMaintenanceError(
                                planetId = planet.id,
                                error = armyMaintenanceResult
                            )
                        )
                    }
                    val resultDestroyArmy = destroyArmyUnitUseCase.invoke(updatedPlanet)
                    updatedPlanet = updatedPlanet.copy(
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
                    errors.add(
                        ArmyMaintenanceError(
                            planetId = planet.id,
                            error = armyMaintenanceResult.error
                        )
                    )
                    val resultDestroyArmy = destroyArmyUnitUseCase.invoke(updatedPlanet)
                    updatedPlanet = updatedPlanet.copy(
                        army = resultDestroyArmy.first,
                        influence = resultDestroyArmy.second
                    )
                    updatedPlanet = updatedPlanet.copy(
                        influence = armyMaintenanceResult.success.influence
                    )
                }
            }
            Log.d("NewTurn", "After army maintenance: $updatedPlanet")

            //11 Produce ROCKET MATERIALS
            val rocketMaterialsResult = produceRocketMaterialsUseCase.invoke(updatedPlanet, empire.technologies)
            when(rocketMaterialsResult){
                is RocketMaterialsResult.Error -> if(isPlanning){
                    errors.add(
                        ProduceRocketMaterialsError(
                            planetId = planet.id,
                            error = rocketMaterialsResult
                        )
                    )
                }
                is RocketMaterialsResult.FailureWithSuccess -> {
                    if(isPlanning) {
                        errors.add(
                            ProduceRocketMaterialsError(
                                planetId = planet.id,
                                error = rocketMaterialsResult.error
                            )
                        )
                    }
                    updatedPlanet = updatedPlanet.copy(
                        rocketMaterials = rocketMaterialsResult.success.updatedRocketMaterials,
                        metal = rocketMaterialsResult.success.updatedMetal,
                        organicSediment = rocketMaterialsResult.success.updatedOrganicSediments
                    )
                }


                is RocketMaterialsResult.Success ->
                    updatedPlanet = updatedPlanet.copy(
                        rocketMaterials = rocketMaterialsResult.updatedRocketMaterials,
                        metal = rocketMaterialsResult.updatedMetal,
                        organicSediment = rocketMaterialsResult.updatedOrganicSediments
                    )
            }

            Log.d("NewTurn", "After rocket materials production: $updatedPlanet")

            //12 ProduceArmyUnit
            val armyResult = produceMilitaryCompoundsUseCase.invoke(updatedPlanet, empire.technologies)
            updatedPlanet = updatedPlanet.copy(
                army = armyResult.first,
                rocketMaterials = armyResult.second
            )
            Log.d("NewTurn", "After army production: $updatedPlanet")

            //13 Produce EXPEDITIONS
            val expeditionsResult = produceExpeditionsUseCase.invoke(updatedPlanet, empire.technologies)
            expeditions += expeditionsResult.first
            updatedPlanet = updatedPlanet.copy(
                rocketMaterials = expeditionsResult.second
            )
            Log.d("NewTurn", "After expeditions production: $expeditions")

            //14 Produce RESEARCH
            val researchResult = produceResearchUseCase.invoke(updatedPlanet, empire.technologies)
            research += researchResult.first
            updatedPlanet = updatedPlanet.copy(
                biomass = researchResult.second
            )
            Log.d("NewTurn", "After research production: $updatedPlanet")

            //15 Produce PROGRESS
            val progressResult = produceProgressUseCase.invoke(updatedPlanet, isPlanning)
            when (progressResult){
                is ProduceProgressResult.Error.InsufficientResources -> {
                    errors.add(
                        ProduceProgressError(
                            planetId = planet.id,
                            error = progressResult
                        )
                    )
                }
                ProduceProgressResult.Error.MaximumLvlOfPlanet -> {
                    errors.add(
                        ProduceProgressError(
                            planetId = planet.id,
                            error = ProduceProgressResult.Error.MaximumLvlOfPlanet
                        )
                    )
                }
                is ProduceProgressResult.FailureWithSuccess -> {
                    updatedPlanet = updatedPlanet.copy(
                        progress = progressResult.success.progress,
                        infrastructure = progressResult.success.infrastructure,
                        biomass = progressResult.success.biomass
                    )
                    errors.add(
                        ProduceProgressError(
                            planetId = planet.id,
                            error = progressResult.error
                        )
                    )
                }
                is ProduceProgressResult.Success -> {
                    updatedPlanet = updatedPlanet.copy(
                        progress = progressResult.progress,
                        infrastructure = progressResult.infrastructure,
                        biomass = progressResult.biomass
                    )
                }
            }

            Log.d("NewTurn", "After progress production: $updatedPlanet")

            //17 Generate TRADEPOWER
            val tradepowerResult = generateTradepowerUseCase.invoke(updatedPlanet)
            tradepower += tradepowerResult.first
            updatedPlanet = updatedPlanet.copy(
                influence = tradepowerResult.second
            )
            Log.d("NewTurn", "After tradepower generation: $tradepower")

            //18 Trade

            //19 Districts changes
            //19a Build new districts
            planetActions
                .filterIsInstance<Action.DistrictAction.BuildDistrict>()
                .forEach { action ->
                    val buildDistrictResult = buildDistrictUseCase.invoke(
                        updatedPlanet, action.district, action.districtId)
                    when (buildDistrictResult) {
                        is BuildDistrictResult.Error -> errors.add(
                            BuildDistrictError(action.planetId,buildDistrictResult)
                        )
                        is BuildDistrictResult.Success -> {
                            updatedPlanet = updatedPlanet.copy(
                                districts = buildDistrictResult.districts
                            )

                        }
                    }
                }
            Log.d("NewTurn", "After building new districts: $updatedPlanet")

            //19b Destroy districts
            planetActions
                .filterIsInstance<Action.DistrictAction.DestroyDistrict>()
                .forEach { action ->
                    val destroyDistrictResult = destroyDistrictUseCase.invoke(updatedPlanet, action.districtId)
                    updatedPlanet = updatedPlanet.copy(
                        planetMetal = destroyDistrictResult.first,
                        districts = destroyDistrictResult.second
                    )
                }
            Log.d("NewTurn", "After destroying districts: $updatedPlanet")

            //19c Change district mode
            planetActions
                .filterIsInstance<Action.DistrictAction.ChangeDistrictMode>()
                .forEach { action ->
                    val changeDistrictModeResult = changeDistrictModeUseCase.invoke(
                        planet = updatedPlanet,
                        districtId = action.districtId,
                        districtForChange = action.districtType,
                        newMode = action.newMode,
                        continueOnError = isPlanning
                    )
                    when(changeDistrictModeResult){
                        is ChangeDistrictModeResult.Error -> errors.add(
                            ChangeDistrictModeError(
                                planetId = planet.id,
                                error = changeDistrictModeResult,
                                districtId = action.districtId
                            )
                        )
                        is ChangeDistrictModeResult.Success -> updatedPlanet = updatedPlanet.copy(
                            infrastructure = changeDistrictModeResult.updatedInfrastructure,
                            districts = changeDistrictModeResult.districts
                        )

                        is ChangeDistrictModeResult.FailureWithSuccess -> {
                            errors.add(
                                ChangeDistrictModeError(
                                    planetId = planet.id,
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
            Log.d("NewTurn", "After changing district mode: $updatedPlanet")

            //Finish district construction
            val finishBuildingResult = finishDistrictConstructionUseCase.invoke(updatedPlanet, isPlanning)
            updatedPlanet = updatedPlanet.copy(
                districts = finishBuildingResult.first,
                infrastructure = finishBuildingResult.second
            )
            Log.d("NewTurn", "After finishing district construction: $updatedPlanet")

            //16 Generate DEVELOPMENT
            val developmentResult = generateDevelopmentUsaCase.invoke(updatedPlanet)
            updatedPlanet = if(isPlanning){
                updatedPlanet.copy(
                    development = developmentResult.second
                )
            } else {
                updatedPlanet.copy(
                    infrastructure = developmentResult.first,
                    development = developmentResult.second
                )
            }

            Log.d("NewTurn", "After development generation: $updatedPlanet")

            //17 Build ship
            updatedPlanet = buildShip.invoke(updatedPlanet)

            //20 Transport In
            val transportsForPlanet = successfulTransports.filter {
                (it.planet2Id == planet.id ||it.planet1Id == planet.id ) && it.isSuccessOut
            }
            updatedPlanet = transportInUseCase.invoke(transportsForPlanet, updatedPlanet)

            Log.d("NewTurn", "After transports in: $updatedPlanet")

            //21 Planet growth
            if(!isPlanning){
                Log.d("Progress", "Planet progress: ${updatedPlanet.progress}")
                Log.d("Progress", "Planet level: ${updatedPlanet.level}")
                Log.d("Progress", "Planet growth border: ${updatedPlanet.planetGrowthBorder}")
                if (updatedPlanet.progress >= updatedPlanet.planetGrowthBorder) {
                    updatedPlanet = planetGrowthUseCase.invoke(updatedPlanet)
                    Log.d("Progress", "After planet growth: $updatedPlanet")
                }
            }

            //22 Check for new planet expedition
            //viz down

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
                ),
                planetOrganicSediments = calculatePlanetOSCapacityUseCase.invoke(updatedPlanet).toFloat()
            )
            Log.d("NewTurn", "After checking resources capacities: $updatedPlanet")

            if(isPlanning){
                Log.d("PlanIncome", "Planet income calculation started.")
                updatedPlanet = updatedPlanet.copy(
                    planetResourcesPossibleIncome = updatePossiblePlanetResourcesIncomeUseCase.invoke(updatedPlanet, planet)
                )
            }
            Log.d("NewTurn", "Updated planet after updatedPlanet: $updatedPlanet")
            updatedPlanet
        }

        var updatedEmpire = empire.copy(
            expeditions = expeditions,
            planets = updatedPlanets,
            tradePower = tradepower,
            research = research,
            transports = updatedTransports.toList()
        )
        Log.d("NewTurn", "After updating empire: $updatedEmpire")

        //Check for new Planet from Expeditions
        if(!isPlanning){
            if(checkForNewPlanetUseCase.invoke(updatedEmpire)){
                Log.d("NewTurn", "New planet is adding to Empire")
                updatedEmpire = createNewPlanetUseCase.invoke(updatedEmpire)
                Log.d("NewTurn", "After new planet is added to empire: $updatedEmpire")
            }
            updatedEmpire = updatedEmpire.copy(
                actions = emptyList()
            )
            Log.d("NewTurn", "Action list for empire is cleaned.")
            val newTurns = empire.turns + 1
            updatedEmpire = updatedEmpire.copy(
                turns = newTurns
            )
            updatedEmpire = checkForResearchFinish.invoke(updatedEmpire)

        }

        if(isPlanning){
            updatedEmpire = updatedEmpire.copy(
                empireResourcesPossibleIncome = updatePossibleEmpireResourcesIncomeUseCase.invoke(updatedEmpire, empire)
            )
            Log.d("NewTurn", "After empire possible income: $updatedEmpire")
        }

        Log.d("NewTurn", "Final empire state: $updatedEmpire\n")
        return Pair(
            updatedEmpire,
            errors.toList()
        )
    }
}