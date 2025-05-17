package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.FinishResearchUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.GetTechnologyPriceUseCase
import javax.inject.Inject

class CheckForResearchFinishUseCase @Inject constructor(
    private val finishResearch: FinishResearchUseCase,
    private val getTechnologyPrice: GetTechnologyPriceUseCase
) {
    operator fun invoke(empire: Empire): Empire{
        val technologyPrice = getTechnologyPrice.invoke(empire.technologies)
        val finishTechnology = empire.technologies.find { it.state == TechnologyResearchState.SELECTED }
        if (empire.research >= technologyPrice){
            return finishResearch.invoke(finishTechnology?.type, empire)
        }

        return empire
    }
}