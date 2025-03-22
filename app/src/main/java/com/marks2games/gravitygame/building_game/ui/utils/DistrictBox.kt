package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.marks2games.gravitygame.battle_game.data.planetmap.PlanetMap
import com.marks2games.gravitygame.building_game.data.model.District

@Composable
fun DistrictBox(
    modifier: Modifier = Modifier,
    mapType: PlanetMap,
    district: District
){
    Box(
       modifier = modifier
           .size(mapType.size)
    )
}