package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import javax.inject.Inject

class IsTechnologyResearchedUseCase @Inject constructor() {
    operator fun invoke(technology: TechnologyEnum, technologies: List<Technology>?): Boolean {
        return technologies?.let{
            (it.find{it.type == technology })?.state == TechnologyResearchState.FINISHED
        } == true
    }
}