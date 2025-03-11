package com.marks2games.gravitygame.building_game.data.model

sealed class Action {
    abstract val planetId: Int
    abstract val mainValue: Int

    data class AccumulateDevelopment()

    data class AccumulateExpeditions(
        val planetId: Int,
        val rocketMaterials: Int,
        val expeditions: Float
    )

    data class AccumulateProgress(
        val planetId: Int,
        val infrastructure: Int,
        val biomass: Float,
        val progress: Int,
        val planetLevel: Int
    )

    data class AccumulateTradepower(
        val planetId: Int,
        val influence: Int,
        val tradepower: Int
    )

    data class BuildDistrict(
        val planetId: Int,
        val infrastructure: Int,
        val district: DistrictEnum
    )

    data class ChangeDistrictMode(
        val planetId: Int,
        val infrastructure: Int,
        val district: DistrictEnum,
        val newMode: Enum<*>
    )

    data class CreateArmyUnit(
        val planetId: Int,
        val rocketMaterials: Int,
        val army: Int
    )

    data class DestroyDistrict(
        val planetId: Int,
        val district: DistrictEnum,
        val metal: Int
    )

    data class Transport(
        val planet1Id: Int,
        val planet2Id: Int,

    )
}