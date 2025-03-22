package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.R
import io.sentry.Sentry

sealed class District{
    abstract val nameId: Int
    abstract val districtId: Int
    abstract val type: DistrictEnum
    abstract val isWorking: Boolean

    abstract fun toMap(): Map<String, Any>
    abstract fun copyWithUpdatedWorking(isWorking: Boolean): District
    abstract fun generateResources(): ResourceChange

    data class Capitol(
        override val nameId: Int = R.string.capitolDistrictName,
        override val districtId: Int = 0,
        override val type: DistrictEnum = DistrictEnum.CAPITOL,
        override val isWorking: Boolean = true
    ) : District(){
        override fun toMap() = mapOf(
            "type" to type.name
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            return ResourceChange(
                produced = mapOf(
                    Resource.PROGRESS to 10,
                    Resource.INFRASTRUCTURE to 10 //10 Metal from planet metal
                ),
                consumed = mapOf(
                    Resource.BIOMASS to 10, //Consumption for Progress
                    Resource.INFRASTRUCTURE to 10 //Consumption for Progress
                )
            )
        }
    }


    data class Prospectors(
        override val nameId: Int = R.string.prospectorsDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.PROSPECTORS,
        val mode: ProspectorsMode = ProspectorsMode.METAL,
        override val isWorking: Boolean = true
    ) : District(){
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name,
            "districtId" to districtId
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            if (!isWorking) return ResourceChange()

            return when (mode) {
                ProspectorsMode.METAL -> ResourceChange(
                    produced = mapOf(Resource.METAL to 10) //From 10 Planet metals
                )
                ProspectorsMode.ORGANIC_SEDIMENTS -> ResourceChange(
                    produced = mapOf(Resource.ORGANIC_SEDIMENTS to 10) //From 10 Planet OS
                )
            }
        }
    }

    data class Empty(
        override val nameId: Int = R.string.emptyDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.EMPTY,
        override val isWorking: Boolean = true
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "districtId" to districtId
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            TODO("Not yet implemented")
        }
    }

    data class Industrial(
        override val nameId: Int = R.string.industrialDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.INDUSTRIAL,
        val mode: IndustrialMode = IndustrialMode.INFRASTRUCTURE,
        override val isWorking: Boolean = true
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name,
            "districtId" to districtId
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            if(!isWorking) return ResourceChange()
            return when (mode) {
                IndustrialMode.INFRASTRUCTURE -> ResourceChange(
                    produced = mapOf(Resource.INFRASTRUCTURE to 50),
                    consumed = mapOf(Resource.METAL to 50)
                )
                IndustrialMode.ROCKET_MATERIALS -> ResourceChange(
                    produced = mapOf(Resource.ROCKET_MATERIALS to 10),
                    consumed = mapOf(
                        Resource.BIOMASS to 20,
                        Resource.ORGANIC_SEDIMENTS to 20
                    )
                )
            }
        }
    }

    data class ExpeditionPlatform(
        override val nameId: Int = R.string.expeditionPlatformDistrictName,
        override val type: DistrictEnum = DistrictEnum.EXPEDITION_PLATFORM,
        override val districtId: Int = 0,
        override val isWorking: Boolean = true
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            if(!isWorking) return ResourceChange()
            return ResourceChange(
                produced = mapOf(
                    Resource.ARMY to 10,
                    Resource.EXPEDITIONS to 10
                    ),
                consumed = mapOf(
                    Resource.ARMY to 10, //Consumed Rocket materials
                    Resource.EXPEDITIONS to 10 //Consumed Rocket materials
                )
            )
        }
    }

    data class UrbanCenter(
        override val nameId: Int = R.string.urbanCenterDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.URBAN_CENTER,
        val mode: UrbanCenterMode = UrbanCenterMode.INFLUENCE,
        override val isWorking: Boolean = true
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name,
            "districtId" to districtId,
        )
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            if (!isWorking) return ResourceChange()

            return when (mode) {
                UrbanCenterMode.INFLUENCE -> ResourceChange(
                    produced = mapOf(Resource.INFLUENCE to 10)
                )
                UrbanCenterMode.RESEARCH -> ResourceChange(
                    produced = mapOf(Resource.RESEARCH to 10),
                    consumed = mapOf(Resource.BIOMASS to 10)
                )
            }
        }
    }

    companion object {
        fun fromMap(map: Map<String, Any>): District {
            val type = (map["type"] as String).toDistrictEnum() ?:
                throw IllegalArgumentException("Unknown district type: ${map["type"]}")
            val districtId = (map["districtId"] as? Number)?.toInt() ?: 0
            return when (type) {
                DistrictEnum.CAPITOL -> Capitol()
                DistrictEnum.PROSPECTORS -> Prospectors(
                    mode = (map["mode"] as? String)?.toEnumOrNull<ProspectorsMode>()
                        ?: ProspectorsMode.METAL,
                    districtId = districtId
                )
                DistrictEnum.EMPTY -> Empty(districtId = districtId)
                DistrictEnum.INDUSTRIAL -> Industrial(
                    mode = (map["mode"] as? String)?.toEnumOrNull<IndustrialMode>()
                        ?: IndustrialMode.INFRASTRUCTURE,
                    districtId = districtId
                )
                DistrictEnum.EXPEDITION_PLATFORM -> ExpeditionPlatform()
                DistrictEnum.URBAN_CENTER -> UrbanCenter(
                    mode = (map["mode"] as? String)?.toEnumOrNull<UrbanCenterMode>()
                        ?: UrbanCenterMode.INFLUENCE,
                    districtId = districtId
                )
            }
        }
    }
}

enum class ProspectorsMode {METAL, ORGANIC_SEDIMENTS}
enum class IndustrialMode {INFRASTRUCTURE, ROCKET_MATERIALS}
enum class UrbanCenterMode {INFLUENCE, RESEARCH}
enum class DistrictEnum {
    CAPITOL,
    PROSPECTORS,
    EMPTY,
    INDUSTRIAL,
    EXPEDITION_PLATFORM,
    URBAN_CENTER
}
enum class RocketMaterialsSetting {NOTHING, MAXIMUM, USAGE}
enum class InfrastructureSetting {MAXIMUM, USAGE}

fun String.toDistrictEnum(): DistrictEnum? {
    return try{
        DistrictEnum.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}

data class ResourceChange(
    val produced: Map<Resource, Int> = emptyMap(),
    val consumed: Map<Resource, Int> = emptyMap()
)

inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? {
    return enumValues<T>().firstOrNull { it.name == this }
}

fun String.toDistrictModeEnum(): Enum<*>? {
    return toEnumOrNull<ProspectorsMode>()
        ?: toEnumOrNull<IndustrialMode>()
        ?: toEnumOrNull<UrbanCenterMode>()
}

fun String.toRocketMaterialsSettingEnum(): RocketMaterialsSetting? {
    return try{
        RocketMaterialsSetting.valueOf(this)
    } catch (e: IllegalArgumentException){
        Sentry.captureException(e)
        null
    }
}

fun String.toInfrastructureSettingEnum(): InfrastructureSetting? {
    return try {
        InfrastructureSetting.valueOf(this)
    } catch (e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}