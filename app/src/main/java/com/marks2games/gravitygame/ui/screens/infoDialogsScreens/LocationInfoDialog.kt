package com.marks2games.gravitygame.ui.screens.infoDialogsScreens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.models.ShipType
import com.marks2games.gravitygame.ui.screens.battleMapScreen.ArmyDialogRow
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun LocationInfoDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    toShow: Boolean,
    onDismissRequest: () -> Unit = { battleModel.closeLocationInfoDialog() },
    closeShipInfoDialog: () -> Unit = {
        battleModel.showShipInfoDialog(false)

    }

) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val weightOfName = 0.3f
    val weightOfNumbers = 0.2f
    val weightOfButtons = 0.1f
    val padding = 16.dp

    ShipInfoDialog(
        shipType = movementUiState.shipTypeToShow,
        toShow = movementUiState.showShipInfoDialog,
        onDismissRequest = closeShipInfoDialog,
        confirmButton = closeShipInfoDialog
    )

    if (toShow) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            LaunchedEffect (Unit) {
                battleModel.initializeLocationDialogValues()
            }

            Card(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .wrapContentSize(),
                shape = RoundedCornerShape(padding)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .padding(padding)
                ) {
                    Column(
                        modifier = modifier
                            .width(IntrinsicSize.Min)
                            .wrapContentSize()
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
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            ArmyDialogRow(
                                shipType = ShipType.CRUISER,
                                startLocation = movementUiState.locationForInfo,
                                endLocation = movementUiState.locationForInfo,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = true
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            ArmyDialogRow(
                                shipType = ShipType.DESTROYER,
                                startLocation = movementUiState.locationForInfo,
                                endLocation = movementUiState.locationForInfo,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = true
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            ArmyDialogRow(
                                shipType = ShipType.GHOST,
                                startLocation = movementUiState.locationForInfo,
                                endLocation = movementUiState.locationForInfo,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = true
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            ArmyDialogRow(
                                shipType = ShipType.WARPER,
                                startLocation = movementUiState.locationForInfo,
                                endLocation = movementUiState.locationForInfo,
                                isWarperPresent = movementUiState.isWarperPresent,
                                battleModel = battleModel,
                                movementUiState = movementUiState,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                weightOfButtons = weightOfButtons,
                                isInfo = true
                            )
                        }
                        Row(
                            modifier = modifier
                                .padding(horizontal = padding, vertical = 4.dp)
                        ) {
                            Slider(
                                value = movementUiState.acceptableLost,
                                onValueChange = {battleModel.changeValueAcceptableLost(value = it)},
                                valueRange = 1f..6f,
                                steps = 4,
                                modifier = modifier
                                    .border(
                                        width = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    .padding(horizontal = 8.dp),
                            )
                        }
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = modifier
                            ) {
                                Text(
                                    text = stringResource(R.string.maxLosses),
                                    modifier = modifier
                                )
                                Text(
                                    text = movementUiState.acceptableLost.toInt().toString(),
                                    modifier = modifier.padding(start = padding)
                                )
                            }
                            Button(
                                onClick = onDismissRequest
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check icon"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}