package com.marks2games.gravitygame.building_game.domain.usecase.utils

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.GenerateBiomassUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.GenerateMetalUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.ProduceInfrastructureUseCase
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class MaxProgressProductionUseCase @Inject constructor(
    private val generateBiomass: GenerateBiomassUseCase,
    private val produceInfrastructure: ProduceInfrastructureUseCase,
    private val generateMetal: GenerateMetalUseCase
) {
    operator fun invoke(planet: Planet, actions: List<Action>):Int{
        var updatedPlanet = planet
        updatedPlanet = updatedPlanet.copy(
            biomass = updatedPlanet.biomass + generateBiomass.invoke(planet)
        )
        updatedPlanet = updatedPlanet.copy(
            metal = generateMetal.invoke(updatedPlanet).first,
            progressSetting = 0,
            infrastructureSetting = InfrastructureSetting.MAXIMUM
        )
        val resultInfra = produceInfrastructure.invoke(updatedPlanet, actions, true)
        val newInfra = when (resultInfra) {
            is ProduceInfraResult.Success -> resultInfra.newInfra
            is ProduceInfraResult.FailureWihSuccess -> resultInfra.success.newInfra
            else -> null
        }
        Log.d("MaxProgressProduction", "newInfra=$newInfra")

        if (newInfra != null) {
            updatedPlanet = updatedPlanet.copy(
                infrastructure = newInfra
            )
        }
        Log.d("MaxProgressProduction", "updatedPlanet=${updatedPlanet.infrastructure}")

        val biomassInt = floor(updatedPlanet.biomass).toInt()
        return min(biomassInt, updatedPlanet.infrastructure)
    }
}