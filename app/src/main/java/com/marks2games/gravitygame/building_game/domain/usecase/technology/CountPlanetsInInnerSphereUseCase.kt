package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import javax.inject.Inject

class CountPlanetsInInnerSphereUseCase @Inject constructor() {
    operator fun invoke(technologies: List<Technology>): Int{
        val basePlanetsInInnerSphere = BuilderGameConstants.BASE_INNER_SPHERE_PLANET
        val gravityManipulationLevel = (((technologies.find { it.type == TechnologyEnum.GRAVITY_MANIPULATION }) as? Technology.MultiplyTechnology.GravityManipulation)?.level ?: 0 )
        return basePlanetsInInnerSphere + gravityManipulationLevel
    }
}