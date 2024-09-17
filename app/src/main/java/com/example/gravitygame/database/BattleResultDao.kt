package com.example.gravitygame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BattleResultDao {
    @Insert
    suspend fun insertBattleResult(battleResult: BattleResult)

    @Query("SELECT * FROM battle_results")
    suspend fun getAllResults(): List<BattleResult>

    @Query("SELECT SUM(myShipLost) FROM battle_results")
    suspend fun getTotalMyShipLost(): Int

    @Query("SELECT SUM(enemyShipDestroyed) FROM battle_results")
    suspend fun getTotalEnemyShipDestroyed(): Int

    @Query("SELECT AVG(turn) FROM battle_results")
    suspend fun getAverageTurn(): Double

    @Query("SELECT COUNT(*) FROM battle_results")
    suspend fun getTotalBattleResults(): Int

    @Query("SELECT COUNT(*) FROM battle_results WHERE result = :resultType")
    suspend fun getCountOfWins(resultType: String = "win"): Int

    @Query("SELECT COUNT(*) FROM battle_results WHERE result = :resultType")
    suspend fun getCountOfLost(resultType: String = "lost"): Int

    @Query("SELECT COUNT(*) FROM battle_results WHERE result = :resultType")
    suspend fun getCountOfDraw(resultType: String = "draw"): Int


}