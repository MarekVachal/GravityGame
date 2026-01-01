package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class BuildNewShipUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Planet{
        val buildingShip = planet.buildingShip
        if (buildingShip == null) return planet
        val militaryCompounds = planet.army
        if(militaryCompounds < BuilderGameConstants.BASE_SHIP_PRICE) return planet
        return planet.copy(
            army = militaryCompounds - BuilderGameConstants.BASE_SHIP_PRICE,
            dockingShip = buildingShip,
            buildingShip = null
        )
    }
}