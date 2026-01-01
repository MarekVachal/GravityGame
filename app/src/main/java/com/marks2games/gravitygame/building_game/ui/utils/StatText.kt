package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun StatText(label: String, value: String, income: Int, isBordered: Boolean, border: Int = 0) {
    Text(text = if(isBordered) {
        "$label: $value + $income/$border"
    } else {
        "$label: $value + $income"
    }
    )
}