package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.IsTechnologyResearchedUseCase
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import javax.inject.Inject

class GetLockedShipsToBuildUseCase @Inject constructor(
    private val isTechnologyResearched: IsTechnologyResearchedUseCase
) {
    operator fun invoke(technologies: List<Technology>): Set<ShipType>{
        val isCruiserResearched = isTechnologyResearched.invoke(TechnologyEnum.CRUISER_TECHNOLOGY, technologies)
        val isDestroyerResearched = isTechnologyResearched.invoke(TechnologyEnum.DESTROYER_TECHNOLOGY, technologies)
        val isGhostResearched = isTechnologyResearched.invoke(TechnologyEnum.GHOST_TECHNOLOGY, technologies)
        val isWarperResearched = isTechnologyResearched.invoke(TechnologyEnum.WARPER_TECHNOLOGY, technologies)

        val set: MutableSet<ShipType> = mutableSetOf()
        if (!isCruiserResearched) set.add(ShipType.CRUISER)
        if (!isDestroyerResearched) set.add(ShipType.DESTROYER)
        if (!isGhostResearched) set.add(ShipType.GHOST)
        if (!isWarperResearched) set.add(ShipType.WARPER)

        return set
    }
}