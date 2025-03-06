package com.marks2games.gravitygame.building_game.domain.usecase

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class UpdatePlanetUseCase @Inject constructor(
    private val planetRepository: PlanetRepository
) {
    suspend operator fun invoke(planet: Planet) {
        planetRepository.updatePlanet(planet)
    }
}