package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.floor

class AccumulateProgressUseCase @Inject constructor(
) {
    operator fun invoke(value: Int, planet: Planet) : Planet {
        var infrastructure = planet.infrastructure
        var biomassFloat = planet.biomass
        var biomassInt = floor(biomassFloat).toInt()
        var progress = planet.progress
        var planetLevel = planet.level
        if(infrastructure >= value && biomassInt >= value){
            infrastructure -= value
            biomassInt -= value
            progress += value
            biomassFloat -= biomassInt.toFloat()
        }

        if(progress >= planet.level*10){
            planetLevel += 1
            progress -= planetLevel*10
        }

        return planet.copy(
            level = planetLevel,
            progress = progress,
            infrastructure = infrastructure,
            biomass = biomassFloat
        )
    }
}