package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import javax.inject.Inject

class UpdateResearchingTechnologyUseCase @Inject constructor() {
    operator fun invoke(setTechnology: TechnologyEnum, nodes: List<Technology>): List<Technology> {
        return nodes.map { node ->
            if (node.type == setTechnology && node.state == TechnologyResearchState.UNLOCKED) {
                node.changeTechnologyState(TechnologyResearchState.SELECTED)
            } else if (node.type == setTechnology && node.state == TechnologyResearchState.SELECTED) {
                node.changeTechnologyState(TechnologyResearchState.UNLOCKED)
            } else {
                node
            }
        }
    }
}