package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireResource
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResource
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class CreateArmyUnitUseCase @Inject constructor(
    private val empireRepository: EmpireRepository,
    private val planetRepository: PlanetRepository
) {
    suspend operator fun invoke(
        value: Int,
        planet: Planet,
        empire: Empire,
        createArmyUnit: (Int, List<Planet>) -> Unit
    ): Planet{
        var rocketMaterial = planet.rocketMaterials
        var army = empire.army
        var updatedPlanet = planet

        if(rocketMaterial >= value) {
            rocketMaterial -= value
            army += value
            updatedPlanet = planet.copy(rocketMaterials = rocketMaterial)
            val planets = empire.planets.map {
                if (it.id == planet.id) {
                    updatedPlanet
                } else {
                    it
                }
            }
            planetRepository.updatePlanetResource(
                planetId = planet.id,
                resource = PlanetResource.ROCKET_MATERIALS,
                value = rocketMaterial.toDouble()
            )
            empireRepository.updateEmpireResource(
                resource = EmpireResource.ARMY,
                value = army.toDouble()
            )
            createArmyUnit(army, planets)
        }
        return updatedPlanet
    }
}