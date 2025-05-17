package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class GetResourceValueUseCase @Inject constructor(){
    operator fun invoke(producedResource: Resource, isMetalForProspectors: Boolean): Triple<Int?, Int?, Int?>{

        return when(producedResource){
            Resource.RESEARCH -> {
                val resourceChange = District.UrbanCenter(districtId = 0, mode = UrbanCenterMode.RESEARCH).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.RESEARCH] ?: 0, resourceChange.consumed[Resource.BIOMASS] ?: 0)
                Triple(simplify[0], simplify[1], null)

            }
            Resource.TRADE_POWER -> Triple(1, 1, null)
            Resource.ARMY -> {
                val resourceChange = District.ExpeditionPlatform(districtId = 0).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.ARMY] ?: 0, resourceChange.consumed[Resource.ARMY] ?: 0)
                Triple(simplify[0], simplify[1], null)

            }
            Resource.CREDITS -> Triple(null, null, null)
            Resource.EXPEDITIONS -> {
                val resourceChange = District.ExpeditionPlatform(districtId = 0).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.EXPEDITIONS] ?: 0, resourceChange.consumed[Resource.EXPEDITIONS] ?: 0)
                Triple(simplify[0], simplify[1], null)
            }
            Resource.BIOMASS -> Triple(null, null, null)
            Resource.METAL -> {
                if(isMetalForProspectors){
                    val resourceChange = District.Prospectors(districtId = 0, mode = ProspectorsMode.METAL).generateResources()
                    return Triple(resourceChange.produced[Resource.METAL]?: 0, null, null)
                } else {
                    val resourceChange = District.Industrial(districtId = 0, mode = IndustrialMode.METAL).generateResources()
                    val simplify = simplify(resourceChange.produced[Resource.METAL] ?: 0, resourceChange.consumed[Resource.ORGANIC_SEDIMENTS] ?: 0)
                    Triple(simplify[0], simplify[1], null)
                }
            }
            Resource.ORGANIC_SEDIMENTS -> {
                val resourceChange = District.Prospectors(districtId = 0, mode = ProspectorsMode.ORGANIC_SEDIMENTS).generateResources()
                Triple(resourceChange.produced[Resource.ORGANIC_SEDIMENTS]?: 0, null, null)
            }
            Resource.INFRASTRUCTURE -> {
                val resourceChange = District.Industrial(districtId = 0, mode = IndustrialMode.INFRASTRUCTURE).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.INFRASTRUCTURE] ?: 0, resourceChange.consumed[Resource.METAL] ?: 0)
                Triple(simplify[0], simplify[1], null)
            }
            Resource.ROCKET_MATERIALS -> {
                val resourceChange = District.Industrial(districtId = 0, mode = IndustrialMode.ROCKET_MATERIALS).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.ROCKET_MATERIALS] ?: 0, resourceChange.consumed[Resource.ORGANIC_SEDIMENTS] ?: 0, resourceChange.consumed[Resource.METAL]?: 0)
                Triple(simplify[0], simplify[1], simplify[2])
            }
            Resource.PROGRESS -> {
                val resourceChange = District.Capitol(districtId = 0).generateResources()
                val simplify = simplify(resourceChange.produced[Resource.PROGRESS]?: 0, resourceChange.consumed[Resource.INFRASTRUCTURE]?: 0, resourceChange.consumed[Resource.BIOMASS]?: 0)
                Triple(simplify[0], simplify[1], simplify[2])
            }
            Resource.DEVELOPMENT -> Triple(1, 1, null)
            Resource.INFLUENCE -> {
                val resourceChange = District.UrbanCenter(districtId = 0, mode = UrbanCenterMode.INFLUENCE).generateResources()
                Triple(resourceChange.produced[Resource.INFLUENCE]?: 0, null, null)

            }
        }
    }

    fun simplify(vararg numbers: Int): List<Int> {
        if (numbers.isEmpty()) throw IllegalArgumentException("Musíš zadat alespoň jedno číslo.")

        fun gcd(a: Int, b: Int): Int {
            return if (b == 0) a else gcd(b, a % b)
        }

        val commonGcd = numbers.reduce { acc, num -> gcd(acc, num) }

        return numbers.map { it / commonGcd }
    }
}