package com.marks2games.gravitygame.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marks2games.gravitygame.ui.utils.BattleResultEnum

@Entity(tableName = "battle_results")
data class BattleResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: BattleResultEnum,
    val timestamp: Long,
    val myShipLost: Int,
    val enemyShipDestroyed: Int,
    val turn: Int
)