package com.example.gravitygame.ai

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.deepCopy
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.ui.utils.calculateBattle
import com.example.gravitygame.viewModels.BattleViewModel

data class GameState(
    val locationList: List<Location>,
    val battleModel: BattleViewModel,
    val enemyBaseLocation: Location
)

data class Move(val ship: Ship, val targetLocation: Int)

class MCTS(private val iterations: Int, private val difficulty: Int) {

    fun findBestMove(initialState: GameState): GameState {
        val mapOfMovement: Map<Int, GameState> = simulate(initialState, iterations)
        val sortedMap: Map<Int, GameState> = mapOfMovement.toSortedMap(compareByDescending { it })
        val chosenStates: List<GameState> = sortedMap.entries
            .take(difficulty)
            .map { it.value }
        return chosenStates.random()
    }

    private fun simulate(state: GameState, iterate: Int): Map<Int, GameState> {
        var mapOfAcceptableLost: MutableMap<Int, Int>
        val mapOfScoredState: MutableMap<Int, GameState> = mutableMapOf()
        repeat(iterate) {
            val moveCombination = generateMoveCombinations(state)
            val movedState = state.applyMoves(moveCombination)
            if (movedState.checkForBattle()) {
                mapOfAcceptableLost = generateAcceptableLost(movedState).toMutableMap()
                movedState.applyAcceptableLost(mapOfAcceptableLost)
            }
            val score = evaluateGameState(movedState)
            mapOfScoredState[score] = movedState
        }
        return mapOfScoredState
    }

    private fun generateAcceptableLost(state: GameState): Map<Int, Int> {
        val mapOfAcceptableLost: MutableMap<Int, Int> = mutableMapOf()
        state.locationList.forEach { location ->
            if(location.enemyShipList.isNotEmpty() && location.myShipList.isNotEmpty()){
                when(calculateBattle(location)){
                    Players.PLAYER1 -> /*TODO( Function for increasing of acceptable lost of AI)*/ mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue++
                    Players.PLAYER2 -> mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue
                    Players.NONE -> /*TODO( What to do with a draw?)*/ mapOfAcceptableLost[location.id] = location.enemyAcceptableLost.intValue++
                }
            }
        }
        return mapOfAcceptableLost
    }

    private fun simulateTurn(mapOfMoves: Map<Ship, List<Move>>): List<Move> {
        val result = mutableListOf<Move>()
        mapOfMoves.values.forEach { result.add(it.random()) }
        return result
    }

    private fun generateMoveCombinations(state: GameState): List<Move> {
        val allPossibleMoves = state.getAllPossibleMoves()
        val mapOfMoves = allPossibleMoves.groupBy { it.ship }
        return simulateTurn(mapOfMoves)
    }
}

fun GameState.getAllPossibleMoves(): List<Move> {
    val moves = mutableListOf<Move>()
    for (location in locationList) {
        if (location.enemyShipList.isNotEmpty()) {
            val shipList = location.enemyShipList
            for (ship in shipList) {
                val possibleLocations = getPossibleLocations(ship, location)
                for (targetLocation in possibleLocations) {
                    moves.add(Move(ship, targetLocation))
                }
            }
        }
    }
    return moves
}

fun GameState.checkForBattle(): Boolean {
    this.locationList.forEach { location ->
        if(location.myShipList.isNotEmpty() && location.enemyShipList.isNotEmpty()){
            return true
        }
    }
    return false
}

fun GameState.applyAcceptableLost(mapOfAcceptableLost: Map<Int, Int>): GameState{
    val newState = this.copy(locationList = locationList.map { it.deepCopy() })
    mapOfAcceptableLost.forEach { (locationId, acceptableLost) ->
        val newAcceptableLost: MutableIntState = mutableIntStateOf(acceptableLost)
        newState.locationList[locationId].enemyAcceptableLost = newAcceptableLost
    }
    return newState
}

fun GameState.applyMoves(moves: List<Move>): GameState {
    var newState = this.copy(locationList = locationList.map { it.deepCopy() })
    for (move in moves) {
        newState = newState.applyMove(move)
    }
    return newState
}

fun GameState.applyMove(move: Move): GameState {
    val newLocationList = this.locationList.map { it.deepCopy() }.toMutableList()
    val currentLocation = newLocationList.first { location ->
        location.enemyShipList.contains(move.ship)
    }
    val targetLocation = newLocationList[move.targetLocation]
    currentLocation.enemyShipList.remove(move.ship)
    targetLocation.enemyShipList.add(move.ship)
    move.ship.startingPosition = targetLocation.id
    move.ship.currentPosition = targetLocation.id
    return this.copy(locationList = newLocationList)
}

@Suppress("Unused")
fun GameState.isTerminal(): Boolean {
    return checkBaseCaptured(Players.PLAYER1) || checkBaseCaptured(Players.PLAYER2)
}

private fun GameState.getPossibleLocations(ship: Ship, currentLocation: Location): List<Int> {
    return if (ship.type == ShipType.WARPER && this.enemyBaseLocation.myShipList.isEmpty()) {
        listOf(0)
    } else {
        currentLocation.getConnectionsList().filter { targetId ->
            val targetLocation = locationList[targetId]
            // Check movements rules
            targetLocation.enemyShipList.size < (battleModel.battleMap?.shipLimitOnPosition ?: 0) &&
                    !(targetLocation.owner.value == Players.PLAYER1 && currentLocation.owner.value == Players.PLAYER1)
        }
    }
}

private fun GameState.checkBaseCaptured(player: Players): Boolean {
    val player1Base = locationList[0]
    val player2Base = locationList.last()
    return when (player) {
        Players.PLAYER1 -> player2Base.owner.value == Players.PLAYER1
        Players.PLAYER2 -> player1Base.owner.value == Players.PLAYER2
        else -> false
    }
}