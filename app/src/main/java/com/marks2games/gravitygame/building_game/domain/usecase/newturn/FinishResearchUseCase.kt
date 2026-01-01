package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.building_game.domain.usecase.technology.GetTechnologyPriceUseCase
import javax.inject.Inject

class FinishResearchUseCase @Inject constructor(
    private val getTechnologyPrice: GetTechnologyPriceUseCase
) {
    operator fun invoke(finishedTechnology: TechnologyEnum?, empire: Empire): Empire {
        val finishedTechnology = empire.technologies.find { it.type == finishedTechnology }

        if (finishedTechnology == null) return empire

        val updatedWithFinished = empire.technologies.map { tech ->
            if (tech.type == finishedTechnology.type) {
                tech.changeTechnologyState(TechnologyResearchState.FINISHED)
            } else {
                tech
            }
        }

        val updatedTechnologies = updatedWithFinished.map { tech ->
            if (tech.state == TechnologyResearchState.LOCKED) {
                val allDepsFinished = tech.dependencies.all { dependencyType ->
                    updatedWithFinished.find { it.type == dependencyType }?.state == TechnologyResearchState.FINISHED
                }
                if (allDepsFinished) {
                    tech.changeTechnologyState(TechnologyResearchState.UNLOCKED)
                } else {
                    tech
                }
            } else {
                tech
            }
        }

        val newResearch = empire.research - getTechnologyPrice.invoke(empire.technologies)

        return empire.copy(
            technologies = updatedTechnologies,
            research = newResearch
        )
    }
}