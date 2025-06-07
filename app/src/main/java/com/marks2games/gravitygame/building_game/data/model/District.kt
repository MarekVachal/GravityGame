package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.R
import androidx.annotation.StringRes
import io.sentry.Sentry

sealed class District{
    abstract val nameId: Int
    abstract val districtId: Int
    abstract val type: DistrictEnum
    abstract val isWorking: Boolean

    abstract fun toMap(): Map<String, Any>
    abstract fun copyWithUpdatedWorking(isWorking: Boolean): District
    abstract fun generateResources(): ResourceChange
    abstract fun getCapacities(): DistrictCapacities
    abstract fun getModes(): List<Enum<*>>

    data class Capitol(
        override val nameId: Int = R.string.capitolDistrictName,
        override val districtId: Int = 0,
        override val type: DistrictEnum = DistrictEnum.CAPITOL,
        override val isWorking: Boolean = true
    ) : District(){
        override fun toMap() = mapOf(
            "type" to type.name
        )

        override fun getModes(): List<Enum<*>> = emptyList()
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
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = mapOf(
                    Resource.BIOMASS to 100,
                    Resource.ORGANIC_SEDIMENTS to 100,
                    Resource.METAL to 100,
                    Resource.ROCKET_MATERIALS to 100,
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
        override fun getModes(): List<Enum<*>> = ProspectorsMode.entries
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            if (!isWorking) return ResourceChange()

            return when (mode) {
                ProspectorsMode.METAL -> ResourceChange(
                    produced = mapOf(Resource.METAL to 20) //From 20 Planet metals
                )
                ProspectorsMode.ORGANIC_SEDIMENTS -> ResourceChange(
                    produced = mapOf(Resource.ORGANIC_SEDIMENTS to 10) //From 10 Planet OS
                )
            }
        }
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = mapOf(
                    Resource.METAL to 100,
                    Resource.ORGANIC_SEDIMENTS to 100
                )
            )
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
        override fun getModes(): List<Enum<*>> = emptyList()
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            return ResourceChange(
                produced = emptyMap()
            )
        }
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = mapOf(
                    Resource.BIOMASS to 100
                )
            )
        }
    }

    data class InConstruction(
        override val nameId: Int = R.string.inConstructionDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.IN_CONSTRUCTION,
        override val isWorking: Boolean = true,
        val infra: Int = 0,
        val buildingDistrict: DistrictEnum = DistrictEnum.EMPTY
    ): District(){
        override fun toMap() = mapOf(
            "type" to type.name,
            "infra" to infra,
            "buildingDistrict" to buildingDistrict.name,
            "districtId" to districtId
        )
        override fun getModes(): List<Enum<*>> = emptyList()
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            return ResourceChange(
                produced = emptyMap()
            )
        }
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = emptyMap()
            )
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
        override fun getModes(): List<Enum<*>> = IndustrialMode.entries
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
                        Resource.METAL to 20,
                        Resource.ORGANIC_SEDIMENTS to 20
                    )
                )
                IndustrialMode.METAL -> ResourceChange(
                    produced = mapOf(Resource.METAL to 10),
                    consumed = mapOf(Resource.ORGANIC_SEDIMENTS to 20)
                )
            }
        }
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = mapOf(
                    Resource.ROCKET_MATERIALS to 100
                )
            )
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
        override fun getModes(): List<Enum<*>> = emptyList()
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
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = emptyMap()
            )
        }
    }

    data class UrbanCenter(
        override val nameId: Int = R.string.urbanCenterDistrictName,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.URBAN_CENTER,
        val mode: UrbanCenterMode = UrbanCenterMode.RESEARCH,
        override val isWorking: Boolean = true
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name,
            "districtId" to districtId,
        )
        override fun getModes(): List<Enum<*>> = UrbanCenterMode.entries
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
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = emptyMap()
            )
        }
    }

    data class Unnocupated(
        override val nameId: Int = R.string.unnocupatedNominative,
        override val districtId: Int,
        override val type: DistrictEnum = DistrictEnum.UNNOCUPATED,
        override val isWorking: Boolean = false
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "districtId" to districtId,
        )
        override fun getModes(): List<Enum<*>> = emptyList()
        override fun copyWithUpdatedWorking(isWorking: Boolean) = copy(isWorking = isWorking)
        override fun generateResources(): ResourceChange {
            return ResourceChange(
                produced = emptyMap()
            )
        }
        override fun getCapacities(): DistrictCapacities {
            return DistrictCapacities(
                capacity = emptyMap()
            )
        }
    }

    companion object {
        fun fromMap(map: Map<String, Any>): District {
            val type = (map["type"] as String).toDistrictEnum() ?:
                throw IllegalArgumentException("Unknown district type: ${map["type"]}")
            val districtId = (map["districtId"] as? Number)?.toInt() ?: 0
            return when (type) {
                DistrictEnum.UNNOCUPATED -> Unnocupated(
                    districtId = districtId
                )
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
                DistrictEnum.IN_CONSTRUCTION -> InConstruction(
                    districtId = districtId,
                    infra = (map["infra"] as? Number?)?.toInt() ?: 0,
                    buildingDistrict = (map["buildingDistrict"] as? String?)?.toDistrictEnum()?: DistrictEnum.EMPTY
                )
            }
        }
    }
}

enum class ProspectorsMode {METAL, ORGANIC_SEDIMENTS}
enum class IndustrialMode {INFRASTRUCTURE, ROCKET_MATERIALS, METAL}
enum class UrbanCenterMode {INFLUENCE, RESEARCH}
enum class DistrictEnum (
    @StringRes val nameIdNominative: Int,
    @StringRes val nameIdGenitive: Int,
    @StringRes val nameIdInstrumental: Int
){
    CAPITOL(R.string.capitolDistrictName, R.string.capitolDistrictGenitive, R.string.capitolDistrictInstrumental),
    PROSPECTORS(R.string.prospectorsDistrictName, R.string.prospectorsDistrictGenitive, R.string.prospectorsDistrictInstrumental),
    EMPTY(R.string.emptyDistrictName, R.string.emptyDistrictGenitive, R.string.emptyDistrictInstrumental),
    INDUSTRIAL(R.string.industrialDistrictName, R.string.industrialDistrictGenitive, R.string.industrialDistrictInstrumental),
    EXPEDITION_PLATFORM(R.string.expeditionPlatformDistrictName, R.string.expeditionPlatformDistrictGenitive, R.string.expeditionPlatformDistrictInstrumental),
    URBAN_CENTER(R.string.urbanCenterDistrictName, R.string.urbanCenterDistrictGenitive, R.string.urbanCenterDistrictInstrumental),
    IN_CONSTRUCTION(R.string.inConstructionDistrictName, R.string.inConstructionDistrictGenitive, R.string.inConstructionDistrictInstrumental),
    UNNOCUPATED(R.string.unnocupatedNominative, R.string.unnocupatedGenitive, R.string.unnocupatedInstrumental)
}
enum class RocketMaterialsSetting (@StringRes val nameId: Int)
{NOTHING (R.string.nothing), MAXIMUM(R.string.maximum), USAGE(R.string.usage)}
enum class InfrastructureSetting (@StringRes val nameId: Int)
{NOTHING (R.string.nothing), MAXIMUM(R.string.maximum), USAGE(R.string.usage)}

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

data class DistrictCapacities(
    val capacity: Map<Resource, Int> = emptyMap(),
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