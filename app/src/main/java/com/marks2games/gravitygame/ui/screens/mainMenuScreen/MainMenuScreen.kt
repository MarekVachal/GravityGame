package com.marks2games.gravitygame.ui.screens.mainMenuScreen

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberAsyncImagePainter
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel
import com.marks2games.gravitygame.core.ui.utils.SignInDialog
import kotlin.system.exitProcess

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onBattleButtonClick: () -> Unit,
    mainMenuModel: MainMenuViewModel,
    battleModel: BattleViewModel,
    onSettingClick: () -> Unit,
    onAccountClick: () -> Unit,
    onEmpireButtonClick: () -> Unit,
    activity: Activity,
    context: Context
) {

    val mainMenuUiStates by mainMenuModel.mainMenuUiStates.collectAsState()

    LaunchedEffect(Unit) {
        if (!mainMenuUiStates.alreadySignAsGuest) {
            mainMenuModel.shouldSignIn()
        }
    }

    InfoTextDialog(
        mainMenuModel = mainMenuModel,
        mainMenuUiStates = mainMenuUiStates,
        toShow = mainMenuUiStates.showTextDialog,
        context = context
    )

    SignInDialog(
        modifier = modifier,
        toShow = mainMenuUiStates.showSignInDialog,
        backToMainMenu = { mainMenuModel.showSignInDialog(false) },
        signInAnonymously = { mainMenuModel.anonymousSignIn() },
        signInWithGoogle = { mainMenuModel.signInWithGoogle() }
    )

    Image(
        painter = painterResource(id = R.drawable.main_menu_back),
        contentDescription = "Background for main menu",
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize()
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonWithMenu(
            mainMenuUiStates = mainMenuUiStates,
            mainMenuModel = mainMenuModel,
            onSettingClick = onSettingClick
        )

        IconButton(onClick = { onAccountClick() }) {
            if (mainMenuUiStates.userImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(mainMenuUiStates.userImage),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.user_account),
                    contentDescription = "User account icon",
                    tint = Color.Unspecified
                )
            }
        }
    }


    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 96.dp)
    ) {
        OutlinedButton(
            onClick = {
                onBattleButtonClick()
                battleModel.isOnlineGame(false)
            },
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
        Spacer(
            modifier = modifier.size(8.dp)
        )
        OutlinedButton(
            onClick = {
                onBattleButtonClick()
                battleModel.isOnlineGame(true)
            },
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onlineBattle),
                style = MaterialTheme.typography.titleMedium
            )
        }
        OutlinedButton(
            onClick = {
                onEmpireButtonClick()
            },
            modifier = modifier,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Empire",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = modifier.align(Alignment.CenterEnd),
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

            Box(
                modifier = modifier.align(Alignment.CenterStart)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { mainMenuModel.openEmail(context = context) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.email),
                            contentDescription = "Email icon",
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = { mainMenuModel.openDiscord(context = context) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.discord),
                            contentDescription = "Discord icon",
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = { mainMenuModel.openFacebook(context = context) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.facebook_icon),
                            contentDescription = "Facebook icon",
                            tint = Color.White
                        )
                    }
                }
            }
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
    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = { mainMenuModel.showMenuList(true) },
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu icon",
                tint = Color.Unspecified
            )
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
                    mainMenuModel.openTextDialog(text = Text.GAME_RULES, toShow = true)
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
                    mainMenuModel.openTextDialog(text = Text.ABOUT_GAME, toShow = true)
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
                    mainMenuModel.openTextDialog(text = Text.ABOUT_US, toShow = true)
                    mainMenuModel.showMenuList(false)
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.donateTitle)
                    )
                },
                onClick = {
                    mainMenuModel.openTextDialog(text = Text.DONATE, toShow = true)
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