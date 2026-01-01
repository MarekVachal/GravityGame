package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResources
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.floor

class UpdatePossiblePlanetResourcesIncomeUseCase @Inject constructor() {
    operator fun invoke(
        updatedPlanet: Planet,
        initialPlanet: Planet
    ): PlanetResources{

        Log.d("PlanIncome", "Updated planet: $updatedPlanet")
        Log.d("PlanIncome", "Initial planet: $initialPlanet")

        val planetResources = PlanetResources(
            mapOf(
                Resource.BIOMASS to floor(updatedPlanet.biomass - initialPlanet.biomass).toInt(),
                Resource.ORGANIC_SEDIMENTS to floor(updatedPlanet.organicSediment - initialPlanet.organicSediment).toInt(),
                Resource.METAL to updatedPlanet.metal - initialPlanet.metal,
                Resource.INFRASTRUCTURE to updatedPlanet.infrastructure,
                Resource.ROCKET_MATERIALS to updatedPlanet.rocketMaterials - initialPlanet.rocketMaterials,
                Resource.INFLUENCE to 0,
                Resource.PROGRESS to updatedPlanet.progress - initialPlanet.progress,
                Resource.DEVELOPMENT to updatedPlanet.development,
                Resource.ARMY to updatedPlanet.army - initialPlanet.army
            )
        )
        return planetResources
    }
}