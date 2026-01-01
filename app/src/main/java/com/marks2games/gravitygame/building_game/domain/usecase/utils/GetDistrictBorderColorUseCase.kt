package com.marks2games.gravitygame.building_game.domain.usecase.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import javax.inject.Inject

class GetDistrictBorderColorUseCase @Inject constructor() {
    operator fun invoke(district: District, isProgress: Boolean): Color {
        Log.d("GetDistrictBorderColorUseCase", "invoke: $district")
        return if (district.type == DistrictEnum.UNNOCUPATED){
            if(isProgress && district.isWorking) {
                Log.d("GetDistrictBorderColorUseCase", "invoke: Green")
                Color.Green
            } else {
                Log.d("GetDistrictBorderColorUseCase", "invoke: Black")
                Color.Black
            }
        } else if (!district.isWorking) {
            Log.d("GetDistrictBorderColorUseCase", "invoke: Red")
            Color.Red
        } else {
            Log.d("GetDistrictBorderColorUseCase", "invoke: Transparent")
            Color.Transparent
        }
    }
}