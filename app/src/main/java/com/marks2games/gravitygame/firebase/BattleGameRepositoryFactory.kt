package com.marks2games.gravitygame.firebase

import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.models.PlayerData
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