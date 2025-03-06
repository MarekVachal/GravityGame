package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.ui.utils.StatText
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel

@Composable
fun PlanetScreen(
    modifier: Modifier = Modifier,
    planetModel: PlanetViewModel,
    empire: Empire,
    makeTradepower: (Int, List<Planet>) -> Unit,
    increaseExpeditions: (Float, List<Planet>) -> Unit,
    createArmyUnit: (Int, List<Planet>) -> Unit,
    planetId: Int?
){
    val planetUiState by planetModel.planet.collectAsState()

    LaunchedEffect(Unit) {
        planetModel.loadPlanet(empire.planets, planetId)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        TopBarStats(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            planet = planetUiState
        )
    }

}

@Composable
private fun TopBarStats(modifier: Modifier, planet: Planet){
    Row(
        modifier = modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatText(label = stringResource(R.string.biomass), value = planet.biomass.toString())
        StatText(
            label = stringResource(R.string.biomass),
            value = planet.organicSediment.toString()
        )
        StatText(label = stringResource(R.string.biomass), value = planet.metal.toString())
        StatText(
            label = stringResource(R.string.biomass),
            value = planet.rocketMaterials.toString()
        )
        StatText(label = stringResource(R.string.biomass), value = planet.infrastructure.toString())
        StatText(label = stringResource(R.string.biomass), value = planet.influence.toString())
        StatText(label = stringResource(R.string.biomass), value = planet.progress.toString())
        StatText(label = stringResource(R.string.biomass), value = planet.level.toString())
        StatText(label = stringResource(R.string.biomass), value = planet.development.toString())
    }
}