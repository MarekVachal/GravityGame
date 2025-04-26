package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireResources
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class UpdatePossibleEmpireResourcesIncomeUseCase @Inject constructor() {
    operator fun invoke(updatedEmpire: Empire, initialEmpire: Empire): EmpireResources{
        val empireResources = EmpireResources(
            mapOf(
                Resource.RESEARCH to updatedEmpire.research - initialEmpire.research,
                Resource.CREDITS to updatedEmpire.credits - initialEmpire.credits,
                Resource.EXPEDITIONS to updatedEmpire.expeditions - initialEmpire.expeditions,
                Resource.TRADE_POWER to updatedEmpire.tradePower - initialEmpire.tradePower
            )
        )
        return empireResources
    }
}