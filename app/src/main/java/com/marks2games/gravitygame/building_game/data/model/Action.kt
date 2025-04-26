package com.marks2games.gravitygame.building_game.data.model

import io.sentry.Sentry
import java.util.UUID

sealed class Action {
    abstract val id: String
    abstract val planetId: Int
    abstract val type: ActionEnum
    abstract fun toMap(): Map<String, Any>

    sealed class SetProduction : Action() {
        data class ExpeditionProduction(
            val value: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.EXPEDITIONS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ProgressProduction(
            val value: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.PROGRESS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ArmyProduction(
            val value: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.ARMY_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ResearchProduction(
            val value: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.RESEARCH_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class InfrastructureProduction(
            val value: InfrastructureSetting,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.INFRA_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value.name,
                "planetId" to planetId
            )
        }
        data class RocketMaterialsProduction(
            val value: RocketMaterialsSetting,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val type: ActionEnum = ActionEnum.ROCKET_MATERIALS_ACTION
        ) : SetProduction(){
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to value.name,
                "planetId" to planetId
            )
        }
    }
    sealed class DistrictAction : Action() {
        abstract val districtId: Int
        data class BuildDistrict(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val districtId: Int,
            val district: DistrictEnum,
            override val type: ActionEnum = ActionEnum.BUILD_DISTRICT_ACTION
        ) : DistrictAction()
        data class DestroyDistrict(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val districtId: Int,
            override val type: ActionEnum = ActionEnum.DESTROY_DISTRICT_ACTION
        ) : DistrictAction()
        data class ChangeDistrictMode(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val districtId: Int,
            val districtType: DistrictEnum,
            val newMode: Enum<*>,
            override val type: ActionEnum = ActionEnum.CHANGE_MODE_ACTION
        ) : DistrictAction()
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "id" to id,
            "planetId" to planetId,
            "districtId" to districtId,
            *when(this){
                is BuildDistrict -> arrayOf("district" to district.name)
                is DestroyDistrict -> emptyArray()
                is ChangeDistrictMode -> arrayOf(
                    "districtType" to districtType.name,
                    "newMode" to newMode.name
                )
            }
        )
    }
    data class TradeAction(
        override val planetId: Int,
        override val id: String = UUID.randomUUID().toString(),
        val trade: Trade,
        override val type: ActionEnum = ActionEnum.TRADE_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "planetId" to planetId,
            "id" to id,
            "trade" to trade.toMap()
        )
    }
    data class TransportAction(
        override val planetId: Int,
        override val id: String = UUID.randomUUID().toString(),
        val transport: Transport,
        override val type: ActionEnum = ActionEnum.TRANSPORT_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "id" to id,
            "planetId" to planetId,
            "transport" to transport.toMap()
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any>): Action {
            val type = (map["type"] as String).toActionEnum() ?: throw IllegalArgumentException("Unknown action type.")
            val planetId = (map["planetId"] as Long).toInt()
            val id = map["id"] as? String ?: UUID.randomUUID().toString()
            @Suppress("UNCHECKED_CAST")
            return when (type) {
                ActionEnum.EXPEDITIONS_ACTION -> SetProduction.ExpeditionProduction(
                    value = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.PROGRESS_ACTION-> SetProduction.ProgressProduction(
                    value = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.ARMY_ACTION -> SetProduction.ArmyProduction(
                    value = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.RESEARCH_ACTION -> SetProduction.ResearchProduction(
                    value = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.INFRA_ACTION -> SetProduction.InfrastructureProduction(
                    value = (map["value"] as String).toInfrastructureSettingEnum()?: throw IllegalArgumentException("Unknown infrastructure setting."),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.ROCKET_MATERIALS_ACTION -> SetProduction.RocketMaterialsProduction(
                    value = (map["value"] as String).toRocketMaterialsSettingEnum()?: throw IllegalArgumentException("Unknown rocket materials setting."),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.BUILD_DISTRICT_ACTION -> DistrictAction.BuildDistrict(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    district = (map["district"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."),
                    id = id
                )
                ActionEnum.DESTROY_DISTRICT_ACTION -> DistrictAction.DestroyDistrict(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    id = id
                )
                ActionEnum.CHANGE_MODE_ACTION -> DistrictAction.ChangeDistrictMode(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    districtType = (map["districtType"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."),
                    newMode = (map["newMode"] as String).toDistrictModeEnum()?: throw IllegalArgumentException("Unknown district mode."),
                    id = id
                )
                ActionEnum.TRADE_ACTION -> TradeAction(
                    planetId = planetId,
                    trade = Trade.fromMap(map["trade"] as Map<String, Any>),
                    id = id
                )
                ActionEnum.TRANSPORT_ACTION -> TransportAction(
                    planetId = planetId,
                    transport = Transport.fromMap(map["transport"] as Map<String, Any>),
                    id = id
                )
            }
        }
    }

}

enum class ActionEnum {
    EXPEDITIONS_ACTION,
    PROGRESS_ACTION,
    ARMY_ACTION,
    RESEARCH_ACTION,
    INFRA_ACTION,
    ROCKET_MATERIALS_ACTION,
    BUILD_DISTRICT_ACTION,
    DESTROY_DISTRICT_ACTION,
    CHANGE_MODE_ACTION,
    TRANSPORT_ACTION,
    TRADE_ACTION,
}

fun String.toActionEnum(): ActionEnum? {
    return try{
        ActionEnum.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}