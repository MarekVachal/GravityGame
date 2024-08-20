package com.example.gravitygame.ui.screen

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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravitygame.R
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips
import com.example.gravitygame.ui.utils.ShipInfoDialog
import com.example.gravitygame.uiStates.MovementUiState
import com.example.gravitygame.viewModels.BattleViewModel

@Composable
fun ArmyDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    show: Boolean,
    onDismissRequest: () -> Unit = { clean(battleModel) },
    onConfirmation: () -> Unit = { battleModel.attack() },
    onCancel: () -> Unit = { clean(battleModel) },
    closeShipInfoDialog: () -> Unit = {battleModel.showShipInfoDialog(false, ShipType.CRUISER)}
) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val weightOfName = 0.3f
    val weightOfNumbers = 0.2f
    val weightOfButtons = 0.1f
    val padding = 16.dp

    ShipInfoDialog(shipType = ShipType.CRUISER, toShow = movementUiState.showCruiserInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.DESTROYER, toShow = movementUiState.showDestroyerInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.GHOST, toShow = movementUiState.showGhostInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)
    ShipInfoDialog(shipType = ShipType.WARPER, toShow = movementUiState.showWarperInfoDialog, onDismissRequest = closeShipInfoDialog, confirmButton = closeShipInfoDialog)

    if (show) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
            ) {
                var initialization by rememberSaveable { mutableStateOf(false) }
                if (!initialization) {
                    battleModel.initializeArmyDialogValues()
                    initialization = true
                }
                Card(
                    modifier = modifier.padding(padding),
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
                                            .wrapContentWidth(align = Alignment.Start)
                                    )

                                    Text(
                                        text = stringResource(id = R.string.enemyShips),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers)
                                    )

                                    Text(
                                        text = stringResource(id = R.string.possibleShipsToMove),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers)
                                    )

                                    Text(
                                        text = stringResource(id = R.string.shipsToMove),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(weightOfNumbers)
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
                                        weightOfButtons = weightOfButtons
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
                                        weightOfButtons = weightOfButtons
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
                                        weightOfButtons = weightOfButtons
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
                                        weightOfButtons = weightOfButtons
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
                            text = stringResource(R.string.maxLost),
                            modifier = modifier.padding(end = padding)
                        )
                        Slider(
                            value = movementUiState.acceptableLost,
                            onValueChange = { battleModel.changeValueAcceptableLost(value = it) },
                            valueRange = 1f..6f,
                            steps = 4,
                            modifier = modifier.fillMaxWidth(0.9f),
                        )

                        Text(
                            text = movementUiState.acceptableLost.toInt().toString(),
                            modifier = modifier.padding(start = padding),
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
                            Text(text = stringResource(id = R.string.cancel))
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
    weightOfButtons: Float
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
        modifier = modifier
    ) {
        Text(
            text = mapOfShips[shipType]?.let { stringResource(id = it.nameId) }?: "Unknown",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(weightOfName)
                .wrapContentWidth(align = Alignment.Start)
                .clickable {
                    battleModel.showShipInfoDialog(true, shipType)}
        )

        Text(
            text = enemyShipsOnPosition.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(weightOfNumbers)
        )

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
            Text(text = stringResource(R.string.minus))
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
            Text(text = stringResource(R.string.plus))
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

/**
 * Set all values to zero for next order
 * @param battleModel BattleViewModel
 */
private fun clean(battleModel: BattleViewModel) {
    battleModel.cleanPositions()
    battleModel.cleanAccessibleLocations()
    battleModel.cleanMovingShip()
    battleModel.cleanAcceptableLost()
    battleModel.showArmyDialog(toShow = false)
}

