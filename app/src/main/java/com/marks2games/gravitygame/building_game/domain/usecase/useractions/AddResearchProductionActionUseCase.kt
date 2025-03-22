package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class AddResearchProductionActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, value: Int): List<Action> {
        val action = Action.SetProduction.ResearchProduction(value, planetId)
        return actions + action
    }
}