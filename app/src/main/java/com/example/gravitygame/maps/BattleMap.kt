package com.example.gravitygame.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.gravitygame.models.Location
import com.example.gravitygame.viewModels.BattleViewModel

abstract class BattleMap {
    abstract val mapName: Int
    abstract val boxSize: Dp
    abstract val planetSize: Dp
    abstract val flagSize: Dp
    abstract val secondsForTurn: Int
    abstract val shipLimitOnMap: Int
    abstract val shipLimitOnPosition: Int
    abstract val locationList: List<Location>

    @Composable
    abstract fun MapLayout(
        modifier: Modifier,
        battleModel: BattleViewModel,
        locationList: List<Location>
    )
}