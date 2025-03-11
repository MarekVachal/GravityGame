package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import javax.inject.Inject

class TradeUseCase @Inject constructor(
    private val empireRepository: EmpireRepository
) {
    suspend operator fun invoke(
        empire: Empire,
        tradeState: Trade
    ): Empire {
        val updatedEmpire = empire
        val planets = empire.planets
        val tradepower = tradeState.tradepower


        empireRepository.updateEmpire(updatedEmpire)
        return updatedEmpire
    }


}