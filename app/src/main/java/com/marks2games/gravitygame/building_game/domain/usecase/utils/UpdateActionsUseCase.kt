package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class UpdateActionsUseCase @Inject constructor() {
    operator fun invoke(
        newList: List<Action>,
        oldList: List<Action>,
        planetId: Int?
    ): List<Action>{
        return planetId?.let {
            val updatedOldList = oldList.filter { it.planetId != planetId }
            updatedOldList + newList
        } ?: oldList
    }
}