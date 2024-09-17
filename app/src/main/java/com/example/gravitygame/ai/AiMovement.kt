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

        // Spustíme všechny iterace paralelně pomocí async
        val deferredResults = (1..iterate).map {
            async(Dispatchers.Default) {
                val movedState = state.deepCopy()
                movedState.generateTurnMove()
                val (score, mapOfLost) = movedState.evaluateGameState(playerData = playerData)
                movedState.applyAcceptableLost(mapOfLost)
                score to movedState
            }
        }

        // Počkáme, až všechny async operace skončí a přidáme výsledky do mapy
        val results = deferredResults.awaitAll()

        results.forEach { (score, movedState) ->
            mapOfScoredState[score] = movedState
        }

        return@coroutineScope mapOfScoredState
    }


}

private fun GameState.evaluateGameState(playerData: PlayerData): Pair<Int, Map<Int, Int>> {
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
        score -= 10
    }

    val (scoreFromSimulation, mapOfLost) = simulateBattle(
        state = this,
        playerData = playerData,
        battleModel = battleModel
    )
    score += scoreFromSimulation



    return Pair(score, mapOfLost)
}

private fun simulateBattle(
    state: GameState,
    playerData: PlayerData,
    battleModel: BattleViewModel
): Pair<Int, Map<Int, Int>> {
    val mapOfAcceptableLost: MutableMap<Int, Int> = mutableMapOf()
    var score = 0
    var incrementOfLost = 0

    val battleState = state.deepCopy()
    battleState.locationList.forEach { location ->
        if(location.enemyShipList.isNotEmpty() && location.myShipList.isNotEmpty()){
            val enemyShipsOnLocation = location.enemyShipList.size
            val (player, _, _) = calculateBattle(
                location = location,
                playerData = playerData,
                isSimulation = true,
                battleModel = battleModel
            )
            when (player){
                Players.PLAYER1 -> {
                    var playerLost: Players
                    var playerShipLost: Int
                    var aiShipLost: Int
                    var player2WinInLoop = false

                    do {
                        val result = calculateBattle(
                            location = location,
                            playerData = playerData,
                            isSimulation = true,
                            battleModel = battleModel
                        )
                        playerLost = result.first
                        playerShipLost = result.second.values.sum()
                        aiShipLost = result.third.values.sum()

                        if(playerLost == Players.PLAYER2){
                            player2WinInLoop = true
                        }

                        mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue++
                        incrementOfLost++
                        battleState.locationList[location.id].enemyAcceptableLost.intValue++
                    } while (playerLost == Players.PLAYER2 || incrementOfLost == enemyShipsOnLocation)

                    if (player2WinInLoop){
                        score += 20
                    } else if (aiShipLost < playerShipLost){
                        score +=5
                    } else if (playerShipLost < aiShipLost){
                        score -=20
                    }
                }
                Players.PLAYER2 -> {
                    mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue
                    score += 30
                }
                Players.NONE -> {
                    mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue++
                    incrementOfLost++
                    battleState.locationList[location.id].enemyAcceptableLost.intValue++
                    val (playerDraw, playerLostDraw, aiLostDraw) = calculateBattle(
                        location = location,
                        playerData = playerData,
                        isSimulation = true,
                        battleModel = battleModel
                    )
                    when(playerDraw){
                        Players.PLAYER1 -> {
                            mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue--
                            score -=10
                        }
                        Players.PLAYER2 -> {
                            score += if(playerLostDraw.values.sum() > aiLostDraw.values.sum()) 20 else 10
                        }
                        Players.NONE -> score += if(playerLostDraw.values.sum() > aiLostDraw.values.sum()) 5 else 0
                    }
                }
            }
        }
    }
    Log.d("Losts", "$mapOfAcceptableLost")
    return Pair(score, mapOfAcceptableLost.toMap())

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
private fun GameState.isWinningState(): Boolean{
    return this.locationList[0].owner.value == Players.PLAYER2
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

private fun GameState.applyAcceptableLost(mapOfAcceptableLost: Map<Int, Int>){
    mapOfAcceptableLost.forEach { (locationId, acceptableLost) ->
        this.locationList[locationId].enemyAcceptableLost.intValue = acceptableLost
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