package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.Technology.SynergyTechnology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import javax.inject.Inject

class ApplySynergyUseCase @Inject constructor(
    private val isTechnologyResearched: IsTechnologyResearchedUseCase
) {
    operator fun invoke(
        resource: Resource,
        districtId: Int,
        planet: Planet,
        technology: TechnologyEnum,
        technologies: List<Technology>
    ): Int{
        if(!isTechnologyResearched.invoke(technology, technologies)) return 0
        val tech = technologies.find { it.type == technology } as SynergyTechnology
        val districtForBonus = tech.bonusGainByDistrict
        val districtNode = planet.planetMapConfig.find {it.id.toInt() == districtId} ?: return 0
        val districtCount = getNumberOfCountDistrict(planet, districtForBonus, districtNode.connections )
        val baseBonus = tech.bonusToResources[resource]?:0
        return districtCount * baseBonus
    }
}

private fun getNumberOfCountDistrict(planet: Planet, district: DistrictEnum, connections: List<String>): Int{
    return connections.count { connection ->
        (planet.districts.find {
            connection.toInt() == it.districtId
        })?.type == district
    }
}