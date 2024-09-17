package com.example.gravitygame.database

class BattleRepository(private val battleResultDao: BattleResultDao) {
    suspend fun insertBattleResult(battleResult: BattleResult) {
        battleResultDao.insertBattleResult(battleResult)
    }

    suspend fun getTotalMyShipLost(): Int {
        return battleResultDao.getTotalMyShipLost()
    }

    suspend fun getAllResults(): List<BattleResult>{
        return battleResultDao.getAllResults()
    }

    suspend fun getTotalEnemyShipDestroyed(): Int {
        return battleResultDao.getTotalEnemyShipDestroyed()
    }

    suspend fun getAverageTurn(): Double {
        return battleResultDao.getAverageTurn()
    }

    suspend fun getTotalBattles(): Int {
        return  battleResultDao.getTotalBattleResults()
    }

    suspend fun getCountOfWins(): Int {
        return battleResultDao.getCountOfWins()
    }

    suspend fun getCountOfLost(): Int {
        return battleResultDao.getCountOfLost()
    }

    suspend fun getCountOfDraw(): Int {
        return battleResultDao.getCountOfDraw()
    }
}