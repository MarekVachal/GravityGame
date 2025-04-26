package com.marks2games.gravitygame.building_game.data.model

import com.marks2games.gravitygame.core.domain.error.NewTurnError

data class EmpireUiState(
    val errors: List<NewTurnError> = emptyList(),
    val isErrorsShown: Boolean = false,
    val isActionsShown: Boolean = false,
    val isDistrictDialogShown: Boolean = false,
    val isTransportDialogShown: Boolean = false,
    val isTransportMenuShown: Boolean = false,
    val planetForTransport: Planet? = null,
    val districtForDialog: District? = null,
    val modeIsChecked: Enum<*>? = null,
    val armyProductionSet: Int = 0,
    val expeditionsProductionSet: Int = 0,
    val progressProductionSet: Int = 0,
    val researchProductionSet: Int = 0,
    val rocketMaterialsProductionSet: RocketMaterialsSetting = RocketMaterialsSetting.USAGE,
    val infrastructureProductionSet: InfrastructureSetting = InfrastructureSetting.USAGE,
    val districtToBuild: DistrictEnum = DistrictEnum.PROSPECTORS,
    val planetIdForDetails: Int = 0,
    val isShownDistrictList: Boolean = false
)
