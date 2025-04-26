package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.domain.error.NewTurnError

data class PlanetType(
    val name: String,
    val planetMetal: Int,
    val biomassCapacityBonus: Float,
    val maxLevel: Int,
    val startingLevel: Int,
    val capacityPlanetOS: Int,
    val districts: List<District> = listOf(
        District.Capitol(),
        District.Empty(districtId = 1),
        District.Empty(districtId = 2),
        District.Empty(districtId = 3)
    )
)

val SmallPlanet = PlanetType(
    name = "Small",
    planetMetal = 3000,
    biomassCapacityBonus = 1.5f,
    maxLevel = 4,
    startingLevel = 0,
    capacityPlanetOS = 1000
)
val MediumPlanet = PlanetType(
    name = "Medium",
    planetMetal = 1000,
    biomassCapacityBonus = 1f,
    maxLevel = 8,
    startingLevel = 2,
    capacityPlanetOS = 1000,
    districts = listOf(
        District.Capitol(),
        District.Empty(districtId = 1),
        District.Empty(districtId = 2),
        District.Empty(districtId = 3),
        District.Empty(districtId = 4),
        District.Empty(districtId = 5)
    )
)
val LargePlanet = PlanetType(
    name = "Large",
    planetMetal = 500,
    biomassCapacityBonus = 1f,
    maxLevel = 16,
    startingLevel = 0,
    capacityPlanetOS = 1000
)

data class Planet(
    val id: Int = 0,
    val name: String = "Planet 0",
    val type: PlanetType,
    val typeName: String = type.name,
    val level: Int = type.startingLevel,
    val maxLevel: Int = type.maxLevel,
    val planetMetal: Int = type.planetMetal,
    val planetOrganicSediments: Float = 0f,
    val capacityPlanetOS: Int  = type.capacityPlanetOS,
    val biomassCapacityBonus: Float = type.biomassCapacityBonus,
    val planetGrowthBorder: Int = 0,
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
    val districts: List<District> = type.districts,
    val errors: List<NewTurnError> = emptyList(),
    val planetResourcesPossibleIncome: PlanetResources = PlanetResources()
) {

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "level" to level,
        "planetMetal" to planetMetal,
        "planetOrganicSediments" to planetOrganicSediments,
        "type" to type.name,
        "planetGrowthBorder" to planetGrowthBorder,
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
        "rocketMaterialsSetting" to rocketMaterialsSetting.name,
        "infrastructureSetting" to infrastructureSetting.name,
        "districts" to districts.map { it.toMap() }
    )

    companion object {
        private fun getPlanetTypeByName(name: String): PlanetType = when (name) {
            SmallPlanet.name -> SmallPlanet
            MediumPlanet.name -> MediumPlanet
            LargePlanet.name -> LargePlanet
            else -> SmallPlanet
        }

        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): Planet {
            val typeName = map["type"] as? String ?: SmallPlanet.name
            val planetType = getPlanetTypeByName(typeName)

            val districts = (map["districts"] as? List<Map<String, Any>>)?.map { District.fromMap(it) } ?: emptyList()

            return Planet(
                id = (map["id"] as? Number)?.toInt() ?: 0,
                name = map["name"] as? String ?: "Planet 0",
                type = planetType,
                level = (map["level"] as? Number)?.toInt() ?: planetType.startingLevel,
                planetMetal = (map["planetMetal"] as? Number)?.toInt() ?: planetType.planetMetal,
                planetOrganicSediments = (map["planetOrganicSediments"] as? Number)?.toFloat() ?: 0f,
                planetGrowthBorder = (map["planetGrowthBorder"] as? Number)?.toInt() ?: 0,
                biomass = (map[Resource.BIOMASS.name] as? Number)?.toFloat() ?: 0f,
                metal = (map[Resource.METAL.name] as? Number)?.toInt() ?: 0,
                organicSediment = (map[Resource.ORGANIC_SEDIMENTS.name] as? Number)?.toFloat() ?: 0f,
                influence = (map[Resource.INFLUENCE.name] as? Number)?.toInt() ?: 0,
                infrastructure = (map[Resource.INFRASTRUCTURE.name] as? Number)?.toInt() ?: 0,
                rocketMaterials = (map[Resource.ROCKET_MATERIALS.name] as? Number)?.toInt() ?: 0,
                progress = (map[Resource.PROGRESS.name] as? Number)?.toInt() ?: 0,
                development = (map[Resource.DEVELOPMENT.name] as? Number)?.toInt() ?: 0,
                army = (map[Resource.ARMY.name] as? Number)?.toInt() ?: 0,
                progressSetting = (map["progressSetting"] as? Number)?.toInt() ?: 0,
                researchSetting = (map["researchSetting"] as? Number)?.toInt() ?: 0,
                expeditionsSetting = (map["expeditionsSetting"] as? Number)?.toInt() ?: 0,
                armyConstructionSetting = (map["armyConstructionSetting"] as? Number)?.toInt() ?: 0,
                rocketMaterialsSetting = (map["rocketMaterialsSetting"] as? String)
                    ?.toRocketMaterialsSettingEnum() ?: RocketMaterialsSetting.USAGE,
                infrastructureSetting = (map["infrastructureSetting"] as? String)
                    ?.toInfrastructureSettingEnum() ?: InfrastructureSetting.USAGE,
                districts = districts
            )
        }
    }
}