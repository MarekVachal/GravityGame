package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class DestroyArmyUnitUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Pair<Int, Int> {
        return Pair(planet.army - planet.influence, 0)
    }
}