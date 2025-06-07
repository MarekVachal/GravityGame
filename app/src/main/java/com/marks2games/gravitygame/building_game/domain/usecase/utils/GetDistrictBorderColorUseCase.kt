package com.marks2games.gravitygame.building_game.domain.usecase.utils

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class GetDistrictBorderColorUseCase @Inject constructor() {
    operator fun invoke(district: District, planetGrowBorder: Int, planetProgress: Int): Color {
        return if (district.type == DistrictEnum.UNNOCUPATED && planetProgress >= planetGrowBorder) {
            Color.Green
        } else if (district.type == DistrictEnum.UNNOCUPATED) {
            Color.Black
        } else if (!district.isWorking) {
            Color.Red
        } else {
            Color.Transparent
        }
    }
}