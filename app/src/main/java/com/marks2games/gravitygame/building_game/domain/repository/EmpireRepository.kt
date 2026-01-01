package com.marks2games.gravitygame.building_game.domain.repository

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet

interface EmpireRepository {
    suspend fun getEmpire(): Empire
    suspend fun updateEmpire(empire: Empire)
    suspend fun getPlanet(planetId: Int): Planet
    suspend fun updatePlanet(planet: Planet)
    suspend fun deleteEmpire()
    suspend fun deletePlanet(planetId: Int)
}