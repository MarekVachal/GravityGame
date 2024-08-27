package com.example.gravitygame.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onBattleButtonClick: () -> Unit
) {

    Image(
        painter = painterResource(id = R.drawable.main_menu_back),
        contentDescription = "Background for main menu",
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize()
    )
    Row (
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        IconButton(
            onClick = { /*TODO*/ },
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu icon",
                tint = Color.Unspecified)
        }
        
        Image(
            painter = painterResource(id = R.drawable.name),
            contentDescription = "Name of the game",
            alignment = Alignment.Center
        )
        
        Box(
            modifier = modifier
        ){
            Row (
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.donate),
                        contentDescription = "Donate Button",
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.discord),
                        contentDescription = "Discord icon",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = onBattleButtonClick,
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.battleWithAI),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}



