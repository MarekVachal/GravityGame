package com.example.gravitygame.ui.utils.progressIndicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("Unused")
@Composable
fun ProgressIndicator(
    toShow: Boolean,
    modifier: Modifier = Modifier
) {
    if (toShow) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = modifier

            ) {
                CircularProgressIndicator(
                    modifier = modifier,
                    color = Color.Black,
                    trackColor = Color.Red
                )
            }

        }
    }
}