package com.marks2games.gravitygame.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.Ship
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel

abstract class BattleMap {
    abstract val mapName: Int
    abstract val boxSize: Dp
    abstract val planetSize: Dp
    abstract val explosionSize: Dp
    abstract val flagSize: Dp
    abstract val secondsForTurn: Int
    abstract val shipLimitOnMap: Int
    abstract val shipLimitOnPosition: Int
    abstract val locationList: List<Location>
    abstract val player1Base: Int
    abstract val player2Base: Int

    @Composable
    abstract fun MapLayout(
        modifier: Modifier,
        battleModel: BattleViewModel,
        record: List<Map<Ship, Int>>,
        enemyRecord: List<Ship>,
        locationList: List<Location>
    )
}

enum class BattleMapEnum{
    TINY
}

fun String.toBattleMap(): BattleMapEnum? {
    return try {
        BattleMapEnum.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}