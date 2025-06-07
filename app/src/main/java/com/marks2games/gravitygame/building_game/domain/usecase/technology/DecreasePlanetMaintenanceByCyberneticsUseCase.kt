package com.marks2games.gravitygame.building_game.domain.usecase.technology

import javax.inject.Inject

class DecreasePlanetMaintenanceByCyberneticsUseCase @Inject constructor() {
    operator fun invoke(cyberneticsLevel: Int): Int{
        return cyberneticsLevel * 5
    }
}