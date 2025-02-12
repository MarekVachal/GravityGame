package com.marks2games.gravitygame.ui.screens.matchmakingScreen

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

data class MatchmakingUiStates(
    val toShow: Boolean = true,
    val opponentFound: Boolean = false,
    val waitingForConfirmation: Boolean = false,
    val waitingListener: ValueEventListener? = null,
    val roomRef: DatabaseReference? = null,
    val toShowSignInDialog: Boolean = false
)
