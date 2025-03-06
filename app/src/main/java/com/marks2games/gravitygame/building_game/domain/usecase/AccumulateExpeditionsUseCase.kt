package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireResource
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResource
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject
import kotlin.math.pow

class AccumulateExpeditionsUseCase @Inject constructor(
    private val planetRepository: PlanetRepository,
    private val empireRepository: EmpireRepository,
    private val createNewPlanetUseCase: CreateNewPlanetUseCase
) {
    suspend operator fun invoke(
        value: Int,
        planet: Planet,
        empire: Empire,
        increaseExpeditions: (Float, List<Planet>) -> Unit
    ): Planet{
        var updatedPlanet = planet
        val planets = empire.planets.toMutableList()
        var rocketMaterial = planet.rocketMaterials
        var expeditions = empire.expeditions
        val planetCost = (50 * 1.15.pow(empire.planetsCount)).toFloat()

        if (rocketMaterial >= value){
            rocketMaterial -= value
            expeditions += value.toFloat()
            planetRepository.updatePlanetResource(
                planetId = planet.id,
                resource = PlanetResource.ROCKET_MATERIALS,
                value = rocketMaterial.toDouble()
            )
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
                val updatedEmpire = empire.copy(
                    planets = planets.toList(),
                    expeditions = expeditions,
                    planetsCount = empire.planetsCount + 1
                )
                empireRepository.updateEmpire(updatedEmpire)
            } else {
                empireRepository.updateEmpireResource(
                    resource = EmpireResource.EXPEDITIONS,
                    value = expeditions.toDouble()
                )
            }
        }
        increaseExpeditions(expeditions, planets.toList())
        return updatedPlanet
    }
}