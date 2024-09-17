package com.example.gravitygame.ui.screens.accountScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.gravitygame.R

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onStatisticButtonClick: () -> Unit,
    onAchievementsButtonClick: () -> Unit
){
    Image(
        painter = painterResource(id = R.drawable.main_menu_back),
        contentDescription = "Background for main menu",
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize()
    )

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Button(
            onClick = { onStatisticButtonClick() }
        ) {
            Text(text = stringResource(id = R.string.statisticTitle))
        }
        Button(
            onClick = {onAchievementsButtonClick()},
            enabled = false
        ) {
            Text(text = stringResource(id = R.string.achievementsTitle))
        }
    }
}