package com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen

import android.content.ContentValues
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.data.model.mapOfShips
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Tasks
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialDialog
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.ShipInfoDialog
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel

@Composable
fun ArmyDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    tutorialModel: TutorialViewModel,
    settingsModel: SettingViewModel,
    timerModel: TimerViewModel,
    toShow: Boolean,
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
        settingsModel = settingsModel
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
            val removeEnabled = battleModel.checkRemoveShip(shipType = shipType)
            val removeIcon = painterResource(if(removeEnabled) R.drawable.remove else R.drawable.remove_disable)
            val addEnabled = battleModel.checkAddShip(
                isWarperPresent = isWarperPresent,
                shipType = shipType,
                startLocation = startLocation,
                endLocation = endLocation
            )
            val addIcon = painterResource(if(addEnabled) R.drawable.add else R.drawable.add_disable)

            Button(
                onClick = { battleModel.removeShip(shipType = shipType) },
                enabled = removeEnabled,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .weight(weightOfButtons),
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = removeIcon,
                    contentDescription = "Remove icon",
                    tint = Color.Unspecified
                )
            }


            Button(
                onClick = { battleModel.addShip(shipType = shipType) },
                enabled = addEnabled,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .weight(weightOfButtons),
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = addIcon,
                    contentDescription = "Add icon",
                    tint = Color.Unspecified
                )
            }
        }
    }
}