package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class AccumulateTradepowerUseCase @Inject constructor(){
    operator fun invoke(
        value: Int,
        planet: Planet,
        empire: Empire,
        increaseTradepower: (Int, List<Planet>) -> Unit
    ): Planet {
        var updatedPlanet = planet
        var influence = planet.influence
        var tradepower = empire.tradePower

        if(influence >= value){
            influence -= value
            tradepower += value

            updatedPlanet = planet.copy(
                influence = influence
            )
            val updatedPlanets = empire.planets.map {
                if (it.id == planet.id) {
                    updatedPlanet
                } else {
                    it
                }
            }
            increaseTradepower(tradepower, updatedPlanets)
        }
        return updatedPlanet
    }
}