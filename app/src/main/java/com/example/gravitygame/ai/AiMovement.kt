package com.example.gravitygame.ai

import android.util.Log
import com.example.gravitygame.maps.BattleMap
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.deepCopy
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.ui.utils.calculateBattle
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.example.gravitygame.ui.utils.PlayerData
import kotlinx.coroutines.*

data class GameState(
    val locationList: List<Location>,
    val battleModel: BattleViewModel,
    val gameMap: BattleMap
)

data class Move(val ship: Ship, val targetLocation: Int)

class MCTS(private val iterations: Int, private val difficulty: Int) {

    suspend fun findBestMove(
        initialState: GameState,
        playerData: PlayerData,
    ): GameState {
        val mapOfMovement: Map<Int, GameState> = simulate(
            state = initialState,
            iterate = iterations,
            playerData = playerData,
        )
        val sortedMap: Map<Int, GameState> = mapOfMovement.toSortedMap(compareByDescending { it })
        val chosenStates: List<GameState> = sortedMap.entries
            .take(difficulty)
            .map { it.value }
        Log.d("Best move", "$chosenStates")
        return chosenStates.random()
    }

    private suspend fun simulate(
        state: GameState,
        iterate: Int,
        playerData: PlayerData
    ): Map<Int, GameState> = coroutineScope {
        val mapOfScoredState: MutableMap<Int, GameState> = mutableMapOf()

        val deferredResults = (1..iterate).map {
            async(Dispatchers.Default) {
                val movedState = state.deepCopy()
                movedState.generateTurnMove()
                val score = movedState.evaluateGameState(playerData = playerData)
                score to movedState
            }
        }

        val results = deferredResults.awaitAll()

        results.forEach { (score, movedState) ->
            mapOfScoredState[score] = movedState
        }

        return@coroutineScope mapOfScoredState
    }


}

private fun GameState.evaluateGameState(playerData: PlayerData): Int {
    var score = 0

    //Move to enemy base
    for (location in this.locationList) {
        if(location.enemyShipList.isNotEmpty()){
            val shipList = location.enemyShipList
            for (ship in shipList) {
                score += getProximityScore(this)
            }
        }
    }

    //Empty AI base remove score
    if(this.locationList[this.gameMap.player2Base].enemyShipList.isEmpty() &&
        this.locationList.any { location -> location.myShipList.any { ship -> ship.type == ShipType.WARPER } }){
        score -= 20
    }

    //Score according to battles plus improvement of AI acceptableLoss
    score += simulateBattle(playerData = playerData)

    if (this.isWinningState()) score += 30
    if (this.isLostState()) score -= 40

    return score
}

private fun GameState.simulateBattle(
    playerData: PlayerData,
): Int {

    this.locationList.forEach { location ->
        if (location.myShipList.isNotEmpty() && location.enemyShipList.isNotEmpty()) {
            val maxAcceptableLost = location.enemyShipList.size
            var enemyAcceptableLost = location.enemyAcceptableLost.intValue

            while (enemyAcceptableLost <= maxAcceptableLost) {
                val (winningPlayer, _, _) = calculateBattle(
                    location = location,
                    playerData = playerData,
                    isSimulation = true,
                    battleModel = this.battleModel
                )

                if (winningPlayer == Players.PLAYER2) {
                    location.enemyAcceptableLost.intValue = enemyAcceptableLost
                    return 15  // AI wins battle
                }

                enemyAcceptableLost++
            }
            return -10  // AI lost battle
        }
    }

    // No Battle
    return 0
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

private fun GameState.generateTurnMove(){
    val ships: MutableList<Ship> = mutableListOf()
    this.locationList.forEach {
            location -> location.enemyShipList.forEach {
            ship -> ships.add(ship)} }
    do {
        val randomShip = ships.random()
        val possibleMoves = this.getPossibleLocations(ship = randomShip)
        val move = Move(randomShip, possibleMoves.random())
        this.applyMove(move)
        ships.remove(randomShip)
    } while (ships.isNotEmpty())
}

private fun GameState.getPossibleLocations(ship: Ship): List<Int> {
    val currentLocation = ship.currentPosition?.let { this.locationList[it] }
    val battleLimit = battleModel.battleMap?.shipLimitOnPosition ?: 0

    return when {
        ship.type == ShipType.WARPER && this.locationList[this.gameMap.player1Base].myShipList.isEmpty()
        -> listOf(0)
        currentLocation != null
        -> {val connections = currentLocation.getConnectionsList().filter { targetId ->
            val targetLocation = this.locationList[targetId]
            targetLocation.enemyShipList.size < battleLimit &&
                    !(targetLocation.owner.value == Players.PLAYER1 && currentLocation.owner.value == Players.PLAYER1)
        }
            val currentIsValid = currentLocation.enemyShipList.size < battleLimit
            connections + if (currentIsValid) listOf(currentLocation.id) else emptyList()
        }
        else -> emptyList()
    }
}

private fun GameState.applyMove(move: Move){
    val currentLocation = this.locationList.first { location ->
        location.enemyShipList.contains(move.ship)
    }
    val targetLocation = this.locationList[move.targetLocation]
    currentLocation.enemyShipList.remove(move.ship)
    targetLocation.enemyShipList.add(move.ship)
    move.ship.startingPosition = currentLocation.id
    move.ship.currentPosition = targetLocation.id
}

private fun GameState.isWinningState(): Boolean{
    val player1Base = this.gameMap.player1Base
    return this.locationList[player1Base].owner.value == Players.PLAYER2
}

private fun GameState.isLostState(): Boolean{
    val player2Base = this.gameMap.player2Base
    return this.locationList[player2Base].owner.value == Players.PLAYER1
}

@Suppress("Unused")
private fun GameState.isTerminal(): Boolean {
    return checkBaseCaptured(Players.PLAYER1) || checkBaseCaptured(Players.PLAYER2)
}

private fun GameState.checkBaseCaptured(player: Players): Boolean {
    val player1Base = this.gameMap.player1Base
    val player2Base = this.gameMap.player2Base
    return when (player) {
        Players.PLAYER1 -> this.locationList[player2Base].owner.value == Players.PLAYER1
        Players.PLAYER2 -> this.locationList[player1Base].owner.value == Players.PLAYER2
        else -> false
    }
}

private fun GameState.deepCopy(): GameState {
    val copiedLocationList = this.locationList.map { it.deepCopy() }

    return GameState(
        locationList = copiedLocationList,
        battleModel = this.battleModel,
        gameMap = this.gameMap
    )
}
