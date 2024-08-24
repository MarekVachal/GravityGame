package com.example.gravitygame.ui.utils

data class Player(
    var player: Players = Players.PLAYER1,
    var opponent: Players = Players.PLAYER2,
    var showTutorial: Boolean = true,
    var win: Boolean = false,
    var lost: Boolean = false,
    var draw: Boolean = false
){

}
enum class Players{
    PLAYER1, PLAYER2, NONE
}

