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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.ui.utils.ActionListPopup
import com.marks2games.gravitygame.building_game.ui.utils.DistrictNodeButton
import com.marks2games.gravitygame.building_game.ui.utils.ErrorList
import com.marks2games.gravitygame.building_game.ui.utils.ResourceInfoDialog
import com.marks2games.gravitygame.building_game.ui.utils.ResourceList
import com.marks2games.gravitygame.building_game.ui.utils.TopGameStatsRow
import com.marks2games.gravitygame.building_game.ui.utils.TransportListPopup
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.ui.utils.MapScreen

@Composable
fun PlanetScreen(
    modifier: Modifier = Modifier,
    planetModel: PlanetViewModel,
    transportModel: TransportViewModel,
    onBackButtonClicked: (Int?, List<Action>) -> Unit,
    toResearchScreen: () -> Unit,
    toEmpireScreen: (Int?, List<Action>) -> Unit,
    onDismiss: () -> Unit = { planetModel.showResourceInfoDialog(false) },
) {
    val uiState by planetModel.planetUiState.collectAsState()
    val mapState by planetModel.mapUiState.collectAsState()

    val context = LocalContext.current

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenWidth = with(density) { windowInfo.containerSize.width.toDp() }

    uiState.planetId?.let { id ->
        val planet = uiState.empire?.planets?.find { it.id == id }
        DistrictDialog(
            planet = planet,
            district = uiState.districtForDialog,
            planetModel = planetModel,
            planetUiState = uiState,
            toShow = uiState.isDistrictDialogShown,
            planets = uiState.empire?.planets ?: emptyList()
        )

        TransportDialog(
            modifier = modifier,
            transportModel = transportModel,
            planet = planet,
            toShow = uiState.isTransportDialogShown,
            empire = uiState.empire,
            closeDialog = { planetModel.closeTransportDialog() },
            addTransportAction = {
                planetModel.addTransportAction(
                    context = context,
                    transport = it
                )
            },
            onPlanetNotFound = { planetModel.closeTransportDialog() }
        )
    }

    ResourceInfoDialog(
        resource = uiState.resourceType,
        toShow = uiState.showResourceInfoDialog,
        onDismiss = onDismiss,
        onConfirm = onDismiss
    )

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
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            TopGameStatsRow(
                empire = uiState.empire,
                testEmpire = uiState.testEmpire,
                toResearchScreen = { toResearchScreen() },
                actionsCount = uiState.actions.size,
                transportsCount = uiState.transports.size,
                isErrorsListEmpty = planetModel.isErrorListEmpty(),
                errorsSize = uiState.errors.size,
                onErrorMenuClick = { planetModel.onErrorMenuClick() },
                onActionMenuClick = { planetModel.onActionMenuClick() },
                onTransportMenuClick = { planetModel.onTransportMenuClick() },
                onPlanetMenuClick = { toEmpireScreen(uiState.planetId, uiState.actions) },
                getTechnologyPrice = { planetModel.getTechnologyPrice() },
                isPlanetMenuPresent = true
            )
        }

        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
        ) {

            Box(
                modifier = modifier
                    .align(Alignment.CenterEnd)
                    .width(screenWidth * 0.80f)
                    .fillMaxWidth()
            ) {
                MapScreen(
                    modifier = Modifier.fillMaxSize(),
                    mapModel = planetModel,
                    nodeInfoContent = {
                        DistrictNodeButton(
                            node = it.district,
                            mapUiState = mapState
                        )
                    },
                    onNodeClicked = { planetModel.openDistrictDetails(it.district) },
                    onLongNodeClicked = {},
                    backgroundImageId = null,
                    isMapRotating = true,
                    isBackgroundRotating = false
                )
            }

            ResourceList(
                modifier = Modifier.align(Alignment.CenterStart),
                planet = uiState.empire?.planets?.first { it.id == uiState.planetId },
                testPlanet = uiState.testEmpire?.planets?.first { it.id == uiState.planetId },
                showResourceInfoDialog = { planetModel.showResourceInfoDialog(true) },
                changeResource = { planetModel.changeResource(it) }
            )

            if (uiState.isErrorsShown) {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f)
                        .align(Alignment.TopStart)
                ) {
                    ErrorList(
                        modifier = modifier,
                        errors = uiState.errors,
                        empire = uiState.empire,
                        onCloseErrorMenuClick = { planetModel.onErrorMenuClick() },
                    )
                }
            }
            if (uiState.isActionsShown) {
                ActionListPopup(
                    modifier = Modifier
                        .width(screenWidth * 0.5f),
                    actions = uiState.actions,
                    deleteAllActions = { planetModel.deleteAllActions() },
                    getActionDescription = { planetModel.getActionDescription(it) },
                    deleteAction = { planetModel.deleteAction(it) },
                    onDismiss = { planetModel.dismissActionMenu() }
                )
            }
            if (uiState.isTransportMenuShown) {
                uiState.empire?.let { empire ->
                    TransportListPopup(
                        modifier = Modifier
                            .width(screenWidth * 0.5f),
                        empire = empire,
                        transports = planetModel.getAllTransports(),
                        onTransportClick = {
                            planetModel.onTransportMenuClick()
                            transportModel.updateTransportDialogOnTransportClick(it)
                        },
                        deleteAllTransports = { planetModel.deleteAllTransports() },
                        deleteTransport = { planetModel.deleteTransport(it) },
                        onDismiss = { planetModel.dismissTransportMenu() }
                    )
                }
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
                onClick = { onBackButtonClicked(uiState.planetId, uiState.actions) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.undo),
                    contentDescription = "Back button",
                    tint = Color.Unspecified
                )
            }
            ExtendedFloatingActionButton(
                modifier = Modifier,
                onClick = { planetModel.testTurn(uiState.empire) }
            ) {
                Text(text = stringResource(R.string.testTurn))
            }
        }
    }
}