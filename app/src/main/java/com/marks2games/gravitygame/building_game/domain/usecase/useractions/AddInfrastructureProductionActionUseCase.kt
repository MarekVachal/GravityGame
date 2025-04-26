package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import javax.inject.Inject

class AddInfrastructureProductionActionUseCase @Inject constructor() {
    operator fun invoke(actions: List<Action>, planetId: Int, value: InfrastructureSetting): List<Action> {
        val action = Action.SetProduction.InfrastructureProduction(value, planetId)
        val newActions = actions.toMutableList()
        newActions.add(action)
        return newActions.toList()
    }
}