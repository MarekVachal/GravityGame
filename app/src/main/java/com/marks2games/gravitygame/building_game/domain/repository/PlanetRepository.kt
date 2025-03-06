package com.marks2games.gravitygame.building_game.domain.repository

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResource

interface PlanetRepository {
    suspend fun getPlanet(planetId: Int): Planet
    suspend fun getPlanetResource(planetId: Int, resource: PlanetResource): Double
    suspend fun updatePlanet(planet: Planet)
    suspend fun updatePlanetResource(planetId: Int, resource: PlanetResource, value: Double)
    suspend fun updatePlanetLevel(planetId: Int, level: Int)
}