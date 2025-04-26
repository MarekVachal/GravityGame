package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.core.domain.error.ProduceProgressResult
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Use case for calculating progress points made on a planet based on its resources and settings.
 *
 * This class is responsible for determining how much progress points can be produced in a single turn
 * based on the available infrastructure, biomass, and the planet's progress setting.
 *
 * @constructor Creates a [ProduceProgressUseCase] instance. No external dependencies are needed.
 */
class ProduceProgressUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, isPlanning: Boolean) : ProduceProgressResult {
        if(planet.level == planet.maxLevel && planet.progressSetting > 0) {
            return ProduceProgressResult.Error.MaximumLvlOfPlanet
        }
        val exampleDistrict = planet.districts.filterIsInstance<District.Capitol>().first()
        val resources = exampleDistrict.generateResources()
        val productionRate = resources.produced[Resource.PROGRESS] ?: 1
        val consumptionBiomassRate = resources.consumed[Resource.BIOMASS]?: 1
        val consumptionInfrastructureRate = resources.consumed[Resource.INFRASTRUCTURE]?: 1

        val maxBiomassBasedProduction = floor(planet.biomass / consumptionBiomassRate).toInt() * productionRate
        val maxInfrastructureBasedProduction = planet.infrastructure / consumptionInfrastructureRate * productionRate
        val availableResource = min(maxBiomassBasedProduction, maxInfrastructureBasedProduction)
        val availableProduction = min(availableResource, planet.progressSetting)
        val success = ProduceProgressResult.Success(
            planet.progress + availableProduction,
            planet.infrastructure - (availableProduction / productionRate) * consumptionInfrastructureRate,
            planet.biomass - ((availableProduction / productionRate) * consumptionBiomassRate).toFloat()
        )

        return if(!isPlanning) {
            if(availableProduction < 0){
                ProduceProgressResult.Success(
                    planet.progress,
                    planet.infrastructure,
                    planet.biomass
                )
            } else {
                success
            }
        } else if(availableProduction == planet.progressSetting) {
            success
        } else {
            ProduceProgressResult.FailureWithSuccess(
                success = ProduceProgressResult.Success(
                    progress = planet.progress + planet.progressSetting,
                    infrastructure = planet.infrastructure - ((planet.progressSetting / productionRate) * consumptionInfrastructureRate),
                    biomass = planet.biomass - ((planet.progressSetting / productionRate) * consumptionBiomassRate).toFloat()
                ),
                error = ProduceProgressResult.Error.InsufficientResources(
                    missingInfra = max(0, planet.progressSetting - maxInfrastructureBasedProduction),
                    missingBiomass = max(0, planet.progressSetting - maxBiomassBasedProduction)
                )
            )
        }
    }
}