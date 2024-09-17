package com.example.gravitygame.ui.screens.selectArmyScreen

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.tutorial.Tasks
import com.example.gravitygame.tutorial.TutorialDialog
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.ui.screens.infoDialogsScreens.ShipInfoDialog
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel

@Composable
fun SelectArmyScreen(
    selectArmyModel: SelectArmyViewModel,
    onNextButtonClicked: () -> Unit,
    battleModel: BattleViewModel,
    settingsModel: SettingViewModel,
    modifier: Modifier = Modifier,
    tutorialModel: TutorialViewModel,
    context: Context,
    onDismissRequest: () -> Unit = { selectArmyModel.showShipInfoDialog(false) },
    isEnabled: Boolean = !selectArmyModel.checkArmySize(battleModel)
) {
    val selectArmyUiState by selectArmyModel.selectArmyUiState.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()
    val column1Weight = 2f
    val buttonWeight = 1f
    val column3Weight = 0.5f

    if (!selectArmyUiState.initialized) {
        selectArmyModel.cleanUiStates()
        selectArmyModel.initialization(true)
    }
    TutorialDialog(tutorialModel = tutorialModel, toShow = tutorialUiState.showTutorialDialog, timerModel = null, settingsModel = settingsModel, context = context)
    if(!tutorialUiState.infoShipTask && tutorialUiState.numberShipsTask && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.INFO_SHIP)
    }

    if(!tutorialUiState.numberShipsTask && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(true, Tasks.NUMBER_SHIPS)
    }

    ShipInfoDialog(
        shipType = selectArmyUiState.shipType,
        toShow = selectArmyUiState.showShipInfoDialog,
        onDismissRequest = onDismissRequest,
        confirmButton = onDismissRequest
    )

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
                .width(IntrinsicSize.Max)
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp)
        ){
            Column (
                modifier = modifier.padding(16.dp)
            ){
                Row(
                    modifier = modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = stringResource(id = R.string.chooseArmyTitle),
                        modifier = modifier,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }


                ShipRow(
                    column3Weight = column3Weight,
                    column1Weight = column1Weight,
                    buttonWeight = buttonWeight,
                    shipType = ShipType.CRUISER,
                    selectArmyModel = selectArmyModel,
                    onRemoveButtonClick = { selectArmyModel.removeShip(ship = ShipType.CRUISER) },
                    onAddButtonClick = { selectArmyModel.addShip(ship = ShipType.CRUISER) },
                    isEnabled = isEnabled,
                    image = R.drawable.cruiser,
                    shipName = R.string.cruiser,
                    numberOfShip = selectArmyUiState.numberCruisers
                )

                HorizontalDivider()

                ShipRow(
                    column3Weight = column3Weight,
                    column1Weight = column1Weight,
                    buttonWeight = buttonWeight,
                    shipType = ShipType.DESTROYER,
                    selectArmyModel = selectArmyModel,
                    onRemoveButtonClick = { selectArmyModel.removeShip(ship = ShipType.DESTROYER) },
                    onAddButtonClick = { selectArmyModel.addShip(ship = ShipType.DESTROYER) },
                    isEnabled = isEnabled,
                    image = R.drawable.destroyer,
                    shipName = R.string.destroyer,
                    numberOfShip = selectArmyUiState.numberDestroyers
                )

                HorizontalDivider()

                ShipRow(
                    column3Weight = column3Weight,
                    column1Weight = column1Weight,
                    buttonWeight = buttonWeight,
                    shipType = ShipType.GHOST,
                    selectArmyModel = selectArmyModel,
                    onRemoveButtonClick = { selectArmyModel.removeShip(ship = ShipType.GHOST) },
                    onAddButtonClick = { selectArmyModel.addShip(ship = ShipType.GHOST) },
                    isEnabled = isEnabled,
                    image = R.drawable.ghost,
                    shipName = R.string.ghost,
                    numberOfShip = selectArmyUiState.numberGhosts
                )

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.warper),
                        contentDescription = "Warper icon",
                        modifier = Modifier
                            .weight(column3Weight)
                            .size(48.dp)
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    Text(
                        text = stringResource(id = R.string.warper),
                        modifier = Modifier
                            .weight(column1Weight)
                            .clickable {
                                selectArmyModel.showShipInfoDialog(true)
                                selectArmyModel.changeShipType(shipType = ShipType.WARPER)
                            }
                    )

                    Text(
                        text = "1",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(buttonWeight*2 + column3Weight)
                    )
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .height(48.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.sumArmy),
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .weight(column1Weight)
                    )

                    Text(
                        text = selectArmyModel.countArmySize().toString() + "/11",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(buttonWeight*2 + column3Weight)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (selectArmyModel.checkArmySize(battleModel)) {
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
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Check icon"
            )
        }

    }

}

@Composable
private fun ShipRow(
    modifier: Modifier = Modifier,
    column3Weight: Float,
    column1Weight: Float,
    buttonWeight: Float,
    shipType: ShipType,
    selectArmyModel: SelectArmyViewModel,
    onRemoveButtonClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    image: Int,
    shipName: Int,
    numberOfShip: Int,
    isEnabled: Boolean
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .width(IntrinsicSize.Max)
    ) {

        Image(
            painter = painterResource(id = image),
            contentDescription = "Cruiser icon",
            modifier = Modifier
                .weight(column3Weight)
                .size(48.dp)
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = stringResource(id = shipName),
            modifier = Modifier
                .weight(column1Weight)
                .clickable {
                    selectArmyModel.showShipInfoDialog(true)
                    selectArmyModel.changeShipType(shipType = shipType)
                }
        )

        Button(
            onClick = onRemoveButtonClick,
            enabled = numberOfShip != 0,
            modifier = Modifier
                .weight(buttonWeight)
        ) {
            Icon(painter = painterResource(id = R.drawable.remove), contentDescription = "Remove icon")
        }

        Text(
            text = numberOfShip.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(column3Weight)
        )

        Button(
            onClick = onAddButtonClick,
            enabled = isEnabled,
            modifier = Modifier
                .weight(buttonWeight)
        ) {
            Icon(painter = painterResource(id = R.drawable.add), contentDescription = "Add icon")
        }
    }
}