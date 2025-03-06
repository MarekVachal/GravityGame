package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class BuildDistrictUseCase @Inject constructor(
    private val planetRepository: PlanetRepository
){
    suspend operator fun invoke(planet: Planet, districtToBuild: DistrictEnum): Planet {
        var infrastructure = planet.infrastructure
        val planetLevel = planet.level
        val districtsCounts = planet.districts.size
        val districts = planet.districts.toMutableList()

        if(districtsCounts == planetLevel || infrastructure < 5){
            return planet
        } else {
            val chosenDistrict = when(districtToBuild){
                DistrictEnum.CAPITOL -> return planet
                DistrictEnum.PROSPECTORS -> District.Prospectors()
                DistrictEnum.EMPTY -> District.Empty()
                DistrictEnum.INDUSTRIAL -> District.Industrial()
                DistrictEnum.EXPEDITION_PLATFORM -> District.ExpeditionPlatform()
                DistrictEnum.URBAN_CENTER -> District.UrbanCenter()
            }
            if(districts.any {it.type == DistrictEnum.EXPEDITION_PLATFORM} ) return planet
            infrastructure -= 5
            districts.add(chosenDistrict)
        }

        val updatedPlanet = planet.copy(
            infrastructure = infrastructure,
            districts = districts
        )

        planetRepository.updatePlanet(updatedPlanet)
        return updatedPlanet
    }
}