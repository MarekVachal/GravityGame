package com.marks2games.gravitygame.building_game.ui.utils

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import kotlin.math.floor

@Composable
fun PlanetList(
    modifier: Modifier,
    empire: Empire,
    testEmpire: Empire,
    excludedPlanet: Planet? = null,
    isForTransport: Boolean = false,
    onPlanetClick: (Planet?) -> Unit
) {
    Log.d("Empire", "$empire")
    var planets = empire.planets
    var testPlanets = testEmpire.planets
    if (excludedPlanet != null) {
        planets = planets.filterNot { it.id == excludedPlanet.id }
        testPlanets = testPlanets.filterNot { it.id == excludedPlanet.id }
    }
    Log.d("Planets", "$planets")
    Log.d("TestPlanets", "$testPlanets")
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(planets.size) {
            PlanetCard(
                planet = planets[it],
                testPlanet = testPlanets[it],
                isForTransport = isForTransport,
                onPlanetClick = {onPlanetClick(planets[it])}
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanetCard(
    modifier: Modifier = Modifier,
    planet: Planet,
    testPlanet: Planet,
    isForTransport: Boolean = false,
    onPlanetClick:() -> Unit
) {
    val modifierForPlanetName = if(!isForTransport){
        modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth()
    } else {
        modifier
            .padding(bottom = 4.dp)
    }
    Card(
        modifier = modifier
            .clickable { onPlanetClick() }
    ) {
        Column(modifier = modifier.padding(4.dp)) {
            Row(
                modifier = modifierForPlanetName,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = planet.name)
                if(!isForTransport){
                    ResourceCard(
                        modifier = modifier,
                        resourceCount = planet.progress,
                        icon = R.drawable.progress_icon,
                        isStoredResource = true,
                        possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.PROGRESS]
                            ?: 0,
                        isBordered = true,
                        border = testPlanet.planetGrowthBorder
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if(!isForTransport){
                    ResourceCard(
                        modifier = modifier,
                        resourceCount = floor(planet.biomass).toInt(),
                        icon = R.drawable.biomass_icon,
                        isStoredResource = true,
                        possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.BIOMASS]
                            ?: 0,
                        isBordered = false
                    )
                }

                ResourceCard(
                    modifier = modifier,
                    resourceCount = floor(planet.organicSediment).toInt(),
                    icon = R.drawable.organic_sediments_icon,
                    isStoredResource = true,
                    possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.ORGANIC_SEDIMENTS]
                        ?: 0,
                    isBordered = true,
                    border = floor(planet.planetOrganicSediments).toInt()
                )
                if(!isForTransport){
                    ResourceCard(
                        modifier = modifier,
                        resourceCount = planet.army,
                        icon = R.drawable.army_icon,
                        isStoredResource = true,
                        possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.ARMY]
                            ?: 0,
                        isBordered = false
                    )
                }

                ResourceCard(
                    modifier = modifier,
                    resourceCount = planet.metal,
                    icon = R.drawable.metal_icon,
                    isStoredResource = true,
                    possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.METAL]
                        ?: 0,
                    isBordered = true,
                    border = planet.planetMetal
                )
                ResourceCard(
                    modifier = modifier,
                    resourceCount = planet.rocketMaterials,
                    icon = R.drawable.rocket_material_icon,
                    isStoredResource = true,
                    possibleIncome = testPlanet.planetResourcesPossibleIncome.resources[Resource.ROCKET_MATERIALS]
                        ?: 0,
                    isBordered = false
                )
                if(!isForTransport){
                    Box{
                        Row{
                            ResourceCard(
                                modifier = modifier,
                                resourceCount = testPlanet.planetResourcesPossibleIncome.resources[Resource.INFRASTRUCTURE]?: 0,
                                icon = R.drawable.infrastructure_icon,
                                isStoredResource = false,
                                isBordered = false
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ResourceCard(
                                modifier = modifier,
                                resourceCount = testPlanet.planetResourcesPossibleIncome.resources[Resource.DEVELOPMENT]?: 0,
                                icon = R.drawable.development_icon,
                                isStoredResource = false,
                                isBordered = false
                            )
                        }
                    }
                }
            }
        }
    }
}