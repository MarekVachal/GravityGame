package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.core.data.model.TechnologyNode
import javax.inject.Inject

class CreateTechnologicalNodesUseCase @Inject constructor() {
    operator fun invoke(technologies: List<Technology>, setButtonColor:(TechnologyResearchState?) -> Color): List<TechnologyNode>{
        return technologies.map{ technology ->
            val connections = technology.dependencies.map{
                it.name
            }
            TechnologyNode(
                type = technology.type,
                posX = technology.posX,
                posY = technology.posY,
                connections = connections,
                buttonColor = setButtonColor(technology.state)
            )
        }
    }
}