package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.geometry.Offset
import com.marks2games.gravitygame.core.data.model.MapNode
import com.marks2games.gravitygame.core.data.model.MapUiState
import javax.inject.Inject

class GetNodePositionUseCase<T: MapNode> @Inject constructor() {
    operator fun invoke(node: T, state: MapUiState<T>): Offset {
        return Offset(
            x = (node.posX * state.mapSize.width) * state.scale + state.offset.x,
            y = (node.posY * state.mapSize.height) * state.scale + state.offset.y
        )
    }
}