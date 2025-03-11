package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.CreateNewPlanetUseCase
import javax.inject.Inject
import kotlin.math.pow

class AccumulateExpeditionsUseCase @Inject constructor(
    private val createNewPlanetUseCase: CreateNewPlanetUseCase
) {
    operator fun invoke(
        value: Int,
        planet: Planet,
        empire: Empire,
        increaseExpeditions: (Float, List<Planet>) -> Unit
    ): Planet {
        var updatedPlanet = planet
        val planets = empire.planets.toMutableList()
        var rocketMaterial = planet.rocketMaterials
        var expeditions = empire.expeditions
        val planetCost = (50 * 1.15.pow(empire.planetsCount)).toFloat()
        if (rocketMaterial >= value){
            rocketMaterial -= value
            expeditions += value.toFloat()
            updatedPlanet = planet.copy(
                rocketMaterials = rocketMaterial
            )
            planets.map {
                if (it.id == planet.id){
                    updatedPlanet
                } else {
                    it
                }
            }
            if (expeditions >= planetCost){
                expeditions -= planetCost
                planets.add(createNewPlanetUseCase.invoke(empire.planetsCount))
            }
        }
        increaseExpeditions(expeditions, planets.toList()) //Spíš vracet Empire a toto volat jinde
        return updatedPlanet
    }
}