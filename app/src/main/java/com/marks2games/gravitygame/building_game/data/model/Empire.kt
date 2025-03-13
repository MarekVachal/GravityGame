package com.marks2games.gravitygame.building_game.data.model

import io.sentry.Sentry

data class Empire(
    val research: Int = 0,
    val tradePower: Int = 0,
    val army: Int = 0,
    val expeditions: Float = 0f,
    val credits: Int = 0,
    val savedTurns: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val planets: List<Planet> = listOf(Planet()),
    val planetsCount: Int = 1,
){
    fun toMap(): Map<String, Any> = mapOf(
        EmpireResource.RESEARCH.name to research,
        EmpireResource.TRADE_POWER.name to tradePower,
        EmpireResource.ARMY.name to army,
        EmpireResource.EXPEDITIONS.name to expeditions,
        EmpireResource.CREDITS.name to credits,
        "lastUpdated" to lastUpdated,
        "planetsCount" to planets.size,
        "savedTurns" to savedTurns
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Empire {
            return Empire(
                research = (map[EmpireResource.RESEARCH.name] as Long).toInt(),
                tradePower = (map[EmpireResource.TRADE_POWER.name] as Long).toInt(),
                army = (map[EmpireResource.ARMY.name] as Long).toInt(),
                expeditions = (map[EmpireResource.EXPEDITIONS.name] as Float),
                credits = (map[EmpireResource.CREDITS.name] as Long).toInt(),
                lastUpdated = map["lastUpdated"] as Long,
                planetsCount = (map["planetsCount"] as? Long)?.toInt() ?: 0,
                savedTurns = (map["savedTurns"] as? Long)?.toInt() ?: 0
            )
        }
    }
}

enum class EmpireResource {
    RESEARCH, TRADE_POWER, ARMY, CREDITS, EXPEDITIONS
}