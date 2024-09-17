package com.example.gravitygame.models

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.gravitygame.ui.utils.Players
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    private var connections: List<Int>,
    @Contextual
    val myShipList: SnapshotStateList<Ship> = mutableStateListOf(),
    @Contextual
    val enemyShipList: SnapshotStateList<Ship> = mutableStateListOf(),
    val myAcceptableLost: MutableIntState = mutableIntStateOf(1),
    val enemyAcceptableLost: MutableIntState = mutableIntStateOf(1),
    val owner: MutableState<Players> = mutableStateOf(Players.NONE),
    val wasBattleHere: MutableState<Boolean> = mutableStateOf(false),

) {
    var accessible by mutableStateOf(false)
    val originalMyShipList: MutableMap<ShipType, Int> = mutableMapOf()
    val originalEnemyShipList: MutableMap<ShipType, Int> = mutableMapOf()
    val mapMyLost: MutableMap<ShipType, Int> = mutableMapOf()
    val mapEnemyLost: MutableMap<ShipType, Int> = mutableMapOf()
    var lastBattleResult: String = ""

    fun getConnectionsList(): List<Int>{
        return connections.toList()
    }
}

fun Location.deepCopy(): Location {
    return this.copy(
        enemyShipList = this.enemyShipList.deepCopy(),
        myShipList = this.myShipList.deepCopy(),
        myAcceptableLost = mutableIntStateOf(this.myAcceptableLost.intValue),
        enemyAcceptableLost = mutableIntStateOf(this.enemyAcceptableLost.intValue),
        owner = mutableStateOf(this.owner.value),
        wasBattleHere = mutableStateOf(this.wasBattleHere.value)

    )
}

fun SnapshotStateList<Ship>.deepCopy(): SnapshotStateList<Ship> {
    val copy = mutableStateListOf<Ship>()
    this.forEach { item ->
        copy.add(item.deepCopy())
    }
    return copy
}

