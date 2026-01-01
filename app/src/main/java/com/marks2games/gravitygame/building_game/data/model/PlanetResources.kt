package com.marks2games.gravitygame.building_game.data.model

data class PlanetResources(
    val resources: Map<Resource, Int> = mapOf(
        Resource.BIOMASS to 0,
        Resource.ORGANIC_SEDIMENTS to 0,
        Resource.METAL to 0,
        Resource.INFRASTRUCTURE to 0,
        Resource.ROCKET_MATERIALS to 0,
        Resource.INFLUENCE to 0,
        Resource.PROGRESS to 0,
        Resource.DEVELOPMENT to 0,
        Resource.ARMY to 0
    )
)
