package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.data.model.enum_class.toShipType
import io.sentry.Sentry
import java.util.UUID

sealed class Action {
    abstract val setting: Any
    abstract val id: String
    abstract val planetId: Int
    abstract val type: ActionEnum
    abstract val name: Int
    abstract fun toMap(): Map<String, Any>

    sealed class SetProduction : Action() {
        data class ExpeditionProduction(
            override val setting: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.expeditionProduction,
            override val type: ActionEnum = ActionEnum.EXPEDITIONS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting,
                "planetId" to planetId
            )

        }
        data class ProgressProduction(
            override val setting: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.progressProductionName,
            override val type: ActionEnum = ActionEnum.PROGRESS_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting,
                "planetId" to planetId
            )
        }
        data class ArmyProduction(
            override val setting: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.armyProductionName,
            override val type: ActionEnum = ActionEnum.ARMY_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting,
                "planetId" to planetId
            )
        }
        data class ResearchProduction(
            override val setting: Int,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.researchProductionName,
            override val type: ActionEnum = ActionEnum.RESEARCH_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting,
                "planetId" to planetId
            )
        }
        data class InfrastructureProduction(
            override val setting: InfrastructureSetting,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.infrastructureProductionName,
            override val type: ActionEnum = ActionEnum.INFRA_ACTION
        ) : SetProduction() {
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting.name,
                "planetId" to planetId
            )
        }
        data class RocketMaterialsProduction(
            override val setting: RocketMaterialsSetting,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.rocketMaterialsProductionName,
            override val type: ActionEnum = ActionEnum.ROCKET_MATERIALS_ACTION
        ) : SetProduction(){
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting.name,
                "planetId" to planetId
            )
        }
        data class ShipTypeBuild(
            override val setting: ShipType,
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.shipTypeBuild,
            override val type: ActionEnum = ActionEnum.SHIP_TYPE_ACTION
        ): SetProduction(){
            override fun toMap(): Map<String, Any> = mapOf(
                "type" to type.name,
                "id" to id,
                "value" to setting.name,
                "planetId" to planetId
            )
        }
    }
    sealed class DistrictAction : Action() {
        abstract val districtId: Int
        data class SettleDistrict(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.settleDistrict,
            override val districtId: Int,
            override val setting: Int,
            override val type: ActionEnum = ActionEnum.SETTLE_DISTRICT_ACTION
        ): DistrictAction()
        data class BuildDistrict(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.buildDistrict,
            override val districtId: Int,
            override val setting: Int,
            val district: DistrictEnum,
            override val type: ActionEnum = ActionEnum.BUILD_DISTRICT_ACTION
        ) : DistrictAction()
        data class DestroyDistrict(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.destroyDistrict,
            override val districtId: Int,
            override val setting: Int,
            override val type: ActionEnum = ActionEnum.DESTROY_DISTRICT_ACTION
        ) : DistrictAction()
        data class ChangeDistrictMode(
            override val planetId: Int,
            override val id: String = UUID.randomUUID().toString(),
            override val name: Int = R.string.changeDistrictModeDescription,
            override val districtId: Int,
            override val setting: Int,
            val districtType: DistrictEnum,
            val newMode: Enum<*>,
            override val type: ActionEnum = ActionEnum.CHANGE_MODE_ACTION
        ) : DistrictAction()
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "id" to id,
            "planetId" to planetId,
            "districtId" to districtId,
            "setting" to setting,
            *when(this){
                is SettleDistrict -> emptyArray()
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
        override val name: Int = R.string.tradeName,
        override val setting: Trade,
        override val type: ActionEnum = ActionEnum.TRADE_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "planetId" to planetId,
            "id" to id,
            "trade" to setting.toMap()
        )
    }
    data class TransportAction(
        override val planetId: Int,
        override val id: String = UUID.randomUUID().toString(),
        override val name: Int = R.string.transportName,
        override val setting: Transport,
        override val type: ActionEnum = ActionEnum.TRANSPORT_ACTION
    ) : Action() {
        override fun toMap(): Map<String, Any> = mapOf(
            "type" to type.name,
            "id" to id,
            "planetId" to planetId,
            "transport" to setting.toMap()
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any>): Action {
            val type = (map["type"] as String).toActionEnum() ?: throw IllegalArgumentException("Unknown action type.")
            val planetId = (map["planetId"] as Long).toInt()
            val id = map["id"] as? String ?: UUID.randomUUID().toString()
            @Suppress("UNCHECKED_CAST")
            return when (type) {
                ActionEnum.SHIP_TYPE_ACTION -> SetProduction.ShipTypeBuild(
                    setting = (map["value"] as String).toShipType()?: ShipType.CRUISER,
                    planetId = planetId,
                    id = id
                )
                ActionEnum.EXPEDITIONS_ACTION -> SetProduction.ExpeditionProduction(
                    setting = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.PROGRESS_ACTION-> SetProduction.ProgressProduction(
                    setting = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.ARMY_ACTION -> SetProduction.ArmyProduction(
                    setting = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.RESEARCH_ACTION -> SetProduction.ResearchProduction(
                    setting = (map["value"] as Long).toInt(),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.INFRA_ACTION -> SetProduction.InfrastructureProduction(
                    setting = (map["value"] as String).toInfrastructureSettingEnum()?: throw IllegalArgumentException("Unknown infrastructure setting."),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.ROCKET_MATERIALS_ACTION -> SetProduction.RocketMaterialsProduction(
                    setting = (map["value"] as String).toRocketMaterialsSettingEnum()?: throw IllegalArgumentException("Unknown rocket materials setting."),
                    planetId = planetId,
                    id = id
                )
                ActionEnum.SETTLE_DISTRICT_ACTION -> DistrictAction.SettleDistrict(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    id = id,
                    setting = (map["setting"] as Long).toInt()
                )
                ActionEnum.BUILD_DISTRICT_ACTION -> DistrictAction.BuildDistrict(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    district = (map["district"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."),
                    id = id,
                    setting = (map["setting"] as Long).toInt()
                )
                ActionEnum.DESTROY_DISTRICT_ACTION -> DistrictAction.DestroyDistrict(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    id = id,
                    setting = (map["setting"] as Long).toInt()
                )
                ActionEnum.CHANGE_MODE_ACTION -> DistrictAction.ChangeDistrictMode(
                    planetId = planetId,
                    districtId = (map["districtId"] as Long).toInt(),
                    districtType = (map["districtType"] as String).toDistrictEnum()?: throw IllegalArgumentException("Unknown district type."),
                    newMode = (map["newMode"] as String).toDistrictModeEnum()?: throw IllegalArgumentException("Unknown district mode."),
                    id = id,
                    setting = (map["setting"] as Long).toInt()
                )
                ActionEnum.TRADE_ACTION -> TradeAction(
                    planetId = planetId,
                    setting = Trade.fromMap(map["trade"] as Map<String, Any>),
                    id = id
                )
                ActionEnum.TRANSPORT_ACTION -> TransportAction(
                    planetId = planetId,
                    setting = Transport.fromMap(map["transport"] as Map<String, Any>),
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
    SHIP_TYPE_ACTION,
    SETTLE_DISTRICT_ACTION
}

fun String.toActionEnum(): ActionEnum? {
    return try{
        ActionEnum.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}