package com.marks2games.gravitygame.building_game.data.model

data class Empire(
    val research: Int = 0,
    val tradePower: Int = 0,
    val army: Int = 0,
    val expeditions: Int = 0,
    val credits: Int = 0,
    val planets: List<Planet> = listOf(Planet()),
    val planetsCount: Int = planets.size,
    val actions: List<Action> = emptyList(),
    val transports: List<Transport> = emptyList()
){
    fun toMap(): Map<String, Any> = mapOf(
        Resource.RESEARCH.name to research,
        Resource.TRADE_POWER.name to tradePower,
        Resource.ARMY.name to army,
        Resource.EXPEDITIONS.name to expeditions,
        Resource.CREDITS.name to credits,
        "planetsCount" to planets.size,
        "actions" to actions.map{it.toMap()},
        "transports" to transports.map { it.toMap()}
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): Empire {
            val actions = (map["actions"] as? List<Map<String, Any>>)
            val transports = (map["transports"] as? List<Map<String, Any>>)
            return Empire(
                research = (map[Resource.RESEARCH.name] as Long).toInt(),
                tradePower = (map[Resource.TRADE_POWER.name] as Long).toInt(),
                army = (map[Resource.ARMY.name] as Long).toInt(),
                expeditions = (map[Resource.EXPEDITIONS.name] as Long).toInt(),
                credits = (map[Resource.CREDITS.name] as Long).toInt(),
                planetsCount = (map["planetsCount"] as? Long)?.toInt() ?: 1,
                actions = actions?.map{ Action.fromMap(it)} ?: emptyList(),
                transports = transports?.map{ Transport.fromMap(it)} ?: emptyList()
            )
        }
    }
}

enum class Resource {
    RESEARCH,
    TRADE_POWER,
    ARMY,
    CREDITS,
    EXPEDITIONS,
    BIOMASS,
    METAL,
    ORGANIC_SEDIMENTS,
    INFRASTRUCTURE,
    ROCKET_MATERIALS,
    PROGRESS,
    DEVELOPMENT,
    INFLUENCE
}