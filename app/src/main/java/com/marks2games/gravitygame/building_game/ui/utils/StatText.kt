package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun StatText(label: String, value: String) {
    Text(text = "$label: $value", modifier = Modifier.padding(horizontal = 4.dp))
}