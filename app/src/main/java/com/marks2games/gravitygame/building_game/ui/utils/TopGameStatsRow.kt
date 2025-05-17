package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel

@Composable
fun TopGameStatsRow(
    modifier: Modifier = Modifier,
    empire: Empire,
    testEmpire: Empire,
    empireModel: EmpireViewModel,
    empireUiState: EmpireUiState,
    toResearchScreen: () -> Unit
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
                    empireUiState = empireUiState,
                    empireModel = empireModel
                )
                Spacer(modifier = modifier.width(8.dp))
                ActionMenu(
                    modifier = modifier,
                    empireUiState = empireUiState,
                    empireModel = empireModel,
                    empire = empire,
                )
                Spacer(modifier = modifier.width(8.dp))
                TransportMenu(
                    modifier = modifier,
                    empireUiState = empireUiState,
                    empire = empire,
                    empireModel = empireModel
                )
            }
        }
        Button(
            onClick = { toResearchScreen() }
        ) {
            StatText(
                label = stringResource(R.string.research),
                value = empire.research.toString(),
                income = testEmpire.empireResourcesPossibleIncome.resources[Resource.RESEARCH] ?: 0,
                isBordered = true,
                border = empireModel.getTechnologyPrice()
            )
        }
        Button(
            onClick = { }
        ) {
            StatText(
                label = stringResource(R.string.credits),
                value = empire.credits.toString(),
                income = testEmpire.empireResourcesPossibleIncome.resources[Resource.CREDITS] ?: 0,
                isBordered = false
            )
        }
        Card{
            ResourceCard(
                modifier = modifier.padding(8.dp),
                resourceCount = empire.expeditions,
                icon = R.drawable.cruiser,
                isStoredResource = true,
                possibleIncome = testEmpire.empireResourcesPossibleIncome.resources[Resource.EXPEDITIONS] ?: 0,
                isBordered = true,
                border = empire.borderForNewPlanet
            )
        }
    }
}