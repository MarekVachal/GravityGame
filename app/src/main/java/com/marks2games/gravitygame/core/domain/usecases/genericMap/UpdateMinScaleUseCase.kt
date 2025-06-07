package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import javax.inject.Inject

class UpdateMinScaleUseCase @Inject constructor(){
    operator fun invoke(
        newScreenSize: IntSize,
        mapSize: Size,
        buttonSize: Float,
        nodesPadding: Float
    ): Float{
        return maxOf(
            newScreenSize.width / (mapSize.width + buttonSize * nodesPadding),
            newScreenSize.height / (mapSize.height + buttonSize * nodesPadding)
        )
    }
}