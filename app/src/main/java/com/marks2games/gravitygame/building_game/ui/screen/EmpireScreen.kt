package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.ui.utils.ActionListPopup
import com.marks2games.gravitygame.building_game.ui.utils.ErrorList
import com.marks2games.gravitygame.building_game.ui.utils.PlanetList
import com.marks2games.gravitygame.building_game.ui.utils.TopGameStatsRow
import com.marks2games.gravitygame.building_game.ui.utils.TransportListPopup
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel

@Composable
fun EmpireOverview(
    modifier: Modifier = Modifier,
    empireModel: EmpireViewModel,
    transportModel: TransportViewModel,
    onBackButtonClicked: () -> Unit,
    toResearchScreen: () -> Unit,
    toPlanetScreen: (Int?, Empire, Empire) -> Unit
) {
    val empire by empireModel.empire.collectAsState()
    val empireUiState by empireModel.empireUiState.collectAsState()
    val testEmpire by empireModel.testEmpire.collectAsState()

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenWidth = with(density) { windowInfo.containerSize.width.toDp() }

    LaunchedEffect(Unit) {
        if(!empire.hasLaunched){
            empireModel.launchEmpireScreen()
        }
    }

/*

To use it for click on item on transportMenu
    empireUiState.planetForTransport?.let {
        val planet = it
        TransportDialog(
            modifier = modifier,
            transportModel = transportModel,
            planet = planet,
            toShow = empireUiState.isTransportDialogShown,
            empire = empire,
            closeDialog = { empireModel.closeTransportDialog() },
            addTransportAction = {
                empireModel.addTransportAction(
                    context = context,
                    planetId = planet.id,
                    transport = it
                )
            },
            onPlanetNotFound = { empireModel.closeTransportDialog() }
        )
    }

 */

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.battle_background),
            contentDescription = "Battle map background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            TopGameStatsRow(
                empire = empire,
                testEmpire = testEmpire,
                toResearchScreen = { toResearchScreen() },
                actionsCount = empire.actions.size,
                transportsCount = empire.transports.size,
                isErrorsListEmpty = empireModel.isErrorListEmpty(),
                errorsSize = empireUiState.errors.size,
                onErrorMenuClick = { empireModel.onErrorMenuClick() },
                onActionMenuClick = { empireModel.onActionMenuClick() },
                onTransportMenuClick = { empireModel.onTransportMenuClick() },
                onPlanetMenuClick = { },
                getTechnologyPrice = { empireModel.getTechnologyPrice() },
                isPlanetMenuPresent = false
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(screenWidth * 0.9f)
                    .fillMaxWidth()
            ) {
                PlanetList(
                    modifier = Modifier.fillMaxWidth(),
                    empire = empire,
                    testEmpire = testEmpire,
                    onPlanetClick = {
                        toPlanetScreen(it?.id, empire, testEmpire)
                    }
                )
            }

            if (empireUiState.isErrorsShown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart)
                ) {
                    ErrorList(
                        modifier = Modifier
                            .width(screenWidth * 0.5f),
                        errors = empireUiState.errors,
                        empire = empire,
                        onCloseErrorMenuClick = { empireModel.onErrorMenuClick() }
                    )
                }
            }
            if (empireUiState.isActionsShown) {
                ActionListPopup(
                    modifier = Modifier
                        .width(screenWidth * 0.5f),
                    actions = empire.actions,
                    deleteAllActions = { empireModel.deleteAllActions() },
                    getActionDescription = { empireModel.getActionDescription(it) },
                    deleteAction = { empireModel.deleteAction(it) },
                    onDismiss = { empireModel.dismissActionMenu() }
                )
            }
            if (empireUiState.isTransportMenuShown) {
                TransportListPopup(
                    modifier = Modifier
                        .width(screenWidth * 0.5f),
                    empire = empire,
                    transports = empireModel.getAllTransports(),
                    onTransportClick = {
                        empireModel.onTransportMenuClick()
                        transportModel.updateTransportDialogOnTransportClick(it)
                    },
                    deleteAllTransports = { empireModel.deleteAllTransports() },
                    deleteTransport = { empireModel.deleteTransport(it) },
                    onDismiss = {empireModel.dismissTransportMenu()}
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier,
                onClick = { onBackButtonClicked() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.undo),
                    contentDescription = "Back button",
                    tint = Color.Unspecified
                )
            }
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterVertically)
            ){
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = stringResource(R.string.turns, empireUiState.turn)
                )
            }

            ExtendedFloatingActionButton(
                modifier = Modifier,
                onClick = { empireModel.newTurn() }
            ) {
                Text(text = stringResource(R.string.newTurn))
            }
        }

    }
}


