package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.Trade
import javax.inject.Inject

class AddTradeActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int?, trade: Trade): List<Action> {
        val action = planetId?.let{
            Action.TradeAction(planetId = planetId, setting = trade)
        }
        val newActions = actions.toMutableList()
        val isActionSet = actions.find {it.type == ActionEnum.TRADE_ACTION && it.setting == trade }
        action?.let {
            if(isActionSet == null){
                newActions.add(it)
            } else {
                newActions.add(it)
                newActions.remove(isActionSet)
            }
        }
        return newActions
    }
}