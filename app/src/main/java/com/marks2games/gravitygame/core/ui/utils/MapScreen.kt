package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
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
    backgroundImageId: Int,
    isToroidal: Boolean = false,
    isBackgroundRotating: Boolean = false
) {
    val state by mapModel.mapUiState.collectAsState()
    val nodeMap = state.nodes.associateBy { it.id }

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
            Image(
                painter = painterResource(id = backgroundImageId),
                contentDescription = "Battle map background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
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
                    isToroidal = isToroidal,
                    mapSize = state.mapSize,
                    onScaleChange = mapModel::updateScale,
                    onOffsetChange = mapModel::updateOffset,
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                /*
                state.nodes.flatMap { node ->
                    node.connections.mapNotNull { targetId ->
                        nodeMap[targetId]?.let { target ->
                            val start = Offset(
                                x = (node.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * mapModel.mapConfig.nodeCenterXCorrection,
                                y = (node.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * mapModel.mapConfig.nodeCenterYCorrection
                            )
                            val end = Offset(
                                x = (target.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * mapModel.mapConfig.nodeCenterXCorrection,
                                y = (target.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * mapModel.mapConfig.nodeCenterYCorrection
                            )

                            if (isToroidal) {
                                // For toroidal maps, calculate all possible wrapped positions
                                val wrappedConnections = mutableListOf<Pair<Offset, Offset>>()

                                // Check if we need to wrap horizontally
                                val xDiff = abs(start.x - end.x)
                                val shouldWrapX = xDiff > size.width * 0.5

                                // Check if we need to wrap vertically
                                val yDiff = abs(start.y - end.y)
                                val shouldWrapY = yDiff > size.height * 0.5

                                // Generate all combinations of wrapped positions
                                for (xWrap in -1..1) {
                                    for (yWrap in -1..1) {
                                        // Skip the unwrapped case (0,0) as we'll add it separately
                                        if (xWrap == 0 && yWrap == 0) continue

                                        // Only add wrapped versions if they're needed
                                        if ((xWrap != 0 && !shouldWrapX) || (yWrap != 0 && !shouldWrapY)) continue

                                        val wrappedEnd = Offset(
                                            end.x + xWrap * size.width,
                                            end.y + yWrap * size.height
                                        )
                                        wrappedConnections.add(start to wrappedEnd)
                                    }
                                }

                                // Always include the original connection
                                wrappedConnections.add(start to end)

                                wrappedConnections
                            } else {
                                listOf(start to end)
                            }
                        }
                    }.flatten()
                }.forEach { (start, end) ->
                    drawLine(
                        color = Color.Gray,
                        start = start,
                        end = end,
                        strokeWidth = 4f
                    )
                }
            }

            // Draw nodes (buttons)
            state.nodes.forEach { node ->
                val normalizedX = (node.posX * state.mapSize.width)
                val normalizedY = (node.posY * state.mapSize.height)

                val basePosX = normalizedX * state.scale + state.offset.x
                val basePosY = normalizedY * state.scale + state.offset.y

                // For toroidal maps, we might need to draw the node in multiple positions
                val positions = if (isToroidal) {
                    // Calculate all visible wrapped positions
                    val positions = mutableListOf(Offset(basePosX, basePosY))

                    // Check if we need to wrap horizontally
                    val screenWidth = state.screenSize.width.toFloat()
                    if (basePosX < 0) positions.add(Offset(basePosX + screenWidth, basePosY))
                    if (basePosX > screenWidth) positions.add(
                        Offset(
                            basePosX - screenWidth,
                            basePosY
                        )
                    )

                    // Check if we need to wrap vertically
                    val screenHeight = state.screenSize.height.toFloat()
                    if (basePosY < 0) positions.add(Offset(basePosX, basePosY + screenHeight))
                    if (basePosY > screenHeight) positions.add(
                        Offset(
                            basePosX,
                            basePosY - screenHeight
                        )
                    )

                    positions
                } else {
                    listOf(Offset(basePosX, basePosY))
                }

                positions.forEach { pos ->
                    Box(
                        modifier = Modifier
                            .size(
                                state.buttonSize.dp,
                                (state.buttonSize * mapModel.mapConfig.buttonShapeCoefficientY).dp
                            )
                            .offset { IntOffset(pos.x.roundToInt(), pos.y.roundToInt()) }
                            .border(
                                width = 2.dp,
                                color = node.buttonColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .pointerInput(node, pos) {
                                detectTapGestures(
                                    onTap = { onNodeClicked(node) },
                                    onLongPress = { onLongNodeClicked(node) }
                                )
                            }
                    ) {
                        nodeInfoContent(node)
                    }
                }

                    */
                // Draw connections (lines between nodes)
                val connections = state.nodes.flatMap { node ->
                    node.connections.mapNotNull { targetId ->
                        nodeMap[targetId]?.let { target ->
                            val start = Offset(
                                x = (node.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * mapModel.mapConfig.nodeCenterXCorrection,
                                y = (node.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * mapModel.mapConfig.nodeCenterYCorrection
                            )
                            val end = Offset(
                                x = (target.posX * state.mapSize.width) * state.scale + state.offset.x + state.buttonSize * mapModel.mapConfig.nodeCenterXCorrection,
                                y = (target.posY * state.mapSize.height) * state.scale + state.offset.y + state.buttonSize * mapModel.mapConfig.nodeCenterYCorrection
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
            state.nodes.forEach { node ->
                val normalizedX = (node.posX * state.mapSize.width)
                val normalizedY = (node.posY * state.mapSize.height)

                val posX = normalizedX * state.scale + state.offset.x
                val posY = normalizedY * state.scale + state.offset.y

                Box(
                    modifier = Modifier
                        .size(state.buttonSize.dp, (state.buttonSize * mapModel.mapConfig.buttonShapeCoefficientY).dp)
                        .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                        .border(
                            width = 2.dp,
                            color = node.buttonColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .combinedClickable(
                            onClick = {
                                onNodeClicked(node)
                            },
                            onLongClick = {
                                onLongNodeClicked(node)
                            }
                        )
                ) {
                    nodeInfoContent(node)
                }

            }
        }
    }
}