package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CanPlanet2ExportUseCase @Inject constructor() {
    operator fun invoke(planet: Planet?): Boolean {
        return planet?.districts?.any { it is District.ExpeditionPlatform } == true
    }
}