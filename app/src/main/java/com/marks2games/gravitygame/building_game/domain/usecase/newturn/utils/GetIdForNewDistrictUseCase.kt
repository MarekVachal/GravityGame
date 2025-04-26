package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import javax.inject.Inject

class GetIdForNewDistrictUseCase @Inject constructor() {
    operator fun invoke(districts: Int): Int {
        return districts

    }
}