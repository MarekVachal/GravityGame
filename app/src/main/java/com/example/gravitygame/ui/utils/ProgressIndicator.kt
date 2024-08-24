package com.example.gravitygame.ui.utils

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("Unused")
@Composable
fun ProgressIndicator(
    toShow: Boolean
){
    if (toShow) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier

        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp),
                color = Color.Black,
                trackColor = Color.Red
            )
        }

    }
}