package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.ui.utils.TechnologyInfoDialog
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.ResearchViewModel
import com.marks2games.gravitygame.core.data.model.MapUiState
import com.marks2games.gravitygame.core.data.model.TechnologyNode
import com.marks2games.gravitygame.core.ui.utils.MapScreen

@Composable
fun ResearchScreen(
    empireModel: EmpireViewModel,
    researchModel: ResearchViewModel,
    toEmpireScreenClicked: () -> Unit,
) {
    val state by researchModel.researchUiState.collectAsState()
    val mapState by researchModel.mapUiState.collectAsState()

    LaunchedEffect(Unit) {
        researchModel.updateTechnologiesUiState(empireModel.empire.value.technologies)
        researchModel.createTechnologicalMapNodes(empireModel.empire.value.technologies)
    }

    state.technologyToShowInfo?.let {
        TechnologyInfoDialog(
            technology = researchModel.getTechnologyFromEnum(
                it
            ),
            isShown = state.isTechnologyInfoDialogShown,
            closeDialog = { researchModel.updateIsTechnologyInfoDialogShown(null, false) }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapScreen(
            modifier = Modifier.align(alignment = Alignment.Center),
            mapModel = researchModel,
            nodeInfoContent = {
                ResearchNodeButton(
                    node = researchModel.getTechnologyFromEnum(it.type),
                    mapUiState = mapState
                )
            },
            onNodeClicked = { researchModel.setResearchingTechnology(it.type, empireModel) },
            onLongNodeClicked = { researchModel.updateIsTechnologyInfoDialogShown(it.type, true) },
            backgroundImageId = R.drawable.battle_background
        )

        Button(
            modifier = Modifier.align(alignment = Alignment.BottomEnd),
            onClick = { toEmpireScreenClicked() }
        ) {
            Icon(
                painter = painterResource(R.drawable.undo),
                contentDescription = null
            )
        }

    }
}

@Composable
private fun ResearchNodeButton(
    node: Technology?,
    mapUiState: MapUiState<TechnologyNode>,
) {
    Card(
        modifier = Modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(node?.nameId ?: R.string.unknownTechnology),
                fontSize = (12 * mapUiState.scale.coerceIn(0.7f, 1.5f)).sp,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${node?.cost ?: "X"}",
                fontSize = (10 * mapUiState.scale.coerceIn(0.7f, 1.5f)).sp
            )
        }
    }
}
