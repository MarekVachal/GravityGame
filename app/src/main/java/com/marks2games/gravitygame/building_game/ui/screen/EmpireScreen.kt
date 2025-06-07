package com.marks2games.gravitygame.building_game.ui.screen

import android.util.Log
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
import com.marks2games.gravitygame.building_game.ui.utils.DistrictNodeButton
import com.marks2games.gravitygame.building_game.ui.utils.ErrorList
import com.marks2games.gravitygame.building_game.ui.utils.PlanetList
import com.marks2games.gravitygame.building_game.ui.utils.TopGameStatsRow
import com.marks2games.gravitygame.building_game.ui.utils.TransportsList
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.ui.utils.MapScreen

@Composable
fun EmpireOverview(
    modifier: Modifier = Modifier,
    empireModel: EmpireViewModel,
    transportModel: TransportViewModel,
    planetModel: PlanetViewModel,
    onBackButtonClicked: () -> Unit,
    toResearchScreen: () -> Unit
) {
    val empire by empireModel.empire.collectAsState()
    val empireUiState by empireModel.empireUiState.collectAsState()
    val testEmpire by empireModel.testEmpire.collectAsState()
    val mapState by planetModel.mapUiState.collectAsState()
    val planetState by planetModel.planetUiState.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(!empire.hasLaunched){
            empireModel.launchEmpireScreen()
        }
        Log.d("EmpireOverview", "Technologies: ${empire.technologies}")
    }

    LaunchedEffect(testEmpire.planets.find { it.id == empireUiState.planetIdForDetails }?.districts){
        planetModel.updateDistrictNodes(testEmpire.planets.find { it.id == empireUiState.planetIdForDetails })
    }

    empire.planets.firstOrNull{it.id == empireUiState.planetIdForDetails}?.let{
        DistrictDialog(
            planet = it,
            district = empireUiState.districtForDialog,
            empireModel = empireModel,
            empireUiState = empireUiState,
            toShow = empireUiState.isDistrictDialogShown,
            planets = empire.planets
        )
    }



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
                empireUiState = empireUiState,
                toResearchScreen = { toResearchScreen() }
            )
        }

        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            empireUiState.districtForDialog?.let{ district ->
                MapScreen(
                    modifier = Modifier.fillMaxSize(),
                    mapModel = planetModel,
                    nodeInfoContent = { DistrictNodeButton(node = district, mapUiState = mapState) },
                    onNodeClicked = {
                        empireModel.openDistrictDetails(
                            planetId = empireUiState.planetIdForDetails,
                            district = district
                        )
                    },
                    onLongNodeClicked = {},
                    backgroundImageId = R.drawable.battle_background
                )
            }

            if(empireUiState.isPlanetListShown){
                Box(
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .width(screenWidth * 0.9f)
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
                            empireModel.updateIsPlanetListShown()
                            planetModel.updatePlanetUiState(it)
                        }
                    )
                }
            } else {
                MapScreen(
                    modifier = Modifier.fillMaxSize(),
                    mapModel = planetModel,
                    nodeInfoContent = { DistrictNodeButton(node = it.district, mapUiState = mapState) },
                    onNodeClicked = {
                        empireModel.openDistrictDetails(
                            planetId = empireUiState.planetIdForDetails,
                            district = it.district
                        )
                    },
                    onLongNodeClicked = {},
                    backgroundImageId = R.drawable.battle_background
                )
            }

            /*
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

             */
            if (empireUiState.isErrorsShown) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    ErrorList(
                        modifier = modifier,
                        errors = empireUiState.errors,
                        empire = empire,
                        empireModel = empireModel
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


