package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Resource

@Composable
fun TopGameStatsRow(
    modifier: Modifier = Modifier,
    actionsCount: Int,
    transportsCount: Int,
    empire: Empire?,
    testEmpire: Empire?,
    toResearchScreen: () -> Unit,
    isErrorsListEmpty: Boolean,
    errorsSize: Int,
    onErrorMenuClick: () -> Unit,
    onActionMenuClick: () -> Unit,
    onTransportMenuClick: () -> Unit,
    onPlanetMenuClick: () -> Unit,
    getTechnologyPrice: () -> Int,
    isPlanetMenuPresent: Boolean
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Row {
                ErrorMenu(
                    modifier = modifier,
                    isErrorsListEmpty = isErrorsListEmpty,
                    errorsSize = errorsSize,
                    onErrorMenuClick = onErrorMenuClick
                )
                Spacer(modifier = modifier.width(8.dp))
                ActionMenu(
                    modifier = modifier,
                    actionsCount = actionsCount,
                    onActionMenuClick = onActionMenuClick,
                )
                Spacer(modifier = modifier.width(8.dp))
                TransportMenu(
                    modifier = modifier,
                    transportCount = transportsCount,
                    onTransportMenuClick = onTransportMenuClick,
                )
            }
        }
        Button(
            onClick = { toResearchScreen() }
        ) {
            ResourceCard(
                modifier = modifier.padding(horizontal = 4.dp),
                iconSize = 36.dp,
                resourceCount = empire?.research ?: 0,
                icon = R.drawable.research_icon,
                isStoredResource = true,
                possibleIncome = testEmpire?.empireResourcesPossibleIncome?.resources[Resource.RESEARCH] ?: 0,
                isBordered = true,
                border = getTechnologyPrice()
            )
        }
        Button(
            onClick = { }
        ) {
            ResourceCard(
                modifier = modifier.padding(horizontal = 4.dp),
                iconSize = 36.dp,
                resourceCount = empire?.credits ?: 0,
                icon = R.drawable.money_icon,
                isStoredResource = true,
                possibleIncome = testEmpire?.empireResourcesPossibleIncome?.resources[Resource.CREDITS] ?: 0,
                isBordered = false
            )
        }

        Card{
            ResourceCard(
                modifier = modifier.padding(4.dp),
                iconSize = 36.dp,
                resourceCount = empire?.expeditions ?: 0,
                icon = R.drawable.expedition_icon,
                isStoredResource = true,
                possibleIncome = testEmpire?.empireResourcesPossibleIncome?.resources[Resource.EXPEDITIONS] ?: 0,
                isBordered = true,
                border = empire?.borderForNewPlanet ?: 0
            )
        }
        if(isPlanetMenuPresent){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = Color.White,
                modifier = modifier
                    .padding(16.dp)
                    .clickable(
                        onClick = { onPlanetMenuClick() }
                    )
            )
        }
    }
}