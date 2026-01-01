package com.marks2games.gravitygame.building_game.data.model

data class Transport(
    val transportId: Int = 0,
    val planet1Id: Int? = null,
    val planet2Id: Int? = null,
    val exportFromPlanet1: Map<Resource, Int> = emptyMap(),
    val exportFromPlanet2: Map<Resource, Int> = emptyMap(),
    val cost: Float = 0f,
    val isSuccessOut: Boolean = false,
    val isLongTime: Boolean = true
) {
    fun toMap(): Map<String, Any> = buildMap {
        put("transportId", transportId)
        put("isLongTime", isLongTime)
        planet1Id?.let { put("planet1Id", it.toLong()) }
        planet2Id?.let { put("planet2Id", it.toLong()) }
        put("exportFromPlanet1", exportFromPlanet1)
        put("exportFromPlanet2", exportFromPlanet2)
        put("isSuccessOut", isSuccessOut)
        put("cost", cost)

    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): Transport {
            return Transport(
                planet1Id = (map["planet1Id"] as? Long)?.toInt(),
                planet2Id = (map["planet2Id"] as? Long)?.toInt(),
                isLongTime = (map["isLongTime"] as? Boolean == true),
                isSuccessOut = (map["isSuccessOut"] as? Boolean == true),
                cost = (map["cost"] as? Double)?.toFloat() ?: 0f,
                exportFromPlanet1 = map["exportFromPlanet1"] as? Map<Resource, Int> ?: emptyMap(),
                exportFromPlanet2 = map["exportFromPlanet2"] as? Map<Resource, Int> ?: emptyMap(),

            )
        }
    }
}