package com.marks2games.gravitygame.building_game.data.model

data class PlanetUiStates(
    val armyProductionSet: Int = 0,
    val expeditionProductionSet: Int = 0,
    val infrastructureProductionSet: InfrastructureSetting = InfrastructureSetting.USAGE,
    val progressProductionSet: Int = 0,
    val researchProductionSet: Int = 0,
    val rocketMaterialsProductionSet: RocketMaterialsSetting = RocketMaterialsSetting.USAGE
)
