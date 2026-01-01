package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import javax.inject.Inject

class GetTechnologyPriceUseCase @Inject constructor() {
    operator fun invoke(technologies: List<Technology>?): Int {
        return technologies?.find { it.state == TechnologyResearchState.SELECTED }?.cost ?: 0
    }
}