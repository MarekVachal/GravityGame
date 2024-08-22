package com.example.gravitygame.ai

import com.example.gravitygame.ui.utils.Players

fun evaluateGameState(gameState: GameState): Int {
    var score = 0

    //Move to enemy base
    for (location in gameState.locationList) {
        if(location.enemyShipList.isNotEmpty()){
            val shipList = location.enemyShipList
            for (ship in shipList) {
                score += getProximityScore(gameState)
            }
        }
    }

    //Hold ships together
    //Calculate battles
    //Hold your base

    return score
}

private fun getProximityScore(gameState: GameState): Int {
    var score = 0
    gameState.locationList.forEach { location ->
        if(location.enemyShipList.isNotEmpty()){
            for (ship in location.enemyShipList){
                score += when(location.id){
                    7 -> 0
                    6 -> 1
                    5 -> 2
                    4 -> 1
                    3 -> 3
                    2 -> 3
                    1 -> 3
                    0 -> 4
                    else -> 0
                }
            }
        }
    }
    return score
}

@Suppress("Unused")
private fun isWinningState(gameState: GameState): Boolean{
    return gameState.locationList[0].owner.value == Players.PLAYER2
}