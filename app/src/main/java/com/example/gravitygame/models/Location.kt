package com.example.gravitygame.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.viewModels.BattleViewModel
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    private var connection: List<Int>,
    @Contextual
    val myShipList: SnapshotStateList<Ship> = mutableStateListOf(),
    @Contextual
    val enemyShipList: SnapshotStateList<Ship> = mutableStateListOf(),
    var myAcceptableLost: Int = 1,
    var enemyAcceptableLost: Int = 1,
    var owner: MutableState<Players> = mutableStateOf(Players.NONE)
) {
    var accessible by mutableStateOf(false)


    fun getConnection(battleModel: BattleViewModel){
        for(i in battleModel.locationListUiState.value.locationList.indices) {
            if (connection.any {
                    it == i && battleModel.locationListUiState.value.locationList[it].myShipList.size < (battleModel.battleMap?.shipLimitOnPosition
                        ?: 0)
                }) {
                battleModel.locationListUiState.value.locationList[i].accessible = true
            }
        }
    }


    fun getConnectionList(): List<Int>{
        return connection
    }


}