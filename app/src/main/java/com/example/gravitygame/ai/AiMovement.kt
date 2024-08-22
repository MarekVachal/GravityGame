package com.example.gravitygame.ai

import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.deepCopy
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.viewModels.BattleViewModel

data class GameState(
    val locationList: List<Location>,
    val battleModel: BattleViewModel,
    val enemyBaseLocation: Location
)

data class Move(val ship: Ship, val targetLocation: Int)

class MCTS(private val iterations: Int, private val difficulty: Int) {

    fun findBestMove(initialState: GameState): List<Move> {
        val mapOfMovement: Map<Int, List<Move>> = simulate(initialState, iterations)
        val sortedMap: Map<Int, List<Move>> = mapOfMovement.toSortedMap(compareByDescending { it })
        val chosenMoves: List<List<Move>> = sortedMap.entries
            .take(difficulty)
            .map { it.value }
        return chosenMoves.random()
    }

    private fun simulate(state: GameState, iterate: Int): Map<Int, List<Move>> {
        val mapOfScoredMoves: MutableMap<Int, List<Move>> = mutableMapOf()
        repeat(iterate) {
            val moveCombination = generateMoveCombinations(state)
            val movedState = state.applyMoves(moveCombination)
            val score = evaluateGameState(movedState)
            mapOfScoredMoves[score] = moveCombination
        }
        return mapOfScoredMoves
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