package com.marks2games.gravitygame.building_game.data.model

data class Trade(
    val capacity: Int = 5,
    val tradepower: Int = 0,
    val credits: Int = 0,
    val metalToBuy: Int = 0,
    val organicSedimentToBuy: Float = 0f,
    val rocketMaterialsToBuy: Int = 0,
    val researchToBuy: Int = 0,
    val metalToSell: Int = 0,
    val organicSedimentToSell: Float = 0f,
    val rocketMaterialsToSell: Int = 0,
    val researchToSell: Int = 0
)
