package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import kotlin.math.floor


@Composable
fun ResourceList(
    modifier: Modifier = Modifier,
    planet: Planet?,
    testPlanet: Planet?,
    showResourceInfoDialog: (Boolean) -> Unit,
    changeResource: (Resource) -> Unit

) {
    Card(
        modifier = modifier.wrapContentSize()
    ) {
        Column(modifier = modifier.padding(4.dp)) {
            Text(text = planet?.name?: stringResource(R.string.unknown_planet))

            ResourceCard(
                modifier = modifier.
                    clickable {
                        changeResource(Resource.PROGRESS)
                        showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = planet?.progress?: 0,
                icon = R.drawable.progress_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.PROGRESS]
                    ?: 0,
                isBordered = true,
                border = testPlanet?.planetGrowthBorder?: 0
            )

            ResourceCard(
                modifier = modifier.
                        clickable {
                    changeResource(Resource.BIOMASS)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = floor(planet?.biomass?: 0f).toInt(),
                icon = R.drawable.biomass_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.BIOMASS]
                    ?: 0,
                isBordered = false
            )

            ResourceCard(
                modifier = modifier.
                        clickable {
                    changeResource(Resource.ORGANIC_SEDIMENTS)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = floor(planet?.organicSediment?: 0f).toInt(),
                icon = R.drawable.organic_sediments_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.ORGANIC_SEDIMENTS]
                    ?: 0,
                isBordered = true,
                border = floor(planet?.planetOrganicSediments?: 0f).toInt()
            )

            ResourceCard(
                modifier = modifier.
                clickable {
                    changeResource(Resource.ARMY)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = planet?.army?: 0,
                icon = R.drawable.warship_material_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.ARMY]
                    ?: 0,
                isBordered = false
            )

            ResourceCard(
                modifier = modifier.
                clickable {
                    changeResource(Resource.METAL)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = planet?.metal?: 0,
                icon = R.drawable.metal_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.METAL]
                    ?: 0,
                isBordered = true,
                border = planet?.planetMetal?: 0
            )

            ResourceCard(
                modifier = modifier.
                clickable {
                    changeResource(Resource.ROCKET_MATERIALS)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = planet?.rocketMaterials?: 0,
                icon = R.drawable.rocket_material_icon,
                isStoredResource = true,
                possibleIncome = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.ROCKET_MATERIALS]
                    ?: 0,
                isBordered = false
            )

            ResourceCard(
                modifier = modifier.
                clickable {
                    changeResource(Resource.INFRASTRUCTURE)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.INFRASTRUCTURE]?: 0,
                icon = R.drawable.infrastructure_icon,
                isStoredResource = false,
                isBordered = false
            )

            ResourceCard(
                modifier = modifier.
                clickable {
                    changeResource(Resource.DEVELOPMENT)
                    showResourceInfoDialog(true)
                },
                iconSize = 24.dp,
                resourceCount = testPlanet?.planetResourcesPossibleIncome?.resources[Resource.DEVELOPMENT]?: 0,
                icon = R.drawable.development_icon,
                isStoredResource = false,
                isBordered = false
            )
        }
    }
}