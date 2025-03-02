package com.marks2games.gravitygame.battle_game.data

import com.google.firebase.database.DatabaseReference
import com.marks2games.gravitygame.battle_game.data.model.PlayerData
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleMapEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.GameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SharedPlayerDataRepository @Inject constructor() {

    private val _playerData = MutableStateFlow(PlayerData())
    val playerData: StateFlow<PlayerData> = _playerData.asStateFlow()

    fun updatePlayer(player: Players, opponent: Players){
        _playerData.update { state ->
            state.copy(
                player = player,
                opponent = opponent
            )
        }
    }

    fun updateRoomRef(roomRef: DatabaseReference?){
        _playerData.update { state ->
            state.copy(
                roomRef = roomRef
            )
        }
    }

    fun updateBattleResult(battleResult: BattleResultEnum){
        _playerData.update { state ->
            state.copy(
                playerBattleResult = battleResult
            )
        }
    }

    fun updateBattleMap(battleMap: BattleMapEnum){
        _playerData.update { state ->
            state.copy(
                battleMap = battleMap
            )
        }
    }

    fun updateGameType(gameType: GameType){
        _playerData.update { state ->
            state.copy(
                gameType = gameType
            )
        }
    }

    fun updateIsOnline(isOnline: Boolean){
        _playerData.update { state ->
            state.copy(
                isOnline = isOnline
            )
        }
    }
}