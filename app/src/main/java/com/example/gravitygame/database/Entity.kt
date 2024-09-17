package com.example.gravitygame.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "battle_results")
data class BattleResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: String,
    val timestamp: Long,
    val myShipLost: Int,
    val enemyShipDestroyed: Int,
    val turn: Int
)