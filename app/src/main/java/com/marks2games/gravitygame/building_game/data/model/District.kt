package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.R
import io.sentry.Sentry

sealed class District{
    abstract val nameId: Int
    abstract val isOnePerPlanet: Boolean
    abstract val type: DistrictEnum

    abstract fun toMap(): Map<String, String>

    data class Capitol(
        override val nameId: Int = R.string.capitolDistrictName,
        override val isOnePerPlanet: Boolean = true,
        override val type: DistrictEnum = DistrictEnum.CAPITOL
    ) : District(){
        override fun toMap() = mapOf(
            "type" to type.name
        )
    }


    data class Prospectors(
        override val nameId: Int = R.string.prospectorsDistrictName,
        override val isOnePerPlanet: Boolean = false,
        override val type: DistrictEnum = DistrictEnum.PROSPECTORS,
        val mode: ProspectorsMode = ProspectorsMode.METAL
    ) : District(){
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name
        )
    }

    data class Empty(
        override val nameId: Int = R.string.emptyDistrictName,
        override val isOnePerPlanet: Boolean = false,
        override val type: DistrictEnum = DistrictEnum.EMPTY
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name
        )
    }

    data class Industrial(
        override val nameId: Int = R.string.industrialDistrictName,
        override val isOnePerPlanet: Boolean = false,
        override val type: DistrictEnum = DistrictEnum.INDUSTRIAL,
        val mode: IndustrialMode = IndustrialMode.INFRASTRUCTURE
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name
        )
    }

    data class ExpeditionPlatform(
        override val nameId: Int = R.string.expeditionPlatformDistrictName,
        override val type: DistrictEnum = DistrictEnum.EXPEDITION_PLATFORM,
        override val isOnePerPlanet: Boolean = false
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name
        )
    }

    data class UrbanCenter(
        override val nameId: Int = R.string.urbanCenterDistrictName,
        override val isOnePerPlanet: Boolean = false,
        override val type: DistrictEnum = DistrictEnum.URBAN_CENTER,
        val mode: UrbanCenterMode = UrbanCenterMode.INFLUENCE
    ) : District() {
        override fun toMap() = mapOf(
            "type" to type.name,
            "mode" to mode.name
        )
    }

    companion object {
        fun fromMap(map: Map<String, String>): District {
            val type = map["type"]?.toDistrictEnum() ?:
                throw IllegalArgumentException("Unknown district type: ${map["type"]}")
            return when (type) {
                DistrictEnum.CAPITOL -> Capitol()
                DistrictEnum.PROSPECTORS -> Prospectors(mode = ProspectorsMode.valueOf(map["mode"] ?: throw IllegalArgumentException("Missing mode")))
                DistrictEnum.EMPTY -> Empty()
                DistrictEnum.INDUSTRIAL -> Industrial(mode = IndustrialMode.valueOf(map["mode"] ?: throw IllegalArgumentException("Missing mode")))
                DistrictEnum.EXPEDITION_PLATFORM -> ExpeditionPlatform()
                DistrictEnum.URBAN_CENTER -> UrbanCenter(mode = UrbanCenterMode.valueOf(map["mode"] ?: throw IllegalArgumentException("Missing mode")))
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

fun String.toDistrictEnum(): DistrictEnum? {
    return try{
        DistrictEnum.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}
