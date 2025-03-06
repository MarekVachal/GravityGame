package com.marks2games.gravitygame.building_game.data.model

data class Transport(
    val planet1Id: Int? = null,
    val planet2Id: Int? = null,
    val planet1Metal: Int = 0,
    val planet1OrganicSediments: Float = 0f,
    val planet1RocketMaterials: Int = 0,
    val planet2Metal: Int = 0,
    val planet2OrganicSediments: Float = 0f,
    val planet2RocketMaterials: Int = 0
)