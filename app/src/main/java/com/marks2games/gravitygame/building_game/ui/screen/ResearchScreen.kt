package com.marks2games.gravitygame.building_game.ui.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.building_game.ui.utils.TechnologyInfoDialog
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.ResearchViewModel
import com.marks2games.gravitygame.core.ui.utils.zoomable
import kotlin.math.roundToInt

@Composable
fun ResearchScreen(
    empireModel: EmpireViewModel,
    researchModel: ResearchViewModel,
    toEmpireScreenClicked: () -> Unit,
) {
    val state by researchModel.researchUiState.collectAsState()
    val nodeMap = state.technologies.associateBy { it.type }

    LaunchedEffect(Unit) {
        researchModel.updateTechnologiesUiState(empireModel.empire.value.technologies)
        Log.d("EmpireTechnologies", "Empire techs: ${empireModel.empire.value.technologies}")
        Log.d("EmpireTechnologies", "Research techs: ${state.technologies}")
    }

    state.technologyToShowInfo?.let {
        TechnologyInfoDialog(
            technology = researchModel.getTechnologyFromEnum(it, researchModel.researchUiState.value.technologies),
            isShown = state.isTechnologyInfoDialogShown,
            closeDialog = { researchModel.updateIsTechnologyInfoDialogShown(null, false) }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.battle_background),
            contentDescription = "Battle map background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .onSizeChanged { researchModel.updateScreenSize(it) }
                .zoomable(
                    getScale = { state.scale },
                    getOffset = { state.offset },
                    minScale = state.minScale,
                    onScaleChange = researchModel::updateScale,
                    onOffsetChange = researchModel::updateOffset,
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                /*
                drawImage(
                    image = (R.drawable.battle_background)
                )

                 */
                // Draw connections (lines between nodes)
                val connections = state.technologies.flatMap { node ->
                    node.dependencies.mapNotNull { targetId ->
                        nodeMap[targetId]?.let { target ->
                            val start = Offset(
                                x = (node.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * state.nodeCenterXCorrection,
                                y = (node.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * state.nodeCenterYCorrection
                            )
                            val end = Offset(
                                x = (target.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * state.nodeCenterXCorrection,
                                y = (target.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * state.nodeCenterYCorrection
                            )
                            start to end
                        }
                    }
                }

                connections.forEach { (start, end) ->
                    drawLine(
                        color = Color.Gray,
                        start = start,
                        end = end,
                        strokeWidth = 4f
                    )
                }
            }

            // Draw nodes (buttons)
            state.technologies.forEach { node ->
                val normalizedX = (node.posX * state.mapSize.width)
                val normalizedY = (node.posY * state.mapSize.height)

                val posX = normalizedX * state.scale + state.offset.x
                val posY = normalizedY * state.scale + state.offset.y

                Box(
                    modifier = Modifier
                        .size(state.buttonSize.dp, (state.buttonSize * 0.75f).dp)
                        .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                        .border(
                            width = if (node.state == TechnologyResearchState.SELECTED) 6.dp else 2.dp,
                            color = when(node.state){
                                TechnologyResearchState.SELECTED -> Color.Yellow
                                TechnologyResearchState.FINISHED -> Color.White
                                TechnologyResearchState.LOCKED -> Color.Gray
                                TechnologyResearchState.UNLOCKED -> Color.Green
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .combinedClickable(
                            onClick = {
                                researchModel.setResearchingTechnology(
                                    node.type,
                                    empireModel
                                )
                                Log.d("EmpireTechnologies", "Empire techs after click: ${empireModel.empire.value.technologies}")
                                Log.d("EmpireTechnologies", "Research techs after click: ${state.technologies}")
                            },
                            onLongClick = { researchModel.updateIsTechnologyInfoDialogShown(node.type, true) }
                        )
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
                                text = stringResource(node.nameId),
                                fontSize = (12 * state.scale.coerceIn(0.7f, 1.5f)).sp,
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${node.cost}",
                                fontSize = (10 * state.scale.coerceIn(0.7f, 1.5f)).sp
                            )
                        }
                    }
                }
            }

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
}