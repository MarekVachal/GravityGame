package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class IsRemoveButtonEnabledUseCase @Inject constructor() {
    operator fun invoke(planet: Planet?, resource: Resource, map: Map<Resource, Int>): Boolean{
        return planet != null && if (resource == Resource.ORGANIC_SEDIMENTS) {
            (map[resource] ?: 0) != 0
        } else if (resource == Resource.METAL) {
            (map[resource] ?: 0) != 0
        } else {
            (map[resource] ?: 0) != 0
        }
    }
}