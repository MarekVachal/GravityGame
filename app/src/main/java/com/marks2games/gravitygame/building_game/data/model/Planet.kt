package com.marks2games.gravitygame.building_game.data.model

data class Planet(
    val id: Int = 0,
    val name: String = "Planet 0",
    val level: Int = 1,
    val biomass: Float  = 0.0f,
    val metal: Int = 0,
    val organicSediment: Float = 0.0f,
    val influence: Int = 0,
    val infrastructure: Int = 0,
    val rocketMaterials: Int = 0,
    val transport: Int = 0,
    val progress: Int = 0,
    val development: Int = 0,
    val districts: List<District> = listOf(District.Capitol()),
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "level" to level,
        PlanetResource.BIOMASS.name to biomass,
        PlanetResource.METAL.name to metal,
        PlanetResource.ORGANIC_SEDIMENTS.name to organicSediment,
        PlanetResource.INFLUENCE.name to influence,
        PlanetResource.INFRASTRUCTURE.name to infrastructure,
        PlanetResource.ROCKET_MATERIALS.name to rocketMaterials,
        PlanetResource.TRANSPORT.name to transport,
        PlanetResource.PROGRESS.name to progress,
        PlanetResource.DEVELOPMENT.name to development,
        "districts" to districts.map { it.toMap() }
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Planet {
            @Suppress("UNCHECKED_CAST")
            val districts = (map["districts"] as? List<Map<String, String>>)?.map { District.fromMap(it) } ?: emptyList()
            return Planet(
                id = (map["id"] as Long).toInt(),
                level = (map["level"] as Long).toInt(),
                biomass = (map[PlanetResource.BIOMASS.name] as Double).toFloat(),
                metal = (map[PlanetResource.METAL.name] as Long).toInt(),
                organicSediment = (map[PlanetResource.ORGANIC_SEDIMENTS.name] as Double).toFloat(),
                influence = (map[PlanetResource.INFLUENCE.name] as Long).toInt(),
                infrastructure = (map[PlanetResource.INFRASTRUCTURE.name] as Long).toInt(),
                rocketMaterials = (map[PlanetResource.ROCKET_MATERIALS.name] as Long).toInt(),
                transport = (map[PlanetResource.TRANSPORT.name] as Long).toInt(),
                progress = (map[PlanetResource.PROGRESS.name] as Long).toInt(),
                development = (map[PlanetResource.DEVELOPMENT.name] as Long).toInt(),
                districts = districts
            )
        }
    }
}

enum class PlanetResource {
    BIOMASS,
    METAL,
    ORGANIC_SEDIMENTS,
    INFRASTRUCTURE,
    ROCKET_MATERIALS,
    TRANSPORT,
    PROGRESS,
    DEVELOPMENT,
    INFLUENCE
}
