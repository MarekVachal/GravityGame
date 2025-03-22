package com.marks2games.gravitygame.building_game.data.model

import io.sentry.Sentry

sealed class Action {
    abstract val planetId: Int
    abstract val type: ActionEnum
    abstract fun toMap(): Map<String, Any>

    sealed class SetProduction : Action() {
        data class ExpeditionProduction(
            val value: Int,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.EXPEDITIONS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ProgressProduction(
            val value: Int,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.PROGRESS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ArmyProduction(
            val value: Int,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.ARMY_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class ResearchProduction(
            val value: Int,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.RESEARCH_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value,
                "planetId" to planetId
            )
        }
        data class InfrastructureProduction(
            val value: InfrastructureSetting,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.INFRA_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value.name,
                "planetId" to planetId
            )
        }
        data class RocketMaterialsProduction(
            val value: RocketMaterialsSetting,
            override val planetId: Int,
            override val type: ActionEnum = ActionEnum.ROCKET_MATERIALS_ACTION
        ) : SetProduction(){
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "value" to value.name,
                "planetId" to planetId
            )
        }
    }
    sealed class DistrictAction : Action() {
        abstract val districtId: Int
        data class BuildDistrict(
            override val planetId: Int,
            override val districtId: Int,
            val district: DistrictEnum,
            override val type: ActionEnum = ActionEnum.BUILD_DISTRICT_ACTION
        ) : DistrictAction()
        data class DestroyDistrict(
            override val planetId: Int,
            override val districtId: Int,
            override val type: ActionEnum = ActionEnum.DESTROY_DISTRICT_ACTION
        ) : DistrictAction()
        data class ChangeDistrictMode(
            override val planetId: Int,
            override val districtId: Int,
            val districtType: DistrictEnum,
            val newMode: Enum<*>,
            override val type: ActionEnum = ActionEnum.CHANGE_MODE_ACTION
        ) : DistrictAction()
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
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
        val trade: Trade,
        override val type: ActionEnum = ActionEnum.TRADE_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "planetId" to planetId,
            "trade" to trade.toMap()
        )
    }
    data class TransportAction(
        override val planetId: Int,
        val transport: Transport,
        override val type: ActionEnum = ActionEnum.TRANSPORT_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "planetId" to planetId,
            "transport" to transport.toMap()
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any>): Action {
            val type = (map["type"] as String).toActionEnum() ?: throw IllegalArgumentException("Unknown action type.")
            val planetId = (map["planetId"] as Long).toInt()
            @Suppress("UNCHECKED_CAST")
            return when (type) {
                ActionEnum.EXPEDITIONS_ACTION -> SetProduction.ExpeditionProduction((map["value"] as Long).toInt(), planetId)
                ActionEnum.PROGRESS_ACTION-> SetProduction.ProgressProduction((map["value"] as Long).toInt(), planetId)
                ActionEnum.ARMY_ACTION -> SetProduction.ArmyProduction((map["value"] as Long).toInt(), planetId)
                ActionEnum.RESEARCH_ACTION -> SetProduction.ResearchProduction((map["value"] as Long).toInt(), planetId)
                ActionEnum.INFRA_ACTION -> SetProduction.InfrastructureProduction((map["value"] as String).toInfrastructureSettingEnum()?: throw IllegalArgumentException("Unknown infrastructure setting."), planetId)
                ActionEnum.ROCKET_MATERIALS_ACTION -> SetProduction.RocketMaterialsProduction((map["value"] as String).toRocketMaterialsSettingEnum()?: throw IllegalArgumentException("Unknown rocket materials setting."), planetId)
                ActionEnum.BUILD_DISTRICT_ACTION -> DistrictAction.BuildDistrict(planetId, (map["districtId"] as Long).toInt(), (map["district"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."))
                ActionEnum.DESTROY_DISTRICT_ACTION -> DistrictAction.DestroyDistrict(planetId, (map["districtId"] as Long).toInt())
                ActionEnum.CHANGE_MODE_ACTION -> DistrictAction.ChangeDistrictMode(planetId, (map["districtId"] as Long).toInt(), (map["districtType"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."), (map["newMode"] as String).toDistrictModeEnum()?: throw IllegalArgumentException("Unknown district mode."))
                ActionEnum.TRADE_ACTION -> TradeAction(planetId, Trade.fromMap(map["trade"] as Map<String, Any>))
                ActionEnum.TRANSPORT_ACTION -> TransportAction(planetId, Transport.fromMap(map["transport"] as Map<String, Any>))
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