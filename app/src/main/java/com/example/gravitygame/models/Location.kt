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
    var myAcceptableLost: MutableIntState = mutableIntStateOf(1),
    var enemyAcceptableLost: MutableIntState = mutableIntStateOf(1),
    var owner: MutableState<Players> = mutableStateOf(Players.NONE)
) {
    var accessible by mutableStateOf(false)

    fun getConnectionsList(): List<Int>{
        return connections
    }
}

fun Location.deepCopy(): Location {
    return this.copy(
        enemyShipList = this.enemyShipList.deepCopy(),
        myShipList = this.myShipList.deepCopy()
    )
}

fun SnapshotStateList<Ship>.deepCopy(): SnapshotStateList<Ship> {
    val copy = mutableStateListOf<Ship>()
    this.forEach { item ->
        copy.add(item.deepCopy())
    }
    return copy
}

