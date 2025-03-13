package com.marks2games.gravitygame.building_game.data.model

import kotlin.math.floor
import kotlin.math.min

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
    val progressSetting: Int = min(infrastructure, floor(biomass).toInt()),
    val researchSetting: Int = 0,
    val expeditionsSetting: Int = 0,
    val armyConstructionSetting: Int = 0,
    val rocketMaterialsSetting: RocketMaterialsSetting = RocketMaterialsSetting.MAXIMUM,
    val infrastructureSetting: InfrastructureSetting = InfrastructureSetting.MAXIMUM,
    val districts: List<District> = listOf(District.Capitol()),
    val actions: List<Action> = emptyList()
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
        "progressSetting" to progressSetting,
        "researchSetting" to researchSetting,
        "expeditionsSetting" to expeditionsSetting,
        "armyConstructionSetting" to armyConstructionSetting,
        "rocketMaterialsSetting" to rocketMaterialsSetting,
        "infrastructureSetting" to infrastructureSetting,
        "districts" to districts.map { it.toMap() },
        "actions" to actions.map {it.toMap()}
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Planet {
            @Suppress("UNCHECKED_CAST")
            val districts = (map["districts"] as? List<Map<String, String>>)?.map { District.fromMap(it) } ?: emptyList()
            val actions = (map["actions"] as? List<Map<String, String>>)?.map { Action.fromMap(it) } ?: emptyList()
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
                progressSetting = (map["progressSetting"] as Long).toInt(),
                researchSetting = (map["researchSetting"] as Long).toInt(),
                expeditionsSetting = (map["expeditionsSetting"] as Long).toInt(),
                armyConstructionSetting = (map["armyConstructionSetting"] as Long).toInt(),
                rocketMaterialsSetting = (map["rocketMaterialsSetting"] as RocketMaterialsSetting),
                infrastructureSetting = (map["infrastructureSetting"] as InfrastructureSetting),
                districts = districts,
                actions = actions
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
