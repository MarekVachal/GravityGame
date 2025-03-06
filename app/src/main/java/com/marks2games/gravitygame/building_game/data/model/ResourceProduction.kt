package com.marks2games.gravitygame.building_game.data.model

data class ResourceProduction(
    val biomass: Float,
    val organicSediment: Float,
    val metal: Int,
    val rocketMaterials: Int,
    val influence: Int,
    val infrastructure: Int,
    val progress: Int,
    val planetLevel: Int
)
