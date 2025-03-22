package com.marks2games.gravitygame.building_game.ui.screen

import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.ui.utils.StatText
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import kotlin.math.floor

@Composable
fun EmpireOverview(
    modifier: Modifier = Modifier,
    empireModel: EmpireViewModel,
    onBackButtonClicked: () -> Unit
) {
    val empire by empireModel.empire.collectAsState()
    val empireUiState by empireModel.empireUiState.collectAsState()

    LaunchedEffect(Unit) {
        empireModel.getEmpireFromDatabase()
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
        TopGameStatsRow(
            modifier = modifier.fillMaxWidth(),
            empireUiState = empire
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier.align(Alignment.TopStart)
            ) {
                ErrorMenu(
                    modifier = modifier,
                    empireUiState = empireUiState,
                    empireModel = empireModel
                )
                Spacer(modifier = modifier.height(8.dp))
                ActionMenu(
                    modifier = modifier,
                    empireUiState = empireUiState,
                    empireModel = empireModel
                )
            }
            ExtendedFloatingActionButton(
                modifier = modifier.align(alignment = Alignment.BottomEnd),
                onClick = { empireModel.newTurn() }
            ) {
                Text(text = stringResource(R.string.newTurn))
            }
            Box(
                modifier = modifier.align(Alignment.TopEnd)
            ){
                PlanetList(
                    modifier = modifier,
                    empireUiState = empire
                )
            }
            IconButton(
                modifier = modifier.align(alignment = Alignment.BottomStart),
                onClick = { onBackButtonClicked() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.undo),
                    contentDescription = "Back button"
                )
            }
        }
    }
}


@Composable
private fun TopGameStatsRow(
    modifier: Modifier = Modifier,
    empireUiState: Empire
) {
    Row(
        modifier = modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { TODO() }
        ) {
            StatText(
                label = stringResource(R.string.research),
                value = empireUiState.research.toString()
            )
        }
        Button(
            onClick = { TODO() }
        ) {
            StatText(
                label = stringResource(R.string.credits),
                value = empireUiState.credits.toString()
            )
        }
        Button(
            onClick = { TODO() }
        ) {
            StatText(
                label = stringResource(R.string.expeditions),
                value = empireUiState.expeditions.toString()
            )
        }
    }
}

@Composable
private fun PlanetList(
    modifier: Modifier = Modifier,
    empireUiState: Empire
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyColumn(
        modifier = modifier.width(screenWidth * 0.5f),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(empireUiState.planets.size) {
            PlanetCard(planet = empireUiState.planets[it - 0])
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanetCard(
    modifier: Modifier = Modifier,
    planet: Planet
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { TODO() }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = planet.name)
                Button(
                    onClick = { TODO() }
                ) {
                    Text(
                        text = ("Details")
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceCard(
                    resource = floor(planet.biomass).toInt(),
                    icon = R.drawable.biomass_icon,
                    isStoredResource = true
                )
                ResourceCard(
                    resource = floor(planet.organicSediment).toInt(),
                    icon = R.drawable.organicsediments,
                    isStoredResource = true
                )
                ResourceCard(
                    resource = planet.metal,
                    icon = R.drawable.metal_icon,
                    isStoredResource = true
                )
                ResourceCard(
                    resource = planet.rocketMaterials,
                    icon = R.drawable.rocket_material_icon,
                    isStoredResource = true
                )
                ResourceCard(
                    resource = planet.infrastructure,
                    icon = R.drawable.infrastructure_icon,
                    isStoredResource = false
                )
                ResourceCard(
                    resource = planet.influence,
                    icon = R.drawable.influence_cion,
                    isStoredResource = false
                )
                ResourceCard(
                    resource = planet.development,
                    icon = R.drawable.development_icon,
                    isStoredResource = false
                )
                ResourceCard(
                    resource = planet.progress,
                    icon = R.drawable.progress_icon,
                    isStoredResource = true
                )
                ResourceCard(
                    resource = planet.army,
                    icon = R.drawable.army_icon,
                    isStoredResource = true
                )
            }
        }
    }
}

@Composable
private fun ResourceCard(
    modifier: Modifier = Modifier,
    resource: Int,
    icon: Int,
    isStoredResource: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = "Resource icon in Resource card"
        )
        Text(if (isStoredResource) " $resource/+1" else " $resource")
    }

}

@Composable
private fun ErrorMenu(
    modifier: Modifier = Modifier,
    empireUiState: EmpireUiState,
    empireModel: EmpireViewModel
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = modifier.align(Alignment.TopEnd),
            text = "${empireUiState.errors.size}"
        )
        IconButton(
            onClick = { TODO() },
            modifier = modifier.align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu icon",
                tint = Color.Unspecified
            )
        }

        DropdownMenu(
            expanded = empireUiState.isErrorsShown,
            onDismissRequest = { empireModel.updateErrorsShown(false) },
            properties = PopupProperties(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            TODO("List of Errors")
        }
    }
}

@Composable
private fun ActionMenu(
    modifier: Modifier = Modifier,
    empireUiState: EmpireUiState,
    empireModel: EmpireViewModel
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = modifier.align(Alignment.TopEnd),
            text = "${empireUiState.actions.size}"
        )
        IconButton(
            onClick = { TODO() },
            modifier = modifier.align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu icon",
                tint = Color.Unspecified
            )
        }

        DropdownMenu(
            expanded = empireUiState.isActionsShown,
            onDismissRequest = { empireModel.updateActionsShown(false) },
            properties = PopupProperties(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            TODO("List of Actions")
        }
    }
}