package com.marks2games.gravitygame.building_game.domain.repository

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireResource

interface EmpireRepository {
    suspend fun getEmpire(): Empire
    suspend fun updateEmpire(empire: Empire)
    suspend fun getEmpireResource(resource: EmpireResource): Double
    suspend fun updateEmpireResource(resource: EmpireResource, value: Double)
    suspend fun saveTurn(value: Int)
    suspend fun updateUpdateTime(value: Long)
}