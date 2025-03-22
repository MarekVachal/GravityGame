package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.domain.error.NewTurnError
import io.sentry.Sentry

data class Planet(
    val id: Int = 0,
    val name: String = "Planet 0",
    val type: PlanetType = PlanetType.SMALL,
    val level: Int = 1,
    val planetMetal: Int = 100,
    val planetOrganicSediments: Float = 0f,
    //val resources: MutableMap<Resource, Float> = mutableMapOf(),
    val biomass: Float  = 0.0f,
    val metal: Int = 0,
    val organicSediment: Float = 0.0f,
    val influence: Int = 0,
    val infrastructure: Int = 0,
    val rocketMaterials: Int = 0,
    val progress: Int = 0,
    val development: Int = 0,
    val army: Int = 0,
    val progressSetting: Int = 0,
    val researchSetting: Int = 0,
    val expeditionsSetting: Int = 0,
    val armyConstructionSetting: Int = 0,
    val rocketMaterialsSetting: RocketMaterialsSetting = RocketMaterialsSetting.MAXIMUM,
    val infrastructureSetting: InfrastructureSetting = InfrastructureSetting.MAXIMUM,
    val districts: List<District> = listOf(District.Capitol()),
    val actions: List<Action> = emptyList(),
    val empireResources: EmpireResources = EmpireResources(),
    val errors: List<NewTurnError> = emptyList()
) {

    /*
    fun getResource(resource: Resource): Float{
        return resources[resource] ?: 0f
    }

    fun decreaseResource(costs: Map<Resource, Float>): Planet {
        val newResources = resources.mapValues { (key, value) -> value - (costs[key] ?: 0f) }
        return copy(resources = newResources.toMutableMap())
    }

    fun increaseResource(increase: Map<Resource, Float>): Planet {
        val newResources = resources.mapValues { (key, value) -> value + (increase[key] ?: 0f) }
        return copy(resources = newResources.toMutableMap())
    }

     */

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "level" to level,
        "planetMetal" to planetMetal,
        "planetOrganicSediments" to planetOrganicSediments,
        "type" to type.name,
        Resource.BIOMASS.name to biomass,
        Resource.METAL.name to metal,
        Resource.ORGANIC_SEDIMENTS.name to organicSediment,
        Resource.INFLUENCE.name to influence,
        Resource.INFRASTRUCTURE.name to infrastructure,
        Resource.ROCKET_MATERIALS.name to rocketMaterials,
        Resource.PROGRESS.name to progress,
        Resource.DEVELOPMENT.name to development,
        Resource.ARMY.name to army,
        "progressSetting" to progressSetting,
        "researchSetting" to researchSetting,
        "expeditionsSetting" to expeditionsSetting,
        "armyConstructionSetting" to armyConstructionSetting,
        "rocketMaterialsSetting" to rocketMaterialsSetting,
        "infrastructureSetting" to infrastructureSetting,
        "districts" to districts.map { it.toMap() },
        "actions" to actions.map { it.toMap() },
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): Planet {
            val districts = (map["districts"] as? List<Map<String, String>>)?.map { District.fromMap(it) } ?: emptyList()
            val actions = (map["actions"] as? List<Map<String, Any>>)?. map { Action.fromMap(it) }?: emptyList()
            return Planet(
                id = (map["id"] as Long).toInt(),
                name = map["name"] as String,
                type = (map["type"] as String).toPlanetType() ?: PlanetType.SMALL,
                level = (map["level"] as Long).toInt(),
                planetMetal = (map["planetMetal"] as Long).toInt(),
                planetOrganicSediments = (map["planetOrganicSediments"] as Double).toFloat(),
                //resources = map["resources"] as MutableMap<Resource, Float>,
                biomass = (map[Resource.BIOMASS.name] as Double).toFloat(),
                metal = (map[Resource.METAL.name] as Long).toInt(),
                organicSediment = (map[Resource.ORGANIC_SEDIMENTS.name] as Double).toFloat(),
                influence = (map[Resource.INFLUENCE.name] as Long).toInt(),
                infrastructure = (map[Resource.INFRASTRUCTURE.name] as Long).toInt(),
                rocketMaterials = (map[Resource.ROCKET_MATERIALS.name] as Long).toInt(),
                progress = (map[Resource.PROGRESS.name] as Long).toInt(),
                development = (map[Resource.DEVELOPMENT.name] as Long).toInt(),
                army = (map[Resource.ARMY.name] as Long).toInt(),
                progressSetting = (map["progressSetting"] as Long).toInt(),
                researchSetting = (map["researchSetting"] as Long).toInt(),
                expeditionsSetting = (map["expeditionsSetting"] as Long).toInt(),
                armyConstructionSetting = (map["armyConstructionSetting"] as Long).toInt(),
                rocketMaterialsSetting = (map["rocketMaterialsSetting"] as String?)
                    ?.toRocketMaterialsSettingEnum()
                    ?: RocketMaterialsSetting.USAGE,
                infrastructureSetting = (map["infrastructureSetting"] as String?)
                    ?.toInfrastructureSettingEnum()
                    ?: InfrastructureSetting.USAGE,
                districts = districts,
                actions = actions
            )
        }
    }
}

enum class PlanetType{
    TINY, SMALL, MEDIUM, LARGE
}

fun String.toPlanetType(): PlanetType? {
    return try{
        PlanetType.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}