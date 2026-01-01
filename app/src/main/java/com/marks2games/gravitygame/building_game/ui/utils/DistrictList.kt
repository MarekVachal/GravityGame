package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DistrictList(
    modifier: Modifier,
    planetId: Int,
    empire: Empire,
    planetModel: PlanetViewModel,
    empireModel: EmpireViewModel,
    screenWidth: Dp
) {
    val planet = empire.planets.find{it.id == planetId}

    if(planet != null){
        LazyColumn(
            modifier = modifier
                .width(screenWidth * 0.4f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Card {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = planet.name,
                            modifier = modifier
                        )
                        Image(
                            Icons.Default.Close,
                            contentDescription = "Close image",
                            modifier = modifier
                                .clickable { planetModel.updateShowDistrictList(false, planet) },
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
            items(empire.planets[planetId].districts.size) {
                DistrictCard(
                    modifier = modifier,
                    district = empire.planets[planetId].districts[it],
                    empireModel = empireModel,
                    planetId = planetId
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .width(screenWidth * 0.5f)
        ) { Text("Cannot find the planet.") }
    }

}

@Composable
private fun DistrictCard(
    modifier: Modifier,
    district: District,
    empireModel: EmpireViewModel,
    planetId: Int
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                empireModel.openDistrictDetails(
                    planetId = planetId,
                    district = district
                )
            }
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(district.nameId))
            }
        }
    }
}

 */