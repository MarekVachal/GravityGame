package com.marks2games.gravitygame.core.domain.error

sealed class RocketMaterialsResult{
    data class Success(val updatedRocketMaterials: Int, val updatedMetal: Int, val updatedOrganicSediments: Float): RocketMaterialsResult()
    data class FailureWithSuccess(val error: Error, val success: Success): RocketMaterialsResult()
    sealed class Error: RocketMaterialsResult() {
        data class InsufficientRocketMaterialsForArmy(val lacking: Int): Error()
        data class InsufficientRocketMaterialsForExpedition(val lacking: Int): Error()
        data class InsufficientRocketMaterialsForArmyAndExpedition(val lackingForArmy: Int, val lackingForExpedition: Int): Error()
    }
}