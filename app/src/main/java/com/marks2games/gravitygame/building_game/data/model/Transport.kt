package com.marks2games.gravitygame.building_game.data.model

data class Transport(
    val transportId: Int,
    val planet1Id: Int? = null,
    val planet2Id: Int? = null,
    val exportFromPlanet1: Map<Resource, Int> = emptyMap(),
    val exportFromPlanet2: Map<Resource, Int> = emptyMap(),
    val isSuccessOut: Boolean = false,
    val isLongTime: Boolean = true
) {
    fun toMap(): Map<String, Any> = buildMap {
        put("transportId", transportId)
        put("isLongTime", isLongTime)
        planet1Id?.let { put("planet1Id", it.toLong()) }
        planet2Id?.let { put("planet2Id", it.toLong()) }
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Transport {
            return Transport(
                planet1Id = (map["planet1Id"] as? Long)?.toInt(),
                planet2Id = (map["planet2Id"] as? Long)?.toInt(),
                isLongTime = (map["isLongTime"] as? Boolean == true),
                transportId = TODO(),
                exportFromPlanet1 = TODO(),
                exportFromPlanet2 = TODO(),
                isSuccessOut = TODO()
            )
        }
    }
}