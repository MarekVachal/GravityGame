package com.marks2games.gravitygame.building_game.data.model

import io.sentry.Sentry

sealed class Action {
    abstract val planetId: Int
    abstract val generatedValue: Int
    abstract fun toMap() : Map<String, String>

    data class AccumulateDevelopment(
        override val planetId: Int,
        override val generatedValue: Int
    ) : Action(){
        override fun toMap() = mapOf(
            "type" to ActionEnum.DEVELOPMENT_ACTION.name,
            "planetId" to planetId.toString(),
            "generatedValue" to generatedValue.toString()
        )
    }

    data class AccumulateExpeditions(
        override val planetId: Int,
        override val generatedValue: Int
    ): Action(){
        override fun toMap() = mapOf(
            "type" to ActionEnum.EXPEDITIONS_ACTION.name,
            "planetId" to planetId.toString(),
            "generatedValue" to generatedValue.toString()
        )
    }

    data class AccumulateProgress(
        override val planetId: Int,
        override val generatedValue: Int
    ): Action() {
        override fun toMap() = mapOf(
            "type" to ActionEnum.PROGRESS_ACTION.name,
            "planetId" to planetId.toString(),
            "generatedValue" to generatedValue.toString()
        )
    }

    data class AccumulateTradepower(
        override val planetId: Int,
        override val generatedValue: Int,
    ): Action() {
        override fun toMap() = mapOf(
            "type" to ActionEnum.TRADEPOWER_ACTION.name,
            "planetId" to planetId.toString(),
            "generatedValue" to generatedValue.toString()
        )
    }

    data class BuildDistrict(
        override val planetId: Int,
        override val generatedValue: Int,
        val district: DistrictEnum
    ): Action() {
        override fun toMap() = mapOf(
            "type" to ActionEnum.BUILD_DISTRICT_ACTION.name,
            "planetId" to planetId.toString(),
            "districtToBuild" to district.name
        )
    }

    data class ChangeDistrictMode(
        override val planetId: Int,
        override val generatedValue: Int,
        val district: DistrictEnum,
        val newMode: Enum<*>
    ): Action(){
        override fun toMap() = mapOf(
            "type" to ActionEnum.CHANGE_MODE_ACTION.name,
            "planetId" to planetId.toString(),
            "districtToChange" to district.name,
            "newMode" to newMode.name
        )
    }

    data class CreateArmyUnit(
        override val planetId: Int,
        override val generatedValue: Int
    ): Action() {
        override fun toMap() = mapOf(
            "type" to ActionEnum.CREATE_ARMY_ACTION.name,
            "planetId" to planetId.toString(),
            "generatedValue" to generatedValue.toString()
        )
    }

    data class DestroyDistrict(
        override val planetId: Int,
        override val generatedValue: Int,
        val district: DistrictEnum
    ): Action() {
        override fun toMap() = mapOf(
            "type" to ActionEnum.DESTROY_DISTRICT_ACTION.name,
            "planetId" to planetId.toString(),
            "districtToDestroy" to district.name
        )
    }

    data class Transport(
        override val planetId: Int,
        override val generatedValue: Int,
        val transport: Transport
    ): Action(){
        override fun toMap() = mapOf(
            "type" to ActionEnum.TRANSPORT_ACTION.name,
            "planetId" to planetId.toString(),
            "planet2Id" to generatedValue.toString(),
            //"transport" to transport.toMap()
        )
    }

    data class Trade(
        override val planetId: Int,
        override val generatedValue: Int,
        val trade: Trade
    ): Action(){
        override fun toMap() = mapOf(
            "type" to ActionEnum.TRADE_ACTION.name,
            "planetId" to planetId.toString(),
            //"trade" to trade.toMap()
        )
    }

    companion object {
        fun fromMap(map: Map<String, String>): Action {
            val type = map["type"]?.toActionEnum() ?:
                throw IllegalArgumentException("Unknown action type: ${map["type"]}")
            return when(type){
                ActionEnum.DEVELOPMENT_ACTION -> AccumulateDevelopment(
                    planetId = map["planetId"]?.toInt() ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0
                )
                ActionEnum.EXPEDITIONS_ACTION -> AccumulateExpeditions(
                    planetId = map["planetId"]?.toInt() ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0
                )
                ActionEnum.PROGRESS_ACTION -> AccumulateProgress(
                    planetId = map["planetId"]?.toInt()
                        ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0
                )
                ActionEnum.TRADEPOWER_ACTION -> AccumulateTradepower(
                    planetId = map["planetId"]?.toInt() ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0
                )
                ActionEnum.BUILD_DISTRICT_ACTION -> BuildDistrict(
                    planetId = map["planetId"]?.toInt()
                        ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0,
                    district = map["districtToBuild"]?.toDistrictEnum() ?: throw IllegalArgumentException("Unknown district enum")
                )
                ActionEnum.DESTROY_DISTRICT_ACTION -> DestroyDistrict(
                    planetId = map["planetId"]?.toInt() ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0,
                    district = map["districtToDestroy"]?.toDistrictEnum() ?: throw IllegalArgumentException("Unknown district enum")
                )
                ActionEnum.CHANGE_MODE_ACTION -> ChangeDistrictMode(
                    planetId = map["planetId"]?.toInt() ?: throw IllegalArgumentException("Missing planet id"),
                    generatedValue = map["generatedValue"]?.toInt() ?: 0,
                    district = map["districtToDestroy"]?.toDistrictEnum() ?: throw IllegalArgumentException("Unknown district enum"),
                    newMode = ProspectorsMode.METAL
                )
                ActionEnum.CREATE_ARMY_ACTION -> TODO()
                ActionEnum.TRANSPORT_ACTION -> TODO()
                ActionEnum.TRADE_ACTION -> TODO()
            }
        }
    }
}

enum class ActionEnum {
    DEVELOPMENT_ACTION,
    EXPEDITIONS_ACTION,
    PROGRESS_ACTION,
    TRADEPOWER_ACTION,
    BUILD_DISTRICT_ACTION,
    DESTROY_DISTRICT_ACTION,
    CHANGE_MODE_ACTION,
    CREATE_ARMY_ACTION,
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