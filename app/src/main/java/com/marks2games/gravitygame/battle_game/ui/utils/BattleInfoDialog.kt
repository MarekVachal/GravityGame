package com.marks2games.gravitygame.battle_game.ui.utils

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.marks2games.gravitygame.battle_game.data.model.Location
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.data.model.mapOfShips
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.MovementUiState

@Composable
fun BattleInfoDialog(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    toShow: Boolean,
    location: Int,
    context: Context,
    onDismissRequest: () -> Unit = { battleModel.showBattleInfo(location = location, toShow = false) }
) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val weightOfName = 0.2f
    val weightOfNumbers = 0.13f
    val padding = 16.dp

    if (toShow) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
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
                            .wrapContentSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = modifier.fillMaxWidth()
                        ){
                            Text(text = stringResource(
                                id = R.string.battleResultOnLocation,
                                battleModel.callBattleResultInBattleInfo(locationListUiState.locationList[location].lastBattleResult, context)

                            ))
                        }
                        HorizontalDivider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = modifier
                        ) {
                            Text(
                                text = stringResource(id = R.string.nameShip),
                                textAlign = TextAlign.Left,
                                modifier = Modifier
                                    .weight(weightOfName)
                            )
                            Text(
                                text = stringResource(id = R.string.enemyShipsBeforeBattle),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(weightOfNumbers)
                            )
                            Text(
                                text = stringResource(id = R.string.enemyShipsLost),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(weightOfNumbers)
                            )
                            Text(
                                text = stringResource(id = R.string.myShipsBeforeBattle),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(weightOfNumbers)
                            )
                            Text(
                                text = stringResource(id = R.string.myShipsLost),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(weightOfNumbers)
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = modifier
                        ) {
                            BattleInfoRow(
                                shipType = ShipType.CRUISER,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                movementUiState = movementUiState,
                                locationList = locationListUiState.locationList,
                                battleModel = battleModel
                            )
                        }
                        HorizontalDivider()
                        Row (
                            modifier = modifier
                        ){
                            BattleInfoRow(
                                shipType = ShipType.DESTROYER,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                movementUiState = movementUiState,
                                locationList = locationListUiState.locationList,
                                battleModel = battleModel
                            )
                        }
                        HorizontalDivider()
                        Row (
                            modifier = modifier
                        ){
                            BattleInfoRow(
                                shipType = ShipType.GHOST,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                movementUiState = movementUiState,
                                locationList = locationListUiState.locationList,
                                battleModel = battleModel
                            )
                        }
                        HorizontalDivider()
                        Row (
                            modifier = modifier
                        ){
                            BattleInfoRow(
                                shipType = ShipType.WARPER,
                                weightOfName = weightOfName,
                                weightOfNumbers = weightOfNumbers,
                                movementUiState = movementUiState,
                                locationList = locationListUiState.locationList,
                                battleModel = battleModel
                            )
                        }
                        Row (
                            modifier = modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ){
                            Button(onClick = {onDismissRequest()}) {
                                Icon(painter = painterResource(id = R.drawable.close), contentDescription = "Close icon")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BattleInfoRow(
    modifier: Modifier = Modifier,
    shipType: ShipType,
    weightOfName: Float,
    weightOfNumbers: Float,
    movementUiState: MovementUiState,
    locationList: List<Location>,
    battleModel: BattleViewModel
){
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
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
                        battleModel.changeShipTypeToShow(shipType = shipType)
                        battleModel.showShipInfoDialog(true)
                    }
            )
        }
        Text(
            text = locationList[movementUiState.indexOfBattleLocationToShow].originalEnemyShipList[shipType].toString(),
            textAlign = TextAlign.Center,
            modifier = modifier.weight(weightOfNumbers)
        )
        Text(
            text = locationList[movementUiState.indexOfBattleLocationToShow].mapEnemyLost[shipType].toString(),
            textAlign = TextAlign.Center,
            modifier = modifier.weight(weightOfNumbers)
        )
        VerticalDivider()
        Text(
            text = locationList[movementUiState.indexOfBattleLocationToShow].originalMyShipList[shipType].toString(),
            textAlign = TextAlign.Center,
            modifier = modifier.weight(weightOfNumbers)
        )
        Text(
            text = locationList[movementUiState.indexOfBattleLocationToShow].mapMyLost[shipType].toString(),
            textAlign = TextAlign.Center,
            modifier = modifier.weight(weightOfNumbers)
        )
    }
}
