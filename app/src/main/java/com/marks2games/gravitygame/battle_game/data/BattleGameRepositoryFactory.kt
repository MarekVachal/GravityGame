package com.marks2games.gravitygame.battle_game.data

import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.battle_game.data.model.PlayerData
import javax.inject.Inject

class BattleGameRepositoryFactory @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun create(playerData: PlayerData): BattleGameRepository {
        return BattleGameRepository(
            roomRef = playerData.roomRef ?: throw IllegalArgumentException("Room reference cannot be null"),
            player = playerData.player,
            auth = auth
        )
    }
}