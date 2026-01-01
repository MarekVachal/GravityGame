package com.marks2games.gravitygame.ui.viewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.core.data.model.MapNode
import com.marks2games.gravitygame.core.data.model.MapUiState
import com.marks2games.gravitygame.core.domain.model.MapConfig
import com.marks2games.gravitygame.core.domain.usecases.genericMap.GetNodePositionUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.GetToroidalPositionsUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateButtonSizeUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateMapSizeUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateMinScaleUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateOffsetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class MapViewModel<T : MapNode> (
    protected open val updateOffset: UpdateOffsetUseCase,
    protected open val updateButtonSize: UpdateButtonSizeUseCase,
    protected open val updateMinScale: UpdateMinScaleUseCase,
    protected open val updateMapSize: UpdateMapSizeUseCase,
    open val mapConfig: MapConfig,
    private val getToroidalPositions: GetToroidalPositionsUseCase<T>,
    private val getNodePosition: GetNodePositionUseCase<T>
) : ViewModel() {

    private val _mapUiState = MutableStateFlow<MapUiState<T>>(MapUiState())
    val mapUiState = _mapUiState.asStateFlow()

    protected fun updateState(transform: MapUiState<T>.() -> MapUiState<T>) {
        _mapUiState.update(transform)
    }

    fun getToroidalPositions(node: T): List<Offset> {
        return getToroidalPositions.invoke(node, mapUiState.value)
    }

    fun getNodePosition(node: T): Offset {
        return getNodePosition.invoke(node, mapUiState.value)
    }

    fun updateNodes(nodes: List<T>){
        updateState { copy(nodes = nodes) }
    }

    fun updateScale(newScale: Float) {
        val clampedScale = newScale.coerceIn(mapUiState.value.minScale, 2.5f)
        updateState{ copy(
                scale = clampedScale,
                buttonSize = updateButtonSize.invoke(
                    clampedScale,
                    mapConfig.defaultButtonSize
                )
            )
        }
    }

    fun updateOffset(newOffset: Offset) {
        if (mapUiState.value.screenSize != IntSize.Zero) {
            _mapUiState.update { state ->
                state.copy(
                    offset = updateOffset.invoke(
                        screenSize = mapUiState.value.screenSize,
                        newOffset = newOffset,
                        scale = mapUiState.value.scale,
                        mapSize = mapUiState.value.mapSize,
                        nodeSize = mapUiState.value.buttonSize,
                        isMapRotating = mapConfig.isMapRotating
                    )
                )
            }
        }
    }

    fun updateScreenSize(
        newScreenSize: IntSize
    ) {
        val mapSize = updateMapSize.invoke(
            newScreenSize = newScreenSize,
            spaceBetweenNodes = mapConfig.spaceBetweenNodes
        )

        val minScale = updateMinScale.invoke(
            newScreenSize = newScreenSize,
            mapSize = mapSize,
            buttonSize = mapConfig.defaultButtonSize,
            nodesPadding = mapConfig.nodesPadding
        )
        val scale = minScale
        val buttonSize = updateButtonSize.invoke(scale, mapConfig.defaultButtonSize)

        val offset = updateOffset.invoke(
            screenSize = newScreenSize,
            newOffset = Offset.Zero,
            scale = scale,
            mapSize = mapSize,
            nodeSize = buttonSize,
            isMapRotating = mapConfig.isMapRotating
        )

        _mapUiState.update { state ->
            state.copy(
                screenSize = newScreenSize,
                mapSize = mapSize,
                minScale = minScale,
                scale = scale,
                buttonSize = buttonSize,
                offset = offset
            )
        }
    }
}