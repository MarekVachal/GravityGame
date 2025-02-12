package com.marks2games.gravitygame.ui.screens.mainMenuScreen

data class MainMenuUiStates (
    val showMenuList: Boolean = false,
    val textToShow: Text = Text.ABOUT_GAME,
    val showTextDialog: Boolean = false,
    val showSignInDialog: Boolean = false,
    val alreadySignAsGuest: Boolean = false,
)