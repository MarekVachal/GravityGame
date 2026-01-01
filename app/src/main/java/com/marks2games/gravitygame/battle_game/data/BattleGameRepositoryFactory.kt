package com.marks2games.gravitygame.battle_game.data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class BattleGameRepositoryFactory @Inject constructor(
    private val sharedPlayerDataRepository: SharedPlayerDataRepository,
    private val auth: FirebaseAuth
) {
    fun create(): BattleGameRepository {
        return BattleGameRepository(
            roomRef = sharedPlayerDataRepository.getRoomRef() ?: throw IllegalArgumentException("Room reference cannot be null"),
            player = sharedPlayerDataRepository.getPlayer(),
            auth = auth
        )
    }
}