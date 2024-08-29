package com.example.gravitygame.ui.screens.armyDialogScreen

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravitygame.R
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips
import com.example.gravitygame.tutorial.Tasks
import com.example.gravitygame.tutorial.TutorialDialog
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.timer.CoroutineTimer
import com.example.gravitygame.ui.screens.infoDialogsScreens.ShipInfoDialog
import com.example.gravitygame.ui.screens.battleMapScreen.MovementUiState
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel

@Composable
fun ArmyDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    tutorialModel: TutorialViewModel,
    settingsModel: SettingViewModel,
    show: Boolean,
    context: Context,
    timer: CoroutineTimer,
    onDismissRequest: () -> Unit = { battleModel.cleanMovementValues() },
    onConfirmation: () -> Unit = { battleModel.attack() },
    onCancel: () -> Unit = { battleModel.cleanMovementValues() },
    closeShipInfoDialog: () -> Unit = {battleModel.showShipInfoDialog(false, ShipType.CRUISER)}
) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()
    val weightOfName = 0.15f
    val weightOfNumbers = 0.15f
    val weightOfButtons = 0.1f
    val padding = 16.dp

    ShipInfoDialog(shipType = ShipType.CRUISER, toShow = movementUiState.showCruiserInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.DESTROYER, toShow = movementUiState.showDestroyerInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.GHOST, toShow = movementUiState.showGhostInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.WARPER, toShow = movementUiState.showWarperInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)

    TutorialDialog(tutorialModel = tutorialModel, toShow = tutorialUiState.showTutorialDialog, timer = timer, settingsModel = settingsModel, context = context)
    if(!tutorialUiState.sendShipsTask && tutorialUiState.battleOverviewTask && tutorialUiState.movementTask && settingsUiState.showTutorial && movementUiState.showArmyDialog){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.SEND_SHIPS, timer = timer)
    }
    if(!tutorialUiState.acceptableLostTask && tutorialUiState.sendShipsTask && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.ACCEPTABLE_LOST, timer = timer)
    }

    if (show) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var initialization by rememberSaveable { mutableStateOf(false) }
                if (!initialization) {
                    battleModel.initializeArmyDialogValues()
                    initialization = true
                }
                Card(
                    modifier = modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min),
                    shape = RoundedCornerShape(16.dp)

                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier.wrapContentWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier = modifier
                                .padding(top = padding, start = padding, end = padding)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = modifier

                                ) {

                                    Text(
                                        text = stringResource(id = R.string.nameShip),
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .weight(weightOfName)
                                            .wrapContentWidth(align = Alignment.Start),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = stringResource(id = R.string.enemyShips),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = stringResource(id = R.string.possibleShipsToMove),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = stringResource(id = R.string.shipsToMove),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = "",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfButtons)
                                    )

                                    Text(
                                        text = "",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfButtons)
                                    )
                                }
                                Row {
                                    ArmyDialogRow(
                                        shipType = ShipType.CRUISER,
                                        startLocation = movementUiState.startPosition,
                                        endLocation = movementUiState.endPosition,
                                        isWarperPresent = movementUiState.isWarperPresent,
                                        locationList = locationListUiState.locationList,
                                        battleModel = battleModel,
                                        movementUiState = movementUiState,
                                        weightOfName = weightOfName,
                                        weightOfNumbers = weightOfNumbers,
                                        weightOfButtons = weightOfButtons,
                                        isInfo = false
                                    )
                                }

                                Row {
                                    ArmyDialogRow(
                                        shipType = ShipType.DESTROYER,
                                        startLocation = movementUiState.startPosition,
                                        endLocation = movementUiState.endPosition,
                                        isWarperPresent = movementUiState.isWarperPresent,
                                        locationList = locationListUiState.locationList,
                                        battleModel = battleModel,
                                        movementUiState = movementUiState,
                                        weightOfName = weightOfName,
                                        weightOfNumbers = weightOfNumbers,
                                        weightOfButtons = weightOfButtons,
                                        isInfo = false
                                    )
                                }
                                Row {
                                    ArmyDialogRow(
                                        shipType = ShipType.GHOST,
                                        startLocation = movementUiState.startPosition,
                                        endLocation = movementUiState.endPosition,
                                        isWarperPresent = movementUiState.isWarperPresent,
                                        locationList = locationListUiState.locationList,
                                        battleModel = battleModel,
                                        movementUiState = movementUiState,
                                        weightOfName = weightOfName,
                                        weightOfNumbers = weightOfNumbers,
                                        weightOfButtons = weightOfButtons,
                                        isInfo = false
                                    )
                                }
                                Row {
                                    ArmyDialogRow(
                                        shipType = ShipType.WARPER,
                                        startLocation = movementUiState.startPosition,
                                        endLocation = movementUiState.endPosition,
                                        isWarperPresent = movementUiState.isWarperPresent,
                                        locationList = locationListUiState.locationList,
                                        battleModel = battleModel,
                                        movementUiState = movementUiState,
                                        weightOfName = weightOfName,
                                        weightOfNumbers = weightOfNumbers,
                                        weightOfButtons = weightOfButtons,
                                        isInfo = false
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = padding, end = padding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.maxLosses),
                            modifier = modifier.padding(end = padding)
                        )
                        Slider(
                            value = movementUiState.acceptableLost,
                            onValueChange = { battleModel.changeValueAcceptableLost(value = it) },
                            valueRange = 1f..6f,
                            steps = 4,
                            modifier = modifier
                                .fillMaxWidth(0.9f)
                                .border(
                                    width = 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                .padding(start = 8.dp, end = 8.dp),
                        )

                        Text(
                            text = movementUiState.acceptableLost.toInt().toString(),
                            modifier = modifier
                                .padding(start = padding),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = modifier
                            .padding(start = padding, end = padding, bottom = padding)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onCancel
                        ) {
                            Icon(painter = painterResource(id = R.drawable.close), contentDescription = "Close icon")
                        }

                        Button(
                            onClick = onConfirmation
                        ) {
                            Text(text = stringResource(id = R.string.attack))
                        }
                    }
                }
            }
        }
}


@Composable
fun ArmyDialogRow(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    shipType: ShipType,
    startLocation: Int?,
    endLocation: Int?,
    isWarperPresent: Boolean,
    locationList: List<Location>,
    movementUiState: MovementUiState,
    weightOfNumbers: Float,
    weightOfName: Float,
    weightOfButtons: Float,
    isInfo: Boolean
) {
    val enemyShipsOnPosition by remember {
        derivedStateOf {
            endLocation?.let {
                battleModel.getNumberOfShip(
                    locationList = locationList,
                    location = it,
                    shipType = shipType,
                    isForEnemy = true
                )
            } ?: 0
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.wrapContentWidth()
    ) {
        Text(
            text = mapOfShips[shipType]?.let { stringResource(id = it.nameId) }?: "Unknown",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(weightOfName)
                .wrapContentWidth(align = Alignment.Start)
                .clickable {
                    battleModel.showShipInfoDialog(true, shipType)
                }
        )

        Text(
            text = enemyShipsOnPosition.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(weightOfNumbers)
        )

        if(!isInfo) {
            Text(
                text = when (shipType) {
                    ShipType.CRUISER -> movementUiState.cruiserToMove.toString()
                    ShipType.DESTROYER -> movementUiState.destroyerToMove.toString()
                    ShipType.GHOST -> movementUiState.ghostToMove.toString()
                    ShipType.WARPER -> movementUiState.warperToMove.toString()
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(weightOfNumbers)
            )
        }


            Text(
                text = when (shipType) {
                    ShipType.CRUISER -> movementUiState.cruiserOnPosition.toString()
                    ShipType.DESTROYER -> movementUiState.destroyerOnPosition.toString()
                    ShipType.GHOST -> movementUiState.ghostOnPosition.toString()
                    ShipType.WARPER -> movementUiState.warperOnPosition.toString()
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(weightOfNumbers)
            )

        if(!isInfo){

        Button(
            onClick = { battleModel.removeShip(shipType = shipType) },
            enabled = when (shipType) {
                ShipType.CRUISER -> movementUiState.movingCruisers > 0
                ShipType.DESTROYER -> movementUiState.movingDestroyers > 0
                ShipType.GHOST -> movementUiState.movingGhosts > 0
                ShipType.WARPER -> movementUiState.movingWarpers > 0
            },
            modifier = modifier
                .weight(weightOfButtons)
        ) {
            Icon(painter = painterResource(id = R.drawable.remove), contentDescription = "Remove icon")
        }


            Button(
                onClick = { battleModel.addShip(shipType = shipType) },
                enabled = when (shipType) {
                    ShipType.CRUISER -> movementUiState.cruiserToMove
                    ShipType.DESTROYER -> movementUiState.destroyerToMove
                    ShipType.GHOST -> movementUiState.ghostToMove
                    ShipType.WARPER -> movementUiState.warperToMove
                } > 0 &&
                        checkEnabledAddShip(
                            ship = shipType,
                            startLocation = startLocation,
                            endLocation = endLocation,
                            isWarperPresent = isWarperPresent,
                            locationList = locationList
                        ) &&
                        battleModel.checkShipLimitOnPosition(),
                modifier = Modifier
                    .weight(weightOfButtons),

                ) {
                Icon(painter = painterResource(id = R.drawable.add), contentDescription = "Add icon")
            }
        }

    }
}

/**
 * Check if a ship can move to the position
 * @param isWarperPresent is there a warper ship type
 * @param ship
 */
private fun checkEnabledAddShip(
    isWarperPresent: Boolean,
    ship: ShipType,
    startLocation: Int?,
    locationList: List<Location>,
    endLocation: Int?
): Boolean {
    var isAccesable = false
    if (ship == ShipType.WARPER) {
        isAccesable = true
    } else if (!isWarperPresent) {
        isAccesable = true
    } else {
        val connectionList = startLocation?.let {
            locationList[it].getConnectionsList()
        }
        connectionList?.forEach { if (it == endLocation) isAccesable = true } ?: return false
    }
    return isAccesable
}