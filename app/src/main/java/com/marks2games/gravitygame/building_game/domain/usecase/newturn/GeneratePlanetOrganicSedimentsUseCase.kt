package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.BIOMASS_CONTRIBUTION_COEFFICIENT
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.floor

class GeneratePlanetOrganicSedimentsUseCase @Inject constructor(){
    /**
     * Generates new amount of organic sediments for a planet based on its existing organic sediment level and biomass.
     *
     * @param planet The planet for which to generate organic sediments.
     * @return The new amount of organic sediments.
     */
    operator fun invoke(planet: Planet): Float{
        return floor(planet.biomass / BIOMASS_CONTRIBUTION_COEFFICIENT)
    }
}