package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.battle_game.ui.utils.timer.CoroutineTimer
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.ui.utils.StatText
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.core.ui.utils.TimerCard
import kotlin.math.floor

@Composable
fun EmpireOverview(
    modifier: Modifier = Modifier,
    empireModel: EmpireViewModel,
    timerModel: TimerViewModel,
    onPlanetClick: (Int) -> Unit
) {
    val empireUiState by empireModel.empire.collectAsState()

    LaunchedEffect(Unit) {
        empireModel.getEmpireFromDatabase()
        timerModel.makeTimer(
            CoroutineTimer(
                timerModel = timerModel,
                onFinishTimer = {
                    empireModel.saveTurn()
                    timerModel.restartTimer(empireModel.getTimeUnitMillis())
                },
                secondsForTurn = empireModel.getTimeToNextUpdate()
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        TopGameStatsRow(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            empireUiState = empireUiState,
            timerModel = timerModel,
        )
        ExtendedFloatingActionButton(
            modifier = modifier.align(alignment = Alignment.BottomEnd),
            onClick = { empireModel.callNewTurn() }
        ) {
            Text(text = stringResource(R.string.newTurn))
        }
        PlanetList(
            modifier = modifier
                .align(Alignment.CenterEnd),
            empireUiState = empireUiState,
            onPlanetClick = onPlanetClick
        )
    }
}

@Composable
private fun TopGameStatsRow(
    modifier: Modifier = Modifier,
    empireUiState: Empire,
    timerModel: TimerViewModel
) {
    Row(
        modifier = modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatText(label = stringResource(R.string.research), value = empireUiState.research.toString())
        StatText(label = stringResource(R.string.tradepower), value = empireUiState.tradePower.toString())
        StatText(label = stringResource(R.string.expeditions), value = empireUiState.expeditions.toString())
        StatText(label = stringResource(R.string.credits), value = empireUiState.credits.toString())
        StatText(label = stringResource(R.string.fleet), value = empireUiState.army.toString())
        StatText(label = stringResource(R.string.turnsBank), value = empireUiState.savedTurns.toString())
        TimerCard(timerModel)
    }
}

@Composable
private fun PlanetList(
    modifier: Modifier = Modifier,
    empireUiState: Empire,
    onPlanetClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(empireUiState.planets.size) {
            PlanetCard(planet = empireUiState.planets[it], onPlanetClick = onPlanetClick)
        }
    }
}

@Composable
private fun PlanetCard(modifier: Modifier = Modifier,planet: Planet, onPlanetClick: (Int) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Card(
        modifier = Modifier
            .width(screenWidth * 0.25f)
            .clickable { onPlanetClick(planet.id) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = planet.name)
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                ResourceCard(resource = floor(planet.biomass).toInt(), icon = R.drawable.biomass_icon)
                ResourceCard(resource = planet.metal, icon = R.drawable.metal_icon)
            }
        }
    }
}

@Composable
private fun ResourceCard(
    modifier: Modifier = Modifier,
    resource: Int,
    icon: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = "Resource icon in Resource card"
        )
        Text(": $resource")
    }

}