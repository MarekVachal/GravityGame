package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Trade
import javax.inject.Inject

class AddTradeActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, trade: Trade): List<Action> {
        val action = Action.TradeAction(planetId = planetId, trade = trade)
        val newActions = actions.toMutableList()
        newActions.add(action)
        return newActions.toList()
    }
}