package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.core.data.model.TechnologyNode
import javax.inject.Inject

class CreateTechnologicalNodesUseCase @Inject constructor() {
    operator fun invoke(technologies: List<Technology>, setButtonColor:(TechnologyResearchState?) -> Color): List<TechnologyNode>{
        return technologies.map{
            val connections = it.dependencies.map{
                it.name
            }
            TechnologyNode(
                type = it.type,
                posX = it.posX,
                posY = it.posY,
                connections = connections,
                buttonColor = setButtonColor(it.state)
            )
        }
    }
}