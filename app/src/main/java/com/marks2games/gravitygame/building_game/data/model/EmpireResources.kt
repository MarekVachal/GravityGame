package com.marks2games.gravitygame.building_game.data.model

data class EmpireResources(
    val resources: Map<Resource, Int> = mapOf(
        Resource.RESEARCH to 0,
        Resource.CREDITS to 0,
        Resource.EXPEDITIONS to 0,
        Resource.TRADE_POWER to 0
    )
)
