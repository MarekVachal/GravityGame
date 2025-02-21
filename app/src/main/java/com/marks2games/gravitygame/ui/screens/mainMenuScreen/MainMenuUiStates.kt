package com.marks2games.gravitygame.ui.screens.mainMenuScreen

import android.net.Uri

data class MainMenuUiStates (
    val showMenuList: Boolean = false,
    val textToShow: Text = Text.ABOUT_GAME,
    val showTextDialog: Boolean = false,
    val showSignInDialog: Boolean = false,
    val alreadySignAsGuest: Boolean = false,
    val userImage: Uri? = null
)