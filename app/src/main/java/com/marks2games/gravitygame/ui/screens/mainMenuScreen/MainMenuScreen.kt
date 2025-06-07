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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.marks2games.gravitygame.core.ui.utils.DeleteEmpireDialog
import com.marks2games.gravitygame.core.ui.utils.SignInDialog
import com.marks2games.gravitygame.core.ui.utils.UiEvent.ShowSnackbar
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onBattleButtonClick: () -> Unit,
    mainMenuModel: MainMenuViewModel,
    onDeleteEmpireClick: (Boolean) -> Unit,
    battleModel: BattleViewModel,
    onSettingClick: () -> Unit,
    onAccountClick: () -> Unit,
    onEmpireButtonClick: () -> Unit,
    activity: Activity,
    context: Context
) {

    val mainMenuUiStates by mainMenuModel.mainMenuUiStates.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        mainMenuModel.uiEvent.collect { event ->
            when (event) {
                is ShowSnackbar -> {
                    val message = context.getString(event.messageResId)
                    snackbarHostState.showSnackbar(message = message)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!mainMenuUiStates.alreadySignAsGuest) {
            mainMenuModel.shouldSignIn()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.main_menu_back),
            contentDescription = "Background for main menu",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SnackbarHost(hostState = snackbarHostState)
                }
            },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = {},
                    actions = {
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
                    },
                    navigationIcon = {
                        IconButtonWithMenu(
                            mainMenuUiStates = mainMenuUiStates,
                            mainMenuModel = mainMenuModel,
                            onSettingClick = onSettingClick
                        )
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ){
                    OutlinedButton(
                        onClick = { mainMenuModel.updateShowDeleteEmpireDialog(true) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.deleteEmpireTitle),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Box {
                        Row {
                            IconButton(onClick = { mainMenuModel.openEmail(context) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.email),
                                    contentDescription = "Email icon",
                                    tint = Color.Unspecified
                                )
                            }
                            IconButton(onClick = { mainMenuModel.openDiscord(context) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.discord),
                                    contentDescription = "Discord icon",
                                    tint = Color.Unspecified
                                )
                            }
                            IconButton(onClick = { mainMenuModel.openFacebook(context) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.facebook_icon),
                                    contentDescription = "Facebook icon",
                                    tint = Color.White
                                )
                            }
                        }
                    }

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
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                DeleteEmpireDialog(
                    showDeleteEmpireDialog = mainMenuUiStates.showDeleteEmpireDialog,
                    mainMenuViewModel = mainMenuModel,
                    updateHasLaunchedEmpireScreen = onDeleteEmpireClick
                )

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
                    mainMenuModel = mainMenuModel,
                    context = context
                )

                NavigationButtons(
                    onBattleButtonClick = onBattleButtonClick,
                    onEmpireButtonClick = onEmpireButtonClick,
                    battleModel = battleModel,
                    mainMenuModel = mainMenuModel,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun NavigationButtons(
    modifier: Modifier = Modifier,
    onBattleButtonClick: () -> Unit,
    onEmpireButtonClick: () -> Unit,
    battleModel: BattleViewModel,
    mainMenuModel: MainMenuViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val horizontalInnerPadding = 24.dp
        val verticalInnerPadding = 12.dp

        OutlinedButton(
            onClick = {
                onBattleButtonClick()
                battleModel.isOnlineGame(false)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(
                horizontal = horizontalInnerPadding,
                vertical = verticalInnerPadding
            )
        ) {
            Text(
                text = stringResource(id = R.string.battleWithAI),
                style = MaterialTheme.typography.titleMedium
            )
        }
        OutlinedButton(
            onClick = {
                onBattleButtonClick()
                battleModel.isOnlineGame(true)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(
                horizontal = horizontalInnerPadding,
                vertical = verticalInnerPadding
            )
        ) {
            Text(
                text = stringResource(id = R.string.onlineBattle),
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedButton(
            onClick = {
                if (mainMenuModel.isSignIn()) {
                    onEmpireButtonClick()
                } else {
                    mainMenuModel.showSignInDialog(true)
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(
                horizontal = horizontalInnerPadding,
                vertical = verticalInnerPadding
            )
        ) {
            Text(
                text = stringResource(R.string.empireTitle),
                style = MaterialTheme.typography.titleMedium
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