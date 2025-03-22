package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class AddTransportActionUseCase @Inject constructor()  {
    operator fun invoke(actions: List<Action>, planetId: Int, transport: Transport): List<Action> {
        val action = Action.TransportAction(planetId, transport)
        return actions + action
    }
}