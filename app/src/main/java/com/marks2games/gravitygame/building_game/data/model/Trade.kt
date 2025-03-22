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
){
    fun toMap(): Map<String, Any> = mapOf(
        "capacity" to capacity,
        "tradepower" to tradepower,
        "credits" to credits,
        "metalToBuy" to metalToBuy,
        "organicSedimentToBuy" to organicSedimentToBuy,
        "rocketMaterialsToBuy" to rocketMaterialsToBuy,
        "researchToBuy" to researchToBuy,
        "metalToSell" to metalToSell,
        "organicSedimentToSell" to organicSedimentToSell,
        "rocketMaterialsToSell" to rocketMaterialsToSell,
        "researchToSell" to researchToSell
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Trade{
            return Trade(
                capacity = (map["capacity"] as Long).toInt(),
                tradepower = (map["tradepower"] as Long).toInt(),
                credits = (map["credits"] as Long).toInt(),
                metalToBuy = (map["metalToBuy"] as Long).toInt(),
                organicSedimentToBuy = (map["organicSedimentToBuy"] as Double).toFloat(),
                rocketMaterialsToBuy = (map["rocketMaterialsToBuy"] as Long).toInt(),
                researchToBuy = (map["researchToBuy"] as Long).toInt(),
                metalToSell = (map["metalToSell"] as Long).toInt(),
                organicSedimentToSell = (map["organicSedimentToSell"] as Double).toFloat(),
                rocketMaterialsToSell = (map["rocketMaterialsToSell"] as Long).toInt(),
                researchToSell = (map["researchToSell"] as Long).toInt()
            )
        }
    }
}
