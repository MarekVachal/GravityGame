package com.marks2games.gravitygame.battle_game.domain

import com.marks2games.gravitygame.battle_game.data.model.Location
import com.marks2games.gravitygame.battle_game.data.model.LocationList
import com.marks2games.gravitygame.battle_game.data.model.MovementRecord
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.battle_game.data.model.realtime_database.SimplifiedMove
import com.marks2games.gravitygame.battle_game.domain.ai.GameState
import com.marks2games.gravitygame.battle_game.domain.ai.MCTS
import com.marks2games.gravitygame.battle_game.domain.ai.createAiArmy
import com.marks2games.gravitygame.core.data.model.Cruiser
import com.marks2games.gravitygame.core.data.model.Destroyer
import com.marks2games.gravitygame.core.data.model.Ghost
import com.marks2games.gravitygame.core.data.model.Ship
import com.marks2games.gravitygame.core.data.model.Warper
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.data.model.enum_class.toShipType
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class Battlefield(
    val player1Base: Int,
    val player2Base: Int,
    val shipLimitOnPosition: Int,
    val shipLimitOnMap: Int,
) {
    fun baseOf(player: Players): Int = if (player == Players.PLAYER1) player1Base else player2Base
}

data class BattleLosses(val player: Int, val opponent: Int)

@ViewModelScoped
class BattleEngine @Inject constructor() {
    private val _locationList = MutableStateFlow(LocationList())
    val locationList = _locationList.asStateFlow()
    private val _movementRecord = MutableStateFlow(MovementRecord())
    val movementRecord = _movementRecord.asStateFlow()
    private lateinit var battlefield: Battlefield
    private val locations: List<Location> get() = _locationList.value.locationList

    fun setMap(field: Battlefield, locations: List<Location>) {
        battlefield = field
        locations[field.player1Base].owner.value = Players.PLAYER1
        locations[field.player2Base].owner.value = Players.PLAYER2
        _locationList.value = LocationList(locations)
    }

    fun createArmy(player: Players, cruisers: Int, destroyers: Int, ghosts: Int) {
        locations.forEach { location ->
            location.myShipList.clear()
            location.enemyShipList.clear()
        }

        val base = battlefield.baseOf(player)
        val fleet = locations[base].myShipList
        var id = 0
        repeat(cruisers) { fleet.add(Cruiser(id++)) }
        repeat(destroyers) { fleet.add(Destroyer(id++)) }
        repeat(ghosts) { fleet.add(Ghost(id++)) }
        fleet.add(Warper(id))
        fleet.forEach { ship ->
            ship.currentPosition = base
            ship.startingPosition = base
        }
    }

    fun createOfflineOpponent(opponent: Players) {
        val base = battlefield.baseOf(opponent)
        val fleet = locations[base].enemyShipList
        if (fleet.isNotEmpty()) return

        val ships = createAiArmy(battlefield.shipLimitOnMap)
        ships.forEach { ship ->
            ship.currentPosition = base
            ship.startingPosition = base
        }
        fleet.addAll(ships)
    }

    fun applyOpponentMove(move: SimplifiedMove, isSetup: Boolean) {
        if (isSetup) {
            move.simplifiedShipList.forEach { ship ->
                val createdShip = when (ship.shipType.toShipType()) {
                    ShipType.CRUISER -> Cruiser(ship.id)
                    ShipType.DESTROYER -> Destroyer(ship.id)
                    ShipType.GHOST -> Ghost(ship.id)
                    ShipType.WARPER -> Warper(ship.id)
                    null -> return
                }
                createdShip.currentPosition = ship.currentPosition
                createdShip.startingPosition = ship.startingPosition
                locations[ship.startingPosition!!].enemyShipList.add(createdShip)
            }
        } else {
            move.acceptableLost.forEach { record ->
                locations[record.locationId].enemyAcceptableLost.intValue = record.lostValue
            }
            move.simplifiedShipList.forEach { ship ->
                val start = locations[ship.startingPosition!!]
                val movingShip = start.enemyShipList.find { it.id == ship.id }
                if (movingShip != null) {
                    locations[ship.currentPosition!!].enemyShipList.add(movingShip)
                    start.enemyShipList.remove(movingShip)
                    movingShip.startingPosition = ship.currentPosition
                }
            }
        }
    }

    fun canStartMove(position: Int): Boolean =
        locations[position].myShipList.any { it.currentPosition == position && !it.hasMoved }

    fun hasWarper(position: Int): Boolean =
        locations[position].myShipList.any { it.type == ShipType.WARPER && it.currentPosition == position }

    fun isReachable(position: Int): Boolean = locations[position].accessible

    fun markReachableLocations(start: Int, opponent: Players) {
        val destinations = if (hasWarper(start)) {
            locations.indices.filter { it != start }
        } else {
            locations[start].getConnectionsList().filter { canTravelBetween(start, it, opponent) }
        }
        destinations.forEach { position ->
            if (locations[position].canAddMoreShips(battlefield.shipLimitOnPosition)) {
                locations[position].accessible = true
            }
        }
    }

    fun clearReachableLocations() {
        locations.forEach { it.accessible = false }
    }

    fun canAddShip(
        shipType: ShipType,
        start: Int?,
        destination: Int?,
        isWarperPresent: Boolean,
        shipsAlreadySelected: Int,
        opponent: Players,
    ): Boolean {
        if (destination == null) return false
        if (isWarperPresent && shipType != ShipType.WARPER &&
            (start == null || !canTravelBetween(start, destination, opponent))
        ) return false
        return locations[destination].myShipList.size + shipsAlreadySelected < battlefield.shipLimitOnPosition
    }

    private fun canTravelBetween(start: Int, destination: Int, opponent: Players): Boolean {
        return destination in locations[start].getConnectionsList() &&
            (locations[start].owner.value != opponent || locations[destination].owner.value != opponent)
    }

    fun moveFleet(start: Int, destination: Int, fleet: Map<ShipType, Int>, acceptableLoss: Int) {
        val origin = locations[start]
        val target = locations[destination]
        val moved = mutableMapOf<Ship, Int>()
        fleet.forEach { (type, count) ->
            repeat(count) {
                val ship = origin.myShipList.firstOrNull { it.type == type && !it.hasMoved }
                if (ship != null) {
                    target.myShipList.add(ship)
                    origin.myShipList.remove(ship)
                    ship.hasMoved = true
                    ship.startingPosition = start
                    ship.currentPosition = destination
                    moved[ship] = destination
                }
            }
        }
        setAcceptableLoss(destination, acceptableLoss)
        _movementRecord.update { it.copy(movementRecordOfTurn = it.movementRecordOfTurn + moved) }
    }

    fun setAcceptableLoss(position: Int, acceptableLoss: Int) {
        locations[position].myAcceptableLost.intValue = acceptableLoss
    }

    fun undoLastMove() {
        val record = _movementRecord.value.movementRecordOfTurn
        val lastMove = record.lastOrNull() ?: return
        lastMove.keys.forEach { ship ->
            ship.startingPosition?.let { locations[it].myShipList.add(ship) }
            ship.currentPosition?.let { locations[it].myShipList.remove(ship) }
            ship.currentPosition = ship.startingPosition
            ship.hasMoved = false
        }
        _movementRecord.update { it.copy(movementRecordOfTurn = record.dropLast(1)) }
    }

    fun clearEnemyRecord() {
        _movementRecord.update { it.copy(enemyRecord = emptyList()) }
    }

    fun prepareTurn() {
        locations.forEach { location ->
            location.wasBattleHere.value = false
            location.myShipList.forEach { ship -> ship.hasMoved = false }
        }
        _movementRecord.update { it.copy(movementRecordOfTurn = emptyList()) }
    }

    fun hasExceededBaseLimit(player: Players): Boolean =
        locations[battlefield.baseOf(player)].hasExceededShipLimit(battlefield.shipLimitOnPosition)

    fun distributeStartingFleet(player: Players) {
        if (!hasExceededBaseLimit(player)) return

        val base = locations[battlefield.baseOf(player)]
        val ships = base.myShipList.toMutableList()
        while (ships.isNotEmpty()) {
            val destinations = base.getConnectionsList()
                .filter { locations[it].enemyShipList.size < battlefield.shipLimitOnPosition }
                .toMutableList()
            if (base.canAddMoreShips(battlefield.shipLimitOnPosition)) destinations.add(base.id)

            val ship = ships.random()
            val destination = destinations.random()
            base.myShipList.remove(ship)
            locations[destination].myShipList.add(ship)
            ship.currentPosition = destination
            ships.remove(ship)
        }
    }

    suspend fun playAiTurn(player: Players, opponent: Players) {
        val bestMove = MCTS(iterations = 500, difficulty = 5).findBestMove(
            initialState = GameState(locations, battlefield),
            player = player,
            opponent = opponent,
        )
        _locationList.value = LocationList(bestMove.locationList)
        _movementRecord.update { record ->
            record.copy(enemyRecord = bestMove.locationList.flatMap { location ->
                location.enemyShipList.filter { it.startingPosition != it.currentPosition }
            })
        }
    }

    fun resetEnemyAcceptableLoss() {
        locations.forEach { it.enemyAcceptableLost.intValue = 1 }
    }

    fun conductBattles(player: Players, opponent: Players): BattleLosses {
        var playerLosses = 0
        var opponentLosses = 0
        locations.forEach { location ->
            if (location.myShipList.isNotEmpty() && location.enemyShipList.isNotEmpty()) {
                location.wasBattleHere.value = true
                location.originalMyShipList.replaceWith(location.myShipList.countByType())
                location.originalEnemyShipList.replaceWith(location.enemyShipList.countByType())

                val (winner, myLosses, enemyLosses) = calculateBattle(
                    location = location,
                    player = player,
                    opponent = opponent,
                    isSimulation = false,
                    writeDestroyedShips = { _, myLost, enemyLost ->
                        playerLosses += myLost
                        opponentLosses += enemyLost
                    },
                )
                if (winner != Players.NONE) location.owner.value = winner
                location.mapMyLost.replaceWith(myLosses)
                location.mapEnemyLost.replaceWith(enemyLosses)
                location.lastBattleResult = when (winner) {
                    Players.NONE -> BattleResultEnum.DRAW
                    player -> BattleResultEnum.WIN
                    else -> BattleResultEnum.LOSE
                }
            }

            if (location.myShipList.isNotEmpty() && location.enemyShipList.isEmpty()) {
                location.owner.value = player
            }
            if (location.myShipList.isEmpty() && location.enemyShipList.isNotEmpty()) {
                location.owner.value = opponent
            }
        }
        return BattleLosses(playerLosses, opponentLosses)
    }

    fun result(player: Players): BattleResultEnum? {
        val opponent = when (player) {
            Players.PLAYER1 -> Players.PLAYER2
            Players.PLAYER2 -> Players.PLAYER1
            Players.NONE -> return null
        }

        val playerBaseCaptured = locations[battlefield.baseOf(player)].owner.value == opponent
        val opponentBaseCaptured = locations[battlefield.baseOf(opponent)].owner.value == player
        return when {
            playerBaseCaptured && opponentBaseCaptured -> BattleResultEnum.DRAW
            playerBaseCaptured || locations.all { it.myShipList.isEmpty() } -> BattleResultEnum.LOSE
            opponentBaseCaptured || locations.all { it.enemyShipList.isEmpty() } -> BattleResultEnum.WIN
            else -> null
        }
    }

    private fun List<Ship>.countByType(): Map<ShipType, Int> {
        val counts = groupingBy { it.type }.eachCount()
        return ShipType.entries.associateWith { counts[it] ?: 0 }
    }

    private fun MutableMap<ShipType, Int>.replaceWith(counts: Map<ShipType, Int>) {
        clear()
        putAll(counts)
    }
}
