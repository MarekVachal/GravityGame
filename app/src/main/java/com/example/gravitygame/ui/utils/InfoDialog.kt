package com.example.gravitygame.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gravitygame.R
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips
import com.example.gravitygame.ui.screen.ArmyDialogRow
import com.example.gravitygame.viewModels.BattleViewModel

@Composable
fun ShipInfoDialog(
    shipType: ShipType,
    toShow: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
) {
    if (toShow) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = confirmButton) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ship_icon),
                    contentDescription = "Image of the ship",
                    modifier = modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = mapOfShips[shipType]?.let { stringResource(id = it.nameId) }?: "Unknown"
                )
            },
            text = {
                Text(
                    text = mapOfShips[shipType]?.let { stringResource(id = it.descriptionId) }?: "Unknown"
                )
            },
            modifier = modifier

        )
    }
}

@Composable
fun EndOfGameDialog(
    toShow: Boolean,
    playerData: Player,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
){
    if(toShow){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = confirmButton) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            title = {
                Text(
                    text = if(playerData.lost){
                        stringResource(id = R.string.titleLostGame)
                    } else if (playerData.win){
                        stringResource(id = R.string.titleWinGame)
                    } else {
                        stringResource(id = R.string.titleDrawGame)
                    }
                )
            },
            text = {
                Text(
                    text = if(playerData.lost){
                        stringResource(id = R.string.lostGame)
                    } else if (playerData.win){
                        stringResource(id = R.string.winGame)
                    } else {
                        stringResource(id = R.string.drawGame)
                    }
                )
            },
            modifier = modifier

        )
    }
}

@Composable
fun LocationInfoDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    toShow: Boolean,
    onDismissRequest: () -> Unit = {battleModel.showLocationInfoDialog(false)},
    closeShipInfoDialog: () -> Unit = {battleModel.showShipInfoDialog(false, ShipType.CRUISER) }

){
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

    if(toShow){
        Dialog(
            onDismissRequest = onDismissRequest
        ){
            var initialization by rememberSaveable { mutableStateOf(false) }
            if (!initialization) {
                battleModel.initializeLocationDialogValues()
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
                            }
                            Row {
                                ArmyDialogRow(
                                    shipType = ShipType.CRUISER,
                                    startLocation = movementUiState.locationForInfo,
                                    endLocation = movementUiState.locationForInfo,
                                    isWarperPresent = movementUiState.isWarperPresent,
                                    locationList = locationListUiState.locationList,
                                    battleModel = battleModel,
                                    movementUiState = movementUiState,
                                    weightOfName = weightOfName,
                                    weightOfNumbers = weightOfNumbers,
                                    weightOfButtons = weightOfButtons,
                                    isInfo = true
                                )
                            }

                            Row {
                                ArmyDialogRow(
                                    shipType = ShipType.DESTROYER,
                                    startLocation = movementUiState.locationForInfo,
                                    endLocation = movementUiState.locationForInfo,
                                    isWarperPresent = movementUiState.isWarperPresent,
                                    locationList = locationListUiState.locationList,
                                    battleModel = battleModel,
                                    movementUiState = movementUiState,
                                    weightOfName = weightOfName,
                                    weightOfNumbers = weightOfNumbers,
                                    weightOfButtons = weightOfButtons,
                                    isInfo = true
                                )
                            }
                            Row {
                                ArmyDialogRow(
                                    shipType = ShipType.GHOST,
                                    startLocation = movementUiState.locationForInfo,
                                    endLocation = movementUiState.locationForInfo,
                                    isWarperPresent = movementUiState.isWarperPresent,
                                    locationList = locationListUiState.locationList,
                                    battleModel = battleModel,
                                    movementUiState = movementUiState,
                                    weightOfName = weightOfName,
                                    weightOfNumbers = weightOfNumbers,
                                    weightOfButtons = weightOfButtons,
                                    isInfo = true
                                )
                            }
                            Row {
                                ArmyDialogRow(
                                    shipType = ShipType.WARPER,
                                    startLocation = movementUiState.locationForInfo,
                                    endLocation = movementUiState.locationForInfo,
                                    isWarperPresent = movementUiState.isWarperPresent,
                                    locationList = locationListUiState.locationList,
                                    battleModel = battleModel,
                                    movementUiState = movementUiState,
                                    weightOfName = weightOfName,
                                    weightOfNumbers = weightOfNumbers,
                                    weightOfButtons = weightOfButtons,
                                    isInfo = true
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = padding, end = padding),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.maxLost),
                        modifier = modifier.padding(end = padding)
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
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismissRequest
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}