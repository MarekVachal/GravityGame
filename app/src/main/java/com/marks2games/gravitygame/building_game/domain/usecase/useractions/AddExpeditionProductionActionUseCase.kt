package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import javax.inject.Inject

class AddExpeditionProductionActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, value: Int): List<Action> {
        val action = Action.SetProduction.ExpeditionProduction(value, planetId)
        return actions + action
    }
}