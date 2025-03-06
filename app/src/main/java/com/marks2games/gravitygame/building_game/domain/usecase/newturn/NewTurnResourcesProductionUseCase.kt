package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.core.domain.TimeProvider
import javax.inject.Inject

class NewTurnResourcesProductionUseCase @Inject constructor(
    private val empireRepository: EmpireRepository,
    private val timeProvider: TimeProvider,
    private val biomassGrowthUseCase: BiomassGrowthUseCase,
    private val organicSedimentGrowthUseCase: OrganicSedimentsGrowthUseCase,
    private val metalGrowthUseCase: MetalGrowthUseCase,
    private val rocketMaterialsGrowthUseCase: RocketMaterialsGrowthUseCase,
    private val infrastructureGrowthUseCase: InfrastructureGrowthUseCase,
    private val researchGrowthUseCase: ResearchGrowthUseCase,
    private val influenceGrowthUseCase: InfluenceGrowthUseCase,
    private val maintenanceUseCase: PlanetMaintenanceUseCase
) {
    suspend operator fun invoke(empire: Empire): Empire {
        var savedTurns = empire.savedTurns
        if (savedTurns == 0) return empire
        var research = empire.research
        var credits = empire.credits

        val updatedPlanets = empire.planets.map { planet ->
            var updatedPlanet = planet

            updatedPlanet = updatedPlanet.copy(
                biomass = biomassGrowthUseCase.invoke(updatedPlanet)
            )
            updatedPlanet = updatedPlanet.copy(
                organicSediment = organicSedimentGrowthUseCase.invoke(updatedPlanet),
                metal = metalGrowthUseCase.invoke(updatedPlanet)
            )
            val rocketMaterialResult = rocketMaterialsGrowthUseCase.invoke(updatedPlanet)
            val infrastructureResult = infrastructureGrowthUseCase.invoke(updatedPlanet)
            updatedPlanet = updatedPlanet.copy(
                rocketMaterials = rocketMaterialResult.first,
                biomass = rocketMaterialResult.second,
                organicSediment = rocketMaterialResult.third,
                infrastructure = infrastructureResult.first,
                metal = infrastructureResult.second
            )
            val researchResult = researchGrowthUseCase.invoke(updatedPlanet, empire)
            updatedPlanet = updatedPlanet.copy(
                biomass = researchResult.second,
                influence = influenceGrowthUseCase.invoke(updatedPlanet)
            )
            research = researchResult.first

            if (planet.level >= 10) {
                updatedPlanet = maintenanceUseCase.invoke(updatedPlanet)
            }

            updatedPlanet
        }

        val newTime = timeProvider.getCurrentTimeMillis()
        savedTurns -= 1


        val updatedEmpire = empire.copy(
            lastUpdated = newTime,
            savedTurns = savedTurns,
            planets = updatedPlanets,
            research = research,
            tradePower = 0,
            credits = if (credits > 99) credits - credits / 100 else credits
        )

        empireRepository.updateEmpire(updatedEmpire)
        return updatedEmpire
    }
}