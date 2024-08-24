package com.example.gravitygame.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.uiStates.SelectArmyUiState
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.ui.utils.ProgressIndicator
import com.example.gravitygame.ui.utils.ShipInfoDialog
import com.example.gravitygame.viewModels.BattleViewModel
import com.example.gravitygame.viewModels.ProgressIndicatorViewModel
import com.example.gravitygame.viewModels.SelectArmyViewModel

@Composable
fun SelectArmyScreen(
    selectArmyModel: SelectArmyViewModel,
    onNextButtonClicked: () -> Unit,
    battleModel: BattleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_army_background),
            contentDescription = "Background for screen Select Army",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.matchParentSize()
        )
        Card (
            modifier = modifier
                .align(Alignment.TopCenter)
                .wrapContentWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ){
            Text(
                text = stringResource(id = R.string.chooseArmyTitle),
                modifier = modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        SelectArmy(
            modifier = Modifier.align(Alignment.Center),
            selectArmyModel = selectArmyModel,
            addShip = { selectArmyModel.addShip(it) },
            removeShip = { selectArmyModel.removeShip(it) },
            battleModel = battleModel
        )

        FloatingActionButton(
            onClick = {
                if (checkArmySize(selectArmyModel.selectArmyUiState.value, battleModel)) {
                    battleModel.createArmyList(selectArmyUiState = selectArmyModel.selectArmyUiState.value)
                    onNextButtonClicked()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.exactArmySize),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier
                .padding(end = 16.dp)
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.ok),
                modifier = modifier.padding(16.dp))
        }

    }

}

@Composable
fun SelectArmy(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    selectArmyModel: SelectArmyViewModel,
    addShip: (ShipType) -> Unit,
    removeShip: (ShipType) -> Unit,
    onDismissRequest: () -> Unit = { selectArmyModel.showShipInfoDialog(false) }
) {
    val selectArmyUiState by selectArmyModel.selectArmyUiState.collectAsState()
    val column1Weight = 2f
    val column2Weight = 1f
    val column3Weight = 0.5f
    val column4Weight = 1f
    val column5Weight = 2.5f
    var shipType by rememberSaveable { mutableStateOf(ShipType.CRUISER) }
    var initialization by rememberSaveable { mutableStateOf(false) }

    if (!initialization) {
        selectArmyModel.cleanUiStates()
        initialization = true
    }

    ShipInfoDialog(
        shipType = shipType,
        toShow = selectArmyUiState.showShipInfoDialog,
        onDismissRequest = onDismissRequest,
        confirmButton = onDismissRequest
    )

    Card(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {

                Text(
                    text = stringResource(id = R.string.cruiser),
                    modifier = Modifier
                        .weight(column1Weight)
                        .clickable {
                            selectArmyModel.showShipInfoDialog(true)
                            shipType = ShipType.CRUISER
                        }
                )

                Button(
                    onClick = { removeShip(ShipType.CRUISER) },
                    enabled = selectArmyUiState.numberCruisers != 0,
                    modifier = Modifier
                        .weight(column2Weight)
                ) {
                    Text(text = stringResource(R.string.minus))
                }

                Text(
                    text = selectArmyUiState.numberCruisers.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(column3Weight)
                )

                Button(
                    onClick = { addShip(ShipType.CRUISER) },
                    enabled = !checkArmySize(selectArmyUiState, battleModel),
                    modifier = Modifier
                        .weight(column4Weight)
                ) {
                    Text(text = stringResource(R.string.plus))
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(id = R.string.destroyer),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(column1Weight)
                        .clickable {
                            selectArmyModel.showShipInfoDialog(true)
                            shipType = ShipType.DESTROYER
                        }
                )

                Button(
                    onClick = { removeShip(ShipType.DESTROYER) },
                    enabled = selectArmyUiState.numberDestroyers != 0,
                    modifier = Modifier
                        .weight(column2Weight)
                ) {
                    Text(text = stringResource(R.string.minus))
                }

                Text(
                    text = selectArmyUiState.numberDestroyers.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(column3Weight)
                )

                Button(
                    onClick = { addShip(ShipType.DESTROYER) },
                    enabled = !checkArmySize(selectArmyUiState, battleModel),
                    modifier = Modifier
                        .weight(column4Weight)
                ) {
                    Text(text = stringResource(R.string.plus))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(id = R.string.ghost),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(column1Weight)
                        .clickable {
                            selectArmyModel.showShipInfoDialog(true)
                            shipType = ShipType.GHOST
                        }
                )

                Button(
                    onClick = { removeShip(ShipType.GHOST) },
                    enabled = selectArmyUiState.numberGhosts != 0,
                    modifier = Modifier
                        .weight(column2Weight)
                ) {
                    Text(text = stringResource(R.string.minus))
                }
                Text(
                    text = selectArmyUiState.numberGhosts.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(column3Weight)
                )

                Button(
                    onClick = { addShip(ShipType.GHOST) },
                    enabled = !checkArmySize(selectArmyUiState, battleModel),
                    modifier = Modifier
                        .weight(column4Weight)
                ) {
                    Text(text = stringResource(R.string.plus))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(id = R.string.warper),
                    modifier = Modifier
                        .weight(column1Weight)
                        .clickable {
                            selectArmyModel.showShipInfoDialog(true)
                            shipType = ShipType.WARPER
                        }
                )

                Text(
                    text = "1",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(column5Weight)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(id = R.string.sumArmy),
                    modifier = Modifier
                        .weight(column1Weight)
                )

                Text(
                    text = countArmySize(selectArmyUiState).toString() + "/11",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(column5Weight)
                )
            }
        }
    }
}

private fun checkArmySize(
    selectArmyUiState: SelectArmyUiState,
    battleModel: BattleViewModel
): Boolean {
    val shipLimit = battleModel.battleMap?.shipLimitOnMap ?: 0
    return countArmySize(selectArmyUiState) == shipLimit
}

private fun countArmySize(selectArmyUiState: SelectArmyUiState): Int {
    return selectArmyUiState.let { it.numberCruisers + it.numberDestroyers + it.numberGhosts + 1 }
}
