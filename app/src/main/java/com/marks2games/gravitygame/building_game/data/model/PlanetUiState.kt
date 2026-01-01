package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.domain.error.NewTurnError

data class PlanetUiState(
    val planetId: Int? = null,
    val empire: Empire? = null,
    val testEmpire: Empire? = null,
    val errors: List<NewTurnError> = emptyList(),
    val transports: List<Transport> = emptyList(),
    val actions: List<Action> = emptyList(),
    val isErrorsShown: Boolean = false,
    val isActionsShown: Boolean = false,
    val isDistrictDialogShown: Boolean = false,
    val isTransportDialogShown: Boolean = false,
    val isTransportMenuShown: Boolean = false,
    val districtForDialog: District? = null,
    val modeIsChecked: Enum<*>? = null,
    val armyProductionSet: String = "",
    val expeditionsProductionSet: String = "",
    val progressProductionSet: String = "",
    val researchProductionSet: String = "",
    val buildingShip: ShipType? = null,
    val rocketMaterialsProductionSet: RocketMaterialsSetting = RocketMaterialsSetting.USAGE,
    val infrastructureProductionSet: InfrastructureSetting = InfrastructureSetting.USAGE,
    val districtToBuild: DistrictEnum = DistrictEnum.PROSPECTORS
)