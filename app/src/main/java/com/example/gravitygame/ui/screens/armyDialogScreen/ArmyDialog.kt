package com.example.gravitygame.ui.screens.armyDialogScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravitygame.R
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips
import com.example.gravitygame.tutorial.Tasks
import com.example.gravitygame.tutorial.TutorialDialog
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.timer.TimerViewModel
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
    timerModel: TimerViewModel,
    toShow: Boolean,
    context: Context,
    onDismissRequest: () -> Unit = { battleModel.cleanMovementValues() },
    onConfirmation: () -> Unit = { battleModel.attack() },
    onCancel: () -> Unit = { battleModel.cleanMovementValues() },
    closeShipInfoDialog: () -> Unit = { battleModel.showShipInfoDialog(false) }
) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()
    val weightOfName = 0.20f
    val weightOfNumbers = 0.15f
    val weightOfButtons = 0.05f
    val padding = 16.dp

    ShipInfoDialog(
        shipType = movementUiState.shipTypeToShow,
        toShow = movementUiState.showShipInfoDialog,
        onDismissRequest = closeShipInfoDialog,
        confirmButton = closeShipInfoDialog
    )

    TutorialDialog(
        tutorialModel = tutorialModel,
        toShow = tutorialUiState.showTutorialDialog,
        timerModel = timerModel,
        settingsModel = settingsModel,
        context = context
    )
    if (settingsUiState.showTutorial){
        if (!tutorialUiState.sendShipsTask && tutorialUiState.battleOverviewTask && tutorialUiState.movementTask && movementUiState.showArmyDialog) {
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.SEND_SHIPS,
                timerModel = timerModel
            )
        }
        if (!tutorialUiState.acceptableLostTask && tutorialUiState.sendShipsTask) {
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.ACCEPTABLE_LOST,
                timerModel = timerModel
            )
        }
    }


    if (toShow) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            LaunchedEffect(Unit) {
                battleModel.initializeArmyDialogValues()
            }

            Card(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .wrapContentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .padding(padding)
                ) {
                    Column(
                        modifier = modifier
                            .wrapContentSize()
                            .width(IntrinsicSize.Min)
                    ) {
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
                        HorizontalDivider()
                        Row {
                            ArmyDialogRow(
                                shipType = ShipType.CRUISER,
                                startLocation = movementUiState.startPosition,
                                endLocation = movementUiState.endPosition,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = false
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            ArmyDialogRow(
                                shipType = ShipType.DESTROYER,
                                startLocation = movementUiState.startPosition,
                                endLocation = movementUiState.endPosition,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = false
                            )
                        }
                        HorizontalDivider()
                        Row {
                            ArmyDialogRow(
                                shipType = ShipType.GHOST,
                                startLocation = movementUiState.startPosition,
                                endLocation = movementUiState.endPosition,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = false
                            )
                        }
                        HorizontalDivider()
                        Row {
                            ArmyDialogRow(
                                shipType = ShipType.WARPER,
                                startLocation = movementUiState.startPosition,
                                endLocation = movementUiState.endPosition,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = false
                            )
                        }
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = padding, end = padding),
                            horizontalArrangement = Arrangement.SpaceEvenly,
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
                                    .fillMaxWidth(0.8f)
                                    .border(
                                        width = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    .padding(start = 8.dp, end = 8.dp),
                            )
                            Text(
                                text = movementUiState.acceptableLost.toInt().toString(),
                                modifier = modifier,
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
                                onClick = onCancel,
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = "Close icon",
                                    modifier = Modifier
                                        .size(24.dp)
                                )
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
}


@Composable
fun ArmyDialogRow(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    shipType: ShipType,
    startLocation: Int?,
    endLocation: Int?,
    isWarperPresent: Boolean,
    movementUiState: MovementUiState,
    weightOfNumbers: Float,
    weightOfName: Float,
    weightOfButtons: Float,
    isInfo: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.wrapContentWidth()
    ) {
        Row(
            modifier = modifier
                .weight(weightOfName),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    battleModel.getShipImage(shipType = shipType)
                ),
                contentDescription = "Ship icon",
                modifier = Modifier
                    .size(36.dp)
            )

            Spacer(modifier = modifier.size(4.dp))

            Text(
                text = mapOfShips[shipType]?.let { stringResource(id = it.nameId) } ?: "Unknown",
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .clickable {
                        battleModel.showShipInfoDialog(true)
                        battleModel.changeShipTypeToShow(shipType = shipType)
                    }
            )
        }

        Text(
            text = battleModel.getNumberOfShip(
                location = endLocation,
                shipType = shipType,
                isForEnemy = true
            ).toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(weightOfNumbers)
        )

        if (!isInfo) {
            Text(
                text = battleModel.setShipsToMoveString(
                    shipType = shipType,
                    movementUiState = movementUiState
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(weightOfNumbers)
            )
        }


        Text(
            text = battleModel.setShipsOnPositionString(
                shipType = shipType,
                movementUiState = movementUiState
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(weightOfNumbers)
        )

        if (!isInfo) {

            Button(
                onClick = { battleModel.removeShip(shipType = shipType) },
                enabled = battleModel.checkRemoveShip(shipType = shipType),
                modifier = modifier
                    .weight(weightOfButtons),
                contentPadding = PaddingValues(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.remove),
                    contentDescription = "Remove icon"
                )
            }


            Button(
                onClick = { battleModel.addShip(shipType = shipType) },
                enabled = battleModel.checkAddShip(
                    isWarperPresent = isWarperPresent,
                    shipType = shipType,
                    startLocation = startLocation,
                    endLocation = endLocation
                ),
                modifier = Modifier
                    .weight(weightOfButtons),
                contentPadding = PaddingValues(4.dp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add icon"
                )
            }
        }
    }
}