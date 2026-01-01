package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.geometry.Offset
import com.marks2games.gravitygame.core.data.model.MapNode
import com.marks2games.gravitygame.core.data.model.MapUiState
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

class GetToroidalPositionsUseCase <T : MapNode> @Inject constructor() {
    operator fun invoke(node: T, state: MapUiState<T>): List<Offset> {
        val drawWidth = state.mapSize.width * state.scale
        val drawHeight = state.mapSize.height * state.scale

        val screenWidth = state.screenSize.width.toFloat()
        val screenHeight = state.screenSize.height.toFloat()

        val mapWidthWithGap = drawWidth + state.buttonSize * 3f
        val mapHeightWithGap = drawHeight + state.buttonSize * 3f

        val baseX = (node.posX * drawWidth) + state.offset.x
        val baseY = (node.posY * drawHeight) + state.offset.y

        val positions = mutableListOf<Offset>()

        // Kolik opakování mapy se vejde nalevo a napravo od baseX tak, aby to pokrylo obrazovku?
        val minRepeatX = floor((-baseX) / mapWidthWithGap).toInt() - 1
        val maxRepeatX = ceil((screenWidth - baseX) / drawWidth).toInt() + 1

        val minRepeatY = floor((-baseY) / mapHeightWithGap).toInt() - 1
        val maxRepeatY = ceil((screenHeight - baseY) / mapHeightWithGap).toInt() + 1

        for (i in minRepeatX..maxRepeatX) {
            for (j in minRepeatY..maxRepeatY) {
                val x = baseX + i * mapWidthWithGap
                val y = baseY + j * mapHeightWithGap
                positions.add(Offset(x, y))
            }
        }

        return positions
    }
}