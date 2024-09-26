package com.example.gravitygame.ui.screens.mainMenuScreen

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.gravitygame.R
import kotlin.system.exitProcess

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onBattleButtonClick: () -> Unit,
    mainMenuModel: MainMenuViewModel,
    onSettingClick: () -> Unit,
    onAccountClick: () -> Unit,
    activity: Activity,
    context: Context
) {

    val mainMenuUiStates by mainMenuModel.mainMenuUiStates.collectAsState()

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
        IconButtonWithMenu(
            mainMenuUiStates = mainMenuUiStates,
            mainMenuModel = mainMenuModel,
            onSettingClick = onSettingClick
        )

        Box(
            modifier = modifier
        ){
            Row (
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                IconButton(onClick = { mainMenuModel.openDiscord(context = context) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.discord),
                        contentDescription = "Discord icon",
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { mainMenuModel.openEmail(context = context) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Email icon",
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = { onAccountClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.user_account),
                        contentDescription = "User account icon",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 96.dp)
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

    Row (
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .padding(24.dp)
    ){
        IconButton(
            onClick = {
                activity.finish()
                exitProcess(0)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout icon",
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun IconButtonWithMenu(
    mainMenuUiStates: MainMenuUiStates,
    mainMenuModel: MainMenuViewModel,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier
    ){
        IconButton(
            onClick = { mainMenuModel.showMenuList(true) },
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu icon",
                tint = Color.Unspecified)
        }

        DropdownMenu(
            expanded = mainMenuUiStates.showMenuList,
            onDismissRequest = { mainMenuModel.showMenuList(false) },
            properties = PopupProperties(),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.gameRulesTitle)
                    )
                },
                onClick = {
                    mainMenuModel.showMenuList(false)
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.aboutGameTitle)
                    )
                },
                onClick = {
                    mainMenuModel.showMenuList(false)
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.aboutUsTitle)
                    )
                },
                onClick = {
                    mainMenuModel.showMenuList(false)
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.donate)
                    )
                },
                onClick = {
                    mainMenuModel.showMenuList(false)
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.settingsTitle)
                    )
                },
                onClick = {
                    mainMenuModel.showMenuList(false)
                    onSettingClick()
                }
            )
        }
    }
}