package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class GetUnlockedProductionModesUseCase @Inject constructor(
    private val isTechnologyResearched: IsTechnologyResearchedUseCase
) {
    operator fun invoke(technologies: List<Technology>, district: District?): List<Enum<*>>{
        var modes = (district?.getModes() ?: emptyList()).toMutableList()
        when(district){
            is District.Industrial -> {
                val isRocketScienceResearch = isTechnologyResearched.invoke(TechnologyEnum.ROCKET_SCIENCE, technologies)
                val isSyntheticMaterialsResearched = isTechnologyResearched.invoke(TechnologyEnum.SYNTHETIC_MATERIALS, technologies)
                if(!isSyntheticMaterialsResearched && !isRocketScienceResearch) {
                    modes.removeAll {
                        it == IndustrialMode.ROCKET_MATERIALS || it == IndustrialMode.METAL
                    }
                } else if (!isSyntheticMaterialsResearched){
                    modes.remove(IndustrialMode.METAL)
                } else if (!isRocketScienceResearch){
                    modes.remove(IndustrialMode.ROCKET_MATERIALS)
                } else {
                    modes
                }
            }
            is District.Prospectors -> {
                val isRocketScienceResearch = isTechnologyResearched.invoke(TechnologyEnum.ROCKET_SCIENCE, technologies)
                if(!isRocketScienceResearch) {
                    modes.remove(ProspectorsMode.ORGANIC_SEDIMENTS)
                } else {
                    modes
                }
            }
            is District.UrbanCenter -> {
                val isInfluenceResearch = isTechnologyResearched.invoke(TechnologyEnum.INFLUENCE_TECHNOLOGY, technologies)
                if(!isInfluenceResearch){
                    modes.remove(UrbanCenterMode.INFLUENCE)
                } else {
                    modes
                }
            }
            else -> modes
        }
        return modes
    }
}