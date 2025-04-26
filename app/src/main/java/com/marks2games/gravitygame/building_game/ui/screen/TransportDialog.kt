package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.data.model.TransportUiState
import com.marks2games.gravitygame.building_game.ui.utils.PlanetCard
import com.marks2games.gravitygame.building_game.ui.utils.PlanetList
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel

@Composable
fun TransportDialog(
    modifier: Modifier,
    transportModel: TransportViewModel,
    planet: Planet,
    toShow: Boolean,
    empire: Empire,
    closeDialog: () -> Unit,
    addTransportAction: (Transport) -> Unit,
    onPlanetNotFound: () -> Unit
) {

    val transportUiState by transportModel.transportUiState.collectAsState()
    val transportEmpire by transportModel.modifiedEmpire.collectAsState()

    LaunchedEffect(toShow) {
        if (toShow) {
            transportModel.launchTransportDialog(empire, planet)
        }
    }

    if (toShow) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = modifier
                    .wrapContentSize()
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.transport),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Is long term transport?")
                        Checkbox(
                            checked = transportUiState.transport.isLongTime,
                            onCheckedChange = { transportModel.updateIsLongTermChosen(it) }
                        )
                    }
                    Row{
                        Text(
                            text = "Cost: ${transportUiState.transport.cost.toInt()} "
                        )
                        Image(
                            painter = painterResource(R.drawable.organic_sediments_icon),
                            contentDescription = "Cost",
                            modifier = modifier.size(24.dp)
                        )
                    }
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            transportModel.getPlanet(
                                planetId = planet.id,
                                empire = transportEmpire
                            )?.let { testPlanet ->
                                PlanetCard(
                                    modifier = modifier,
                                    planet = testPlanet,
                                    testPlanet = testPlanet,
                                    isForTransport = true,
                                    onPlanetClick = { }
                                )
                            }?: onPlanetNotFound()
                            if (transportUiState.isTransportReady){
                                listOf(
                                    Resource.ORGANIC_SEDIMENTS,
                                    Resource.METAL,
                                    Resource.ROCKET_MATERIALS
                                ).forEach { resource ->
                                    ResourceRowForPlanet1(
                                        transportUiState = transportUiState,
                                        transportModel = transportModel,
                                        resource = resource,
                                        planet = planet
                                    )
                                }
                            }
                        }
                        if (!transportUiState.isTransportReady) {
                            Box(
                                modifier = modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                PlanetList(
                                    modifier = modifier,
                                    isForTransport = true,
                                    empire = empire,
                                    testEmpire = transportEmpire,
                                    excludedPlanet = planet,
                                    onPlanetClick = {
                                        transportModel.updateChosen2Planet(
                                            planet = it,
                                            onPlanetNotFound = { onPlanetNotFound() }
                                        )
                                    }
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box {
                                    transportUiState.transport.planet2Id?.let{ planetId ->
                                            transportModel.getPlanet(
                                                planetId = planetId,
                                                empire = transportEmpire
                                            )?.let { testPlanet2 ->
                                                PlanetCard(
                                                    modifier = modifier.align(Alignment.CenterStart),
                                                    planet = testPlanet2,
                                                    testPlanet = testPlanet2,
                                                    isForTransport = true,
                                                    onPlanetClick = { }
                                                )
                                            }?: onPlanetNotFound()
                                        }?: onPlanetNotFound()

                                    IconButton(
                                        modifier = modifier.align(Alignment.TopEnd),
                                        onClick = {
                                            transportModel.updateChosen2Planet(
                                                planet = null,
                                                onPlanetNotFound = {}
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            tint = Color.Black,
                                            contentDescription = "Close button"
                                        )
                                    }
                                }
                                transportUiState.transport.planet2Id?.let{ planet2Id ->
                                    transportModel.getPlanet(
                                        planetId = planet2Id,
                                        empire = empire
                                    )?.let{ planet2 ->
                                        if (transportModel.canPlanet2Export(planet2)) {
                                            listOf(
                                                Resource.ORGANIC_SEDIMENTS,
                                                Resource.METAL,
                                                Resource.ROCKET_MATERIALS
                                            ).forEach {
                                                ResourceRowForPlanet2(
                                                    transportUiState = transportUiState,
                                                    resource = it,
                                                    empire = empire,
                                                    transportModel = transportModel,
                                                    onPlanetNotFound = onPlanetNotFound
                                                )
                                            }
                                        }
                                    }?: onPlanetNotFound()
                                }?: onPlanetNotFound()
                            }
                        }
                    }
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { closeDialog() }
                        ) { Text("Close") }
                        Button(
                            onClick = {
                                addTransportAction(transportUiState.transport)
                                closeDialog()
                            },
                            enabled = transportUiState.isTransportReady
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceRowForPlanet1(
    transportUiState: TransportUiState,
    transportModel: TransportViewModel,
    resource: Resource,
    planet: Planet
) {
    val addIcon = if(transportModel.isAddButtonEnabled(planet, resource, true)) painterResource(R.drawable.add) else painterResource(R.drawable.add_disable)
    val removeIcon = if(transportModel.isRemoveButtonEnabled(planet, resource, true)) painterResource(R.drawable.remove) else painterResource(R.drawable.remove_disable)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(4.dp)
                )
                .width(24.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.Center),
                text = (transportUiState.transport.exportFromPlanet1[resource] ?: 0).toString()
            )
        }
        Image(
            painter = painterResource(transportModel.getResourceIcon(resource)),
            contentDescription = "Resource icon $resource",
            modifier = Modifier
                .padding(start = 12.dp)
                .size(24.dp)
        )
        IconButton(
            onClick = { transportModel.addResource(resource, true) },
            enabled = transportModel.isAddButtonEnabled(planet, resource, true)
        ) {
            Icon(
                painter = addIcon,
                contentDescription = "Add",
                tint = Color.Unspecified
            )
        }
        IconButton(
            onClick = { transportModel.removeResource(resource, true) },
            enabled = transportModel.isRemoveButtonEnabled(planet, resource, true)
        ) {
            Icon(
                painter = removeIcon,
                contentDescription = "Clear",
                tint = Color.Unspecified
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow",
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun ResourceRowForPlanet2(
    transportUiState: TransportUiState,
    transportModel: TransportViewModel,
    resource: Resource,
    empire: Empire,
    onPlanetNotFound: () -> Unit
) {
    val planet = transportModel.getPlanet(
        planetId = transportUiState.transport.planet2Id!!,
        empire = empire
    )
    planet?.let{ planet ->
        val removeIcon = if(transportModel.isRemoveButtonEnabled(planet, resource, false)) painterResource(R.drawable.remove) else painterResource(R.drawable.remove_disable)
        val addIcon = if(transportModel.isAddButtonEnabled(planet, resource, false)) painterResource(R.drawable.add) else painterResource(R.drawable.add_disable)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Arrow",
                tint = Color.Unspecified
            )
            IconButton(
                onClick = { transportModel.removeResource(resource, false) },
                enabled = transportModel.isRemoveButtonEnabled(planet, resource, false)
            ) {
                Icon(
                    painter = removeIcon,
                    contentDescription = "Clear",
                    tint = Color.Unspecified
                )
            }
            IconButton(
                onClick = { transportModel.addResource(resource, false) },
                enabled = transportModel.isAddButtonEnabled(planet, resource, false)
            ) {
                Icon(
                    painter = addIcon,
                    contentDescription = "Add",
                    tint = Color.Unspecified
                )
            }
            Image(
                painter = painterResource(transportModel.getResourceIcon(resource)),
                contentDescription = "Resource icon $resource",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .width(24.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = (transportUiState.transport.exportFromPlanet2[resource]
                        ?: 0).toString()
                )
            }
        }
    } ?: onPlanetNotFound()
}