package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel

@Composable
fun TransportScreen(
    planets: List<Planet>,
    transportModel: TransportViewModel,
    updatePlanets: (List<Planet>) -> Unit
){

    val transportUiState by transportModel.transportUiState.collectAsState()

    //OkButton
    Button(
        onClick = { transportModel.transport(planets, updatePlanets) }
    ) {
        Text(text = "Transport")
    }

}