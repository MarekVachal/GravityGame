package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.core.data.model.MapNode
import com.marks2games.gravitygame.ui.viewModel.MapViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun <T : MapNode> MapScreen(
    modifier: Modifier,
    mapModel: MapViewModel<T>,
    nodeInfoContent: @Composable (T) -> Unit,
    onNodeClicked: (T) -> Unit,
    onLongNodeClicked: (T) -> Unit,
    backgroundImageId: Int?,
    isMapRotating: Boolean,
    isBackgroundRotating: Boolean
) {
    val state by mapModel.mapUiState.collectAsState()
    val nodeMap = state.nodes.associateBy { it.id }

    val drawWidth = state.mapSize.width * state.scale
    val drawHeight = state.mapSize.height * state.scale

    val drawNodes: @Composable (T, Offset) -> Unit = { node, position ->
        val x = position.x - state.buttonSize
        val y = position.y - state.buttonSize
        Box(
            modifier = Modifier
                .size(
                    state.buttonSize.dp,
                    (state.buttonSize * mapModel.mapConfig.buttonShapeCoefficientY).dp
                )
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .border(2.dp, node.buttonColor, RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = { onNodeClicked(node) },
                    onLongClick = { onLongNodeClicked(node) }
                )
        ) {
            nodeInfoContent(node)
        }
    }

    Box(
        modifier = modifier.clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (isBackgroundRotating) {
                        Modifier
                            .scale(state.scale)
                            .offset {
                                IntOffset(
                                    state.offset.x.roundToInt(),
                                    state.offset.y.roundToInt()
                                )
                            }
                    } else {
                        Modifier
                    }
                )
        ) {
            if(backgroundImageId != null){
                Image(
                    painter = painterResource(id = backgroundImageId),
                    contentDescription = "Battle map background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .onSizeChanged { mapModel.updateScreenSize(it) }
                .zoomable(
                    getScale = { state.scale },
                    getOffset = { state.offset },
                    minScale = state.minScale,
                    onScaleChange = mapModel::updateScale,
                    onOffsetChange = mapModel::updateOffset
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (isMapRotating) {
                    // For toroidal map, draw connections considering all possible wrapped positions
                    val drawnConnections = mutableSetOf<Pair<String, String>>()
                    val nodePositionsCache = mutableMapOf<String, List<Offset>>()
                    state.nodes.forEach { node ->
                        nodePositionsCache[node.id] = mapModel.getToroidalPositions(node)
                    }
                    state.nodes.forEach { node ->
                        val fromPositions = nodePositionsCache[node.id] ?: return@forEach
                        node.connections.forEach { targetId ->
                            if (drawnConnections.contains(targetId to node.id)) return@forEach
                            drawnConnections.add(node.id to targetId)
                            val toPositions = nodePositionsCache[targetId] ?: return@forEach
                            fromPositions.forEach { from ->
                                toPositions.forEach { to ->
                                    val dx = abs(from.x - to.x)
                                    val dy = abs(from.y - to.y)
                                    if (dx <= drawWidth / 2 && dy <= drawHeight / 2) {
                                        drawLine(
                                            color = Color.Gray,
                                            start = from,
                                            end = to,
                                            strokeWidth = 4f
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Normal connections
                    state.nodes.forEach { node ->
                        node.connections.forEach { targetId ->
                            nodeMap[targetId]?.let { target ->
                                val start = mapModel.getNodePosition(node)
                                val end = mapModel.getNodePosition(target)
                                drawLine(
                                    color = Color.Gray,
                                    start = start,
                                    end = end,
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }
                }
            }



            state.nodes.forEach { node ->
                if (isMapRotating) {
                    mapModel.getToroidalPositions(node).forEach { position ->
                        drawNodes(node, position)
                    }
                } else {
                    drawNodes(node, mapModel.getNodePosition(node))
                }
            }
        }
    }
}