package com.example.gravitygame.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R

@Composable
fun ProgressIndicator(
    toShow: Boolean,
    inProgress: Boolean,
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
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.5f)
            ) {
                Row(
                    modifier = modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    if (inProgress){
                        CircularProgressIndicator(
                            modifier = modifier.size(96.dp),
                            color = Color.Black,
                            trackColor = Color.Red
                        )
                    } else {
                        Text(text = stringResource(id = R.string.newTurn))
                    }
                }
            }
        }
    }
}