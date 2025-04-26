package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.ui.utils.ActionList
import com.marks2games.gravitygame.building_game.ui.utils.DistrictList
import com.marks2games.gravitygame.building_game.ui.utils.ErrorList
import com.marks2games.gravitygame.building_game.ui.utils.PlanetList
import com.marks2games.gravitygame.building_game.ui.utils.TopGameStatsRow
import com.marks2games.gravitygame.building_game.ui.utils.TransportsList
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel

@Composable
fun EmpireOverview(
    modifier: Modifier = Modifier,
    empireModel: EmpireViewModel,
    transportModel: TransportViewModel,
    onBackButtonClicked: () -> Unit
) {
    val empire by empireModel.empire.collectAsState()
    val empireUiState by empireModel.empireUiState.collectAsState()
    val testEmpire by empireModel.testEmpire.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        empireModel.launchEmpireScreen()
    }

    DistrictDialog(
        modifier = modifier,
        planet = empire.planets[empireUiState.planetIdForDetails],
        district = empireUiState.districtForDialog,
        empireModel = empireModel,
        empireUiState = empireUiState,
        toShow = empireUiState.isDistrictDialogShown,
        planets = empire.planets
    )


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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.battle_background),
            contentDescription = "Battle map background",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.matchParentSize()
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            TopGameStatsRow(
                empire = empire,
                testEmpire = testEmpire,
                empireModel = empireModel,
                empireUiState = empireUiState
            )
        }

        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = modifier
                    .align(Alignment.TopEnd)
                    .width(screenWidth * 0.5f)
                    .fillMaxWidth()
            ) {
                PlanetList(
                    modifier = modifier.fillMaxWidth(),
                    empire = empire,
                    testEmpire = testEmpire,
                    onPlanetClick = {
                        empireModel.updateShowDistrictList(
                            isShown = true,
                            planet = it
                        )
                    }
                )
            }
            if (empireUiState.isShownDistrictList) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    DistrictList(
                        modifier = modifier,
                        planetId = empireUiState.planetIdForDetails,
                        empire = testEmpire,
                        empireModel = empireModel,
                        screenWidth = screenWidth
                    )
                }
            }
            if (empireUiState.isErrorsShown) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    ErrorList(
                        modifier = modifier,
                        empireUiState.errors,
                        empire
                    )
                }
            }
            if (empireUiState.isActionsShown) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    ActionList(
                        modifier = modifier,
                        actions = empire.actions,
                        empire = empire,
                        empireModel = empireModel
                    )
                }
            }
            if (empireUiState.isTransportMenuShown) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    TransportsList(
                        modifier = modifier,
                        empire = empire,
                        empireModel = empireModel,
                        transports = empireModel.getAllTransports(),
                        onTransportClick = {
                            empireModel.updateTransportMenuShown(true)
                            transportModel.updateTransportDialogOnTransportClick(it)
                        }
                    )
                }
            }
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = modifier,
                onClick = { onBackButtonClicked() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.undo),
                    contentDescription = "Back button",
                    tint = Color.Unspecified
                )
            }
            ExtendedFloatingActionButton(
                modifier = modifier,
                onClick = { empireModel.newTurn() }
            ) {
                Text(text = stringResource(R.string.newTurn))
            }
        }

    }
}


