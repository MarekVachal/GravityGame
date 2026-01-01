package com.marks2games.gravitygame.building_game.data.model

data class Empire(
    val hasLaunched: Boolean = false,
    val research: Int = 0,
    val tradePower: Int = 0,
    val army: Int = 0,
    val expeditions: Int = 0,
    val credits: Int = 0,
    val planets: List<Planet>,
    val planetsCount: Int = planets.size,
    val actions: List<Action> = emptyList(),
    val transports: List<Transport> = emptyList(),
    val empireResourcesPossibleIncome: EmpireResources = EmpireResources(),
    val borderForNewPlanet: Int,
    val turns: Int = 0,
    val lastGetPlanet: PlanetType,
    val technologies: List<Technology> = createAllTechnologies()
){
    fun toMap(): Map<String, Any?> = mapOf(
        Resource.RESEARCH.name to research,
        Resource.TRADE_POWER.name to tradePower,
        Resource.ARMY.name to army,
        Resource.EXPEDITIONS.name to expeditions,
        Resource.CREDITS.name to credits,
        "planetsCount" to planetsCount,
        "actions" to actions.map { it.toMap() },
        "transports" to transports.map { it.toMap() },
        "borderForNewPlanet" to borderForNewPlanet,
        "turns" to turns,
        "lastGetPlanet" to lastGetPlanet.name,
        "technologies" to technologies.toFirebaseMap()
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): Empire {
            val actions = (map["actions"] as? List<Map<String, Any>>)
                ?.map { Action.fromMap(it) } ?: emptyList()

            val transports = (map["transports"] as? List<Map<String, Any>>)
                ?.map { Transport.fromMap(it) } ?: emptyList()

            val planetTypeName = map["lastGetPlanet"] as? String ?: "Small"
            val lastGetPlanet = when (planetTypeName) {
                SmallPlanet.name -> SmallPlanet
                MediumPlanet.name -> MediumPlanet
                LargePlanet.name -> LargePlanet
                else -> SmallPlanet
            }
            val techsMap = map["technologies"] as? Map<String, Map<String, Any>> ?: emptyMap()
            val technologies = technologiesFromFirebaseMap(techsMap)

            return Empire(
                research = (map[Resource.RESEARCH.name] as Long).toInt(),
                tradePower = (map[Resource.TRADE_POWER.name] as Long).toInt(),
                army = (map[Resource.ARMY.name] as Long).toInt(),
                expeditions = (map[Resource.EXPEDITIONS.name] as Long).toInt(),
                credits = (map[Resource.CREDITS.name] as Long).toInt(),
                actions = actions,
                transports = transports,
                borderForNewPlanet = (map["borderForNewPlanet"] as? Long)?.toInt() ?: 1000,
                turns = (map["turns"] as? Long)?.toInt() ?: 0,
                lastGetPlanet = lastGetPlanet,
                planets = emptyList(),
                planetsCount = (map["planetsCount"] as Number?)?.toInt()?: 1,
                technologies = technologies
            )
        }
    }
}