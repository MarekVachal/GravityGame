package com.example.gravitygame.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import com.example.gravitygame.ai.GameState
import com.example.gravitygame.ai.MCTS
import com.example.gravitygame.maps.BattleMap
import com.example.gravitygame.models.Cruiser
import com.example.gravitygame.models.Destroyer
import com.example.gravitygame.models.Ghost
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.LocationList
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.Warper
import com.example.gravitygame.ui.utils.CoroutineTimer
import com.example.gravitygame.ui.utils.Player
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.ui.utils.calculateBattle
import com.example.gravitygame.uiStates.MovementRecord
import com.example.gravitygame.uiStates.MovementUiState
import com.example.gravitygame.uiStates.SelectArmyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BattleViewModel : ViewModel() {

    private val _movementUiState = MutableStateFlow(MovementUiState())
    val movementUiState: StateFlow<MovementUiState> = _movementUiState.asStateFlow()
    private var _locationListUiState = MutableStateFlow(LocationList())
    val locationListUiState: StateFlow<LocationList> = _locationListUiState.asStateFlow()
    var battleMap: BattleMap? = null
    private val _movementRecord = MutableStateFlow(MovementRecord())
    val movementRecord: StateFlow<MovementRecord> = _movementRecord.asStateFlow()
    val playerData = Player()
    private val mctsIterations = 100
    private val aiDifficulty = 5


    private fun turnCounter(){
        val count = movementUiState.value.turn + 1
        _movementUiState.value = _movementUiState.value.copy(turn = count)
    }
    fun createMapBoxPositions(location: Int, coordinates: Rect){
        val mapBoxCoordinates: MutableMap<Int, Rect> = movementUiState.value.mapBoxCoordinates.toMutableMap()
        mapBoxCoordinates[location] = coordinates
        _movementUiState.value = _movementUiState.value.copy(mapBoxCoordinates = mapBoxCoordinates.toMap())
    }

    fun changeLastTouchPosition(offset: Offset){
        _movementUiState.value = _movementUiState.value.copy(lastTouchPosition = offset)
    }

    private fun updateRecordsForTurn() {
        val indexOfEndLocation = movementUiState.value.endPosition
        val movementMap: MutableMap<Int, Int> = mutableMapOf()
        val movementRecord: MutableList<MutableMap<Int, Int>> =
            movementRecord.value.movementRecordOfTurn
        indexOfEndLocation?.let {
            locationListUiState.value.locationList[it].myShipList
                .forEach { ship ->
                    if (ship.justMoved)
                        movementMap[ship.id] = indexOfEndLocation
                }
        }
        movementRecord.add(movementMap)
        _movementRecord.value = _movementRecord.value.copy(movementRecordOfTurn = movementRecord)
    }

    private fun cleanRecordsForTurn() {
        _movementRecord.value.movementRecordOfTurn.clear()
    }

    fun createBattleMap(selectedMap: BattleMap) {
        battleMap = selectedMap
        battleMap?.let {
            _locationListUiState.value =
                _locationListUiState.value.copy(locationList = it.locationList)
        }
        setPlayerBases()
    }

    private fun setPlayerBases() {
        val player1Base = 0
        val player2Base = locationListUiState.value.locationList.lastIndex
        _locationListUiState.value.locationList[player1Base].owner.value = Players.PLAYER1
        _locationListUiState.value.locationList[player2Base].owner.value = Players.PLAYER2
    }

    fun createArmyList(selectArmyUiState: SelectArmyUiState) {
        _locationListUiState.value.locationList.forEach { it.myShipList.clear() }
        _locationListUiState.value.locationList.forEach { it.enemyShipList.clear() }
        var indexNumber = 0
        val startingLocationIndex =
            if (playerData.player == Players.PLAYER1) 0 else locationListUiState.value.locationList.lastIndex
        for (i in 1..selectArmyUiState.numberCruisers) {
            _locationListUiState.value.locationList[startingLocationIndex].myShipList.add(Cruiser(indexNumber))
            indexNumber++
        }
        for (i in 1..selectArmyUiState.numberDestroyers) {
            _locationListUiState.value.locationList[startingLocationIndex].myShipList.add(Destroyer(indexNumber))
            indexNumber++
        }
        for (i in 1..selectArmyUiState.numberGhosts) {
            _locationListUiState.value.locationList[startingLocationIndex].myShipList.add(Ghost(indexNumber))
            indexNumber++
        }
        _locationListUiState.value.locationList[startingLocationIndex].myShipList.add(Warper(indexNumber))
        _locationListUiState.value.locationList[startingLocationIndex].myShipList.forEach { ship ->
            ship.currentPosition = startingLocationIndex
            ship.startingPosition = startingLocationIndex
        }
    }

    fun initializeEnemyShipList(enemyShipList: List<Ship>) {
        val startingLocationIndex =
            if (playerData.player == Players.PLAYER1) locationListUiState.value.locationList.lastIndex else 0
        _locationListUiState.value.locationList[startingLocationIndex].enemyShipList.addAll(
            enemyShipList
        )
    }

    fun finishTurn(timer: CoroutineTimer) {
        cleanHasMoved()
        cleanRecordsForTurn()
        cleanMovementValues()
        turnCounter()
        aiMove()
        val newLocationList = locationListUiState.value.locationList
        _locationListUiState.value.locationList.forEach {
            if (it.myShipList.isNotEmpty() && it.enemyShipList.isNotEmpty()) {
                when (calculateBattle(it)) {
                    Players.PLAYER1 -> newLocationList[it.id].owner.value = Players.PLAYER1
                    Players.PLAYER2 -> newLocationList[it.id].owner.value = Players.PLAYER2
                    Players.NONE -> return
                }
            }
            if (it.myShipList.isNotEmpty() && it.enemyShipList.isEmpty() && it.owner.value != playerData.player) {
                newLocationList[it.id].owner.value = playerData.player
            }
            if (it.myShipList.isEmpty() && it.enemyShipList.isNotEmpty() && it.owner.value != playerData.opponent) {
                newLocationList[it.id].owner.value = playerData.opponent
            }
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
        checkEndCondition(thisPlayer = playerData.player, timer = timer)


        /*
        val gameData = GameData()
        gameData.updateLocationList(locationListTrue = locationList, armyList = armyList)
        val jsonData = Json.encodeToString(gameData)
        */
    }

    private fun initializeGameState(): GameState {
        val locationList = locationListUiState.value.locationList
        val enemyBaseLocation = locationListUiState.value.locationList[0]
        val battleModel = this
        return GameState(
            locationList = locationList,
            battleModel = battleModel,
            enemyBaseLocation = enemyBaseLocation
        )
    }

    private fun aiMove() {
        val state = initializeGameState()
        val mcts = MCTS(mctsIterations, aiDifficulty)
        val bestMove = mcts.findBestMove(state)
        updateUIWithBestMove(bestMove)
    }

    private fun updateUIWithBestMove(state: GameState) {
        _locationListUiState.value =
            _locationListUiState.value.copy(locationList = state.locationList)
    }

    private fun checkEndCondition(thisPlayer: Players, timer: CoroutineTimer) {
        val player1Base = 0
        val player2Base = locationListUiState.value.locationList.lastIndex

        if (locationListUiState.value.locationList[player1Base].owner.value == Players.PLAYER2) {
            playerData.lost = thisPlayer == Players.PLAYER1
            playerData.win = thisPlayer == Players.PLAYER2
            endOfGame(timer = timer)
        }

        if (locationListUiState.value.locationList[player2Base].owner.value == Players.PLAYER1) {
            playerData.lost = thisPlayer == Players.PLAYER2
            playerData.win = thisPlayer == Players.PLAYER1
            endOfGame(timer = timer)
        }
    }

    private fun endOfGame(timer: CoroutineTimer) {
        showEndOfGameDialog(true)
        timer.stopTimer()
    }

    fun showEndOfGameDialog(toShow: Boolean) {
        if (toShow) {
            _movementUiState.value = _movementUiState.value.copy(showEndOfGameDialog = true)
        } else {
            _movementUiState.value = _movementUiState.value.copy(showEndOfGameDialog = false)
        }
    }

    fun showLocationInfoDialog(toShow: Boolean) {
        if (toShow) {
            _movementUiState.value = _movementUiState.value.copy(showLocationInfoDialog = true)
        } else {
            _movementUiState.value = _movementUiState.value.copy(showLocationInfoDialog = false)
        }
    }

    private fun moveShips(locationList: MutableList<Location>, shipType: ShipType) {
        val shipInIssueNullable: Ship? =
            movementUiState.value.startPosition?.let { startLocation -> locationListUiState.value.locationList[startLocation].myShipList.firstOrNull { ship -> ship.type == shipType && !ship.hasMoved } }
        val shipInIssue: Ship = shipInIssueNullable?.let { shipInIssueNullable } ?: return
        movementUiState.value.endPosition?.let { endPosition ->
            locationList[endPosition].myShipList.add(
                shipInIssue
            )
        }
        movementUiState.value.startPosition?.let { startLocation ->
            locationList[startLocation].myShipList.remove(
                shipInIssue
            )
        }
        shipInIssue.hasMoved = true
        shipInIssue.justMoved = true
        shipInIssue.startingPosition = movementUiState.value.startPosition
        shipInIssue.currentPosition = movementUiState.value.endPosition
    }

    fun initializeLocationDialogValues() {
        val cruiserOnPosition =
            locationListUiState.value.locationList[movementUiState.value.locationForInfo].myShipList.count { ship -> ship.type == ShipType.CRUISER }
        val destroyerOnPosition =
            locationListUiState.value.locationList[movementUiState.value.locationForInfo].myShipList.count { ship -> ship.type == ShipType.DESTROYER }
        val ghostOnPosition =
            locationListUiState.value.locationList[movementUiState.value.locationForInfo].myShipList.count { ship -> ship.type == ShipType.GHOST }
        val warperOnPosition =
            locationListUiState.value.locationList[movementUiState.value.locationForInfo].myShipList.count { ship -> ship.type == ShipType.WARPER }
        val acceptableLost = locationListUiState.value.locationList[movementUiState.value.locationForInfo].myAcceptableLost
        _movementUiState.value = _movementUiState.value.copy(
            acceptableLost = acceptableLost.intValue.toFloat(),
            cruiserOnPosition = cruiserOnPosition,
            destroyerOnPosition = destroyerOnPosition,
            ghostOnPosition = ghostOnPosition,
            warperOnPosition = warperOnPosition
        )
    }

    fun closeLocationInfoDialog(){
        val newLocationList = locationListUiState.value.locationList
        val intValue: Int = movementUiState.value.acceptableLost.toInt()
        newLocationList[movementUiState.value.locationForInfo].myAcceptableLost = mutableIntStateOf(intValue)
        showLocationInfoDialog(false)
        cleanAcceptableLost()
    }


    fun initializeArmyDialogValues() {
        val cruiserOnPosition =
            movementUiState.value.endPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.CRUISER } } ?: 0
        val destroyerOnPosition =
            movementUiState.value.endPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.DESTROYER } } ?: 0
        val ghostOnPosition =
            movementUiState.value.endPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.GHOST } } ?: 0
        val warperOnPosition =
            movementUiState.value.endPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.WARPER } } ?: 0
        val cruiserToMove =
            movementUiState.value.startPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.CRUISER && !ship.hasMoved } } ?: 0
        val destroyerToMove =
            movementUiState.value.startPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.DESTROYER && !ship.hasMoved } } ?: 0
        val ghostToMove =
            movementUiState.value.startPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.GHOST && !ship.hasMoved } } ?: 0
        val warperToMove =
            movementUiState.value.startPosition?.let { locationListUiState.value.locationList[it].myShipList.count { ship -> ship.type == ShipType.WARPER && !ship.hasMoved } } ?: 0
        _movementUiState.value = _movementUiState.value.copy(
            cruiserOnPosition = cruiserOnPosition,
            destroyerOnPosition = destroyerOnPosition,
            ghostOnPosition = ghostOnPosition,
            warperOnPosition = warperOnPosition,
            cruiserToMove = cruiserToMove,
            destroyerToMove = destroyerToMove,
            ghostToMove = ghostToMove,
            warperToMove = warperToMove
        )
    }

    fun addShip(shipType: ShipType) {
        when (shipType) {
            ShipType.CRUISER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    cruiserToMove = movementUiState.value.cruiserToMove.dec(),
                    cruiserOnPosition = movementUiState.value.cruiserOnPosition.inc(),
                    movingCruisers = movementUiState.value.movingCruisers.inc()
                )
            }

            ShipType.DESTROYER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    destroyerToMove = movementUiState.value.destroyerToMove.dec(),
                    destroyerOnPosition = movementUiState.value.destroyerOnPosition.inc(),
                    movingDestroyers = movementUiState.value.movingDestroyers.inc()
                )
            }

            ShipType.GHOST -> {
                _movementUiState.value = _movementUiState.value.copy(
                    ghostToMove = movementUiState.value.ghostToMove.dec(),
                    ghostOnPosition = movementUiState.value.ghostOnPosition.inc(),
                    movingGhosts = movementUiState.value.movingGhosts.inc()
                )
            }

            ShipType.WARPER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    warperToMove = movementUiState.value.warperToMove.dec(),
                    warperOnPosition = movementUiState.value.warperOnPosition.inc(),
                    movingWarpers = movementUiState.value.movingWarpers.inc()
                )
            }
        }
    }

    fun removeShip(shipType: ShipType) {
        when (shipType) {
            ShipType.CRUISER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    cruiserToMove = movementUiState.value.cruiserToMove.inc(),
                    cruiserOnPosition = movementUiState.value.cruiserOnPosition.dec(),
                    movingCruisers = movementUiState.value.movingCruisers.dec()
                )
            }

            ShipType.DESTROYER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    destroyerToMove = movementUiState.value.destroyerToMove.inc(),
                    destroyerOnPosition = movementUiState.value.destroyerOnPosition.dec(),
                    movingDestroyers = movementUiState.value.movingDestroyers.dec()
                )
            }

            ShipType.GHOST -> {
                _movementUiState.value = _movementUiState.value.copy(
                    ghostToMove = movementUiState.value.ghostToMove.inc(),
                    ghostOnPosition = movementUiState.value.ghostOnPosition.dec(),
                    movingGhosts = movementUiState.value.movingGhosts.dec()
                )
            }

            ShipType.WARPER -> {
                _movementUiState.value = _movementUiState.value.copy(
                    warperToMove = movementUiState.value.warperToMove.inc(),
                    warperOnPosition = movementUiState.value.warperOnPosition.dec(),
                    movingWarpers = movementUiState.value.movingWarpers.dec()
                )
            }
        }
    }

    fun undoAttack() {
        if (movementRecord.value.movementRecordOfTurn.isNotEmpty()) {
            val newLocationList: MutableList<Location> =
                locationListUiState.value.locationList.toMutableList()
            val indexOfLastMove = movementRecord.value.movementRecordOfTurn.size - 1
            _movementRecord.value.movementRecordOfTurn[indexOfLastMove].forEach { ship ->
                val shipInIssue: Ship? =
                    locationListUiState.value.locationList[ship.value].myShipList.find { it.id == ship.key }
                if (shipInIssue != null) {
                    shipInIssue.startingPosition?.let {
                        newLocationList[it].myShipList.add(shipInIssue)
                    }
                    shipInIssue.currentPosition?.let {
                        newLocationList[it].myShipList.remove(shipInIssue)
                    }
                    shipInIssue.startingPosition?.let { startingPosition ->
                        newLocationList[startingPosition].myShipList.find { it.id == ship.key }?.currentPosition =
                            shipInIssue.startingPosition
                    }
                    shipInIssue.hasMoved = false
                }
            }
            _locationListUiState.value =
                _locationListUiState.value.copy(locationList = newLocationList)
            val newMovementRecord = movementRecord.value.movementRecordOfTurn
            newMovementRecord.removeAt(indexOfLastMove)
            _movementRecord.value =
                _movementRecord.value.copy(movementRecordOfTurn = newMovementRecord)
        }
    }

    private fun changeWarperPresent(isTrue: Boolean) {
        if (isTrue) {
            _movementUiState.value = _movementUiState.value.copy(isWarperPresent = true)
        } else {
            _movementUiState.value = _movementUiState.value.copy(isWarperPresent = false)
        }
    }

    fun setLocationForInfo(location: Int) {
        _movementUiState.value = _movementUiState.value.copy(locationForInfo = location)
    }

    fun setStartLocationMovementOrder(position: Int) {
        if (checkShipInLocation(position)) {
            changeMovementPosition(isStart = true, position = position)
            if (!isWarperPresent(position)) {
                movementUiState.value.startPosition?.let { getAccessibleConnections(it) }
            } else {
                openAllLocations(position)
            }
        }
    }

    fun setEndLocationMovementOrder(position: Int) {
        if (movementUiState.value.startPosition != null) {
            if (movementUiState.value.startPosition == position) {
                changeMovementPosition(isStart = true, position = null)
                cleanAccessibleLocations()
            } else {
                if (!isWarperPresent(movementUiState.value.startPosition) && checkArmyReach(position = position)) {
                    changeMovementPosition(isStart = false, position = position)
                    changeWarperPresent(isTrue = false)
                    showArmyDialog(toShow = true)
                } else if (isWarperPresent(movementUiState.value.startPosition)) {
                    changeMovementPosition(isStart = false, position = position)
                    changeWarperPresent(isTrue = true)
                    showArmyDialog(toShow = true)
                }
            }
        }
    }

    private fun getAccessibleConnections(location: Int) {
        val connections = locationListUiState.value.locationList[location].getConnectionsList()
        for (i in locationListUiState.value.locationList.indices) {
            if (connections.any {
                    it == i &&
                            locationListUiState.value.locationList[it].myShipList.size < (battleMap?.shipLimitOnPosition
                        ?: 0) &&
                            !checkOwnersRestriction(startLocation = location, endLocation = it)
                }) {
                locationListUiState.value.locationList[i].accessible = true
            }
        }
    }

    private fun checkOwnersRestriction(startLocation: Int, endLocation: Int): Boolean {
        val startLocationOwner = locationListUiState.value.locationList[startLocation].owner.value
        val endLocationOwner = locationListUiState.value.locationList[endLocation].owner.value
        return startLocationOwner == playerData.opponent && endLocationOwner == playerData.opponent
    }

    fun attack() {
        val newLocationList: MutableList<Location> =
            locationListUiState.value.locationList.toMutableList()
        repeat(movementUiState.value.movingCruisers) {
            moveShips(locationList = newLocationList, shipType = ShipType.CRUISER)
        }
        repeat(movementUiState.value.movingDestroyers) {
            moveShips(locationList = newLocationList, shipType = ShipType.DESTROYER)
        }
        repeat(movementUiState.value.movingGhosts) {
            moveShips(locationList = newLocationList, shipType = ShipType.GHOST)
        }
        repeat(movementUiState.value.movingWarpers) {
            moveShips(locationList = newLocationList, shipType = ShipType.WARPER)
        }
        movementUiState.value.endPosition?.let {
            val intValue: Int = movementUiState.value.acceptableLost.toInt()
            newLocationList[it].myAcceptableLost = mutableIntStateOf(intValue)
        }

        _locationListUiState.value =
            _locationListUiState.value.copy(locationList = newLocationList.toList())
        updateRecordsForTurn()
        cleanJustMoved()
        cleanMovementValues()
        changeWarperPresent(isTrue = false)
    }

    fun showShipInfoDialog(toShow: Boolean, shipType: ShipType) {
        if (toShow) {
            when (shipType) {
                ShipType.CRUISER -> _movementUiState.value =
                    _movementUiState.value.copy(showCruiserInfoDialog = true)

                ShipType.DESTROYER -> _movementUiState.value =
                    _movementUiState.value.copy(showDestroyerInfoDialog = true)

                ShipType.GHOST -> _movementUiState.value =
                    _movementUiState.value.copy(showGhostInfoDialog = true)

                ShipType.WARPER -> _movementUiState.value =
                    _movementUiState.value.copy(showWarperInfoDialog = true)
            }
        } else {
            _movementUiState.value = _movementUiState.value.copy(
                showCruiserInfoDialog = false,
                showDestroyerInfoDialog = false,
                showGhostInfoDialog = false,
                showWarperInfoDialog = false
            )
        }
    }

    private fun showArmyDialog(toShow: Boolean) {
        if (toShow) {
            _movementUiState.value = _movementUiState.value.copy(showArmyDialog = true)
        } else {
            _movementUiState.value = _movementUiState.value.copy(showArmyDialog = false)
        }
    }

    fun cleanAfterUnsuccessfulMovement(){
        cleanPositions()
        cleanAccessibleLocations()
    }

    private fun cleanPositions() {
        changeMovementPosition(isStart = true, position = null)
        changeMovementPosition(isStart = false, position = null)
    }

    private fun cleanAccessibleLocations() {
        _locationListUiState.value.locationList.forEach { it.accessible = false }
    }

    private fun cleanJustMoved() {
        movementUiState.value.endPosition?.let {
            _locationListUiState.value.locationList[it].myShipList.forEach { ship ->
                ship.justMoved = false
            }
        }
    }

    /**
     * Write the position user has chosen for army movement.
     * @param isStart if true write a position for starting position, if false write a position for a destination
     * @param position id of position user has chosen
     */
    private fun changeMovementPosition(isStart: Boolean, position: Int?) {
        if (isStart) {
            _movementUiState.value = _movementUiState.value.copy(startPosition = position)
        } else {
            _movementUiState.value = _movementUiState.value.copy(endPosition = position)
        }
    }

    private fun openAllLocations(location: Int) {
        val newLocationList: MutableList<Location> =
            locationListUiState.value.locationList.toMutableList()
        newLocationList.forEach {
            if (it.id != location && checkShipLimitOnPosition(it.id)) it.accessible = true
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    private fun checkShipInLocation(location: Int): Boolean {
        return locationListUiState.value.locationList[location].myShipList.any { it.currentPosition == location && !it.hasMoved }
    }

    private fun isWarperPresent(location: Int?): Boolean {
        return location?.let { locationListUiState.value.locationList[it].myShipList.any { ship -> ship.type == ShipType.WARPER && ship.currentPosition == location } }
            ?: false
    }

    private fun checkArmyReach(position: Int?): Boolean {
        return position?.let { locationListUiState.value.locationList[it].accessible } ?: false
    }

    private fun cleanHasMoved() {
        val newLocationList: MutableList<Location> =
            locationListUiState.value.locationList.toMutableList()
        newLocationList.forEach { location ->
            location.myShipList.forEach { ship ->
                ship.hasMoved = false
            }
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    fun getNumberOfShip(
        locationList: List<Location>,
        location: Int,
        shipType: ShipType,
        isForEnemy: Boolean = false
    ): Int {
        return if (!isForEnemy)
            locationList[location].myShipList.count { it.type == shipType }
        else
            locationList[location].enemyShipList.count { it.type == shipType }
    }

    /**
     * Calculate limit of ships on the position
     * @param location The location of the case
     * @return If is possible to move next ship on the position
     */
    private fun checkShipLimitOnPosition(location: Int?): Boolean {
        val shipsOnPosition =
            location?.let { locationListUiState.value.locationList[it].myShipList.size } ?: 100
        return (battleMap?.shipLimitOnPosition ?: 0) > shipsOnPosition
    }

    fun changeValueAcceptableLost(value: Float) {
        _movementUiState.value = _movementUiState.value.copy(acceptableLost = value)
    }

    private fun cleanMovingShip() {
        _movementUiState.value = _movementUiState.value.copy(
            movingCruisers = 0,
            movingDestroyers = 0,
            movingGhosts = 0,
            movingWarpers = 0
        )
    }

    fun checkShipLimitOnPosition(): Boolean {
        val totalShipsOnPosition: Int
        val shipsAlreadyOnPosition =
            movementUiState.value.endPosition?.let { locationListUiState.value.locationList[it].myShipList.size }
                ?: 100
        val shipsMovingOnPosition =
            movementUiState.value.movingCruisers +
                    movementUiState.value.movingDestroyers +
                    movementUiState.value.movingGhosts +
                    movementUiState.value.movingWarpers
        totalShipsOnPosition = shipsAlreadyOnPosition + shipsMovingOnPosition
        return ((battleMap?.shipLimitOnPosition ?: 0) > totalShipsOnPosition)
    }

    private fun cleanAcceptableLost() {
        _movementUiState.value = _movementUiState.value.copy(acceptableLost = 1.0f)
    }

    fun cleanMovementValues(){
        cleanPositions()
        cleanAccessibleLocations()
        cleanMovingShip()
        cleanAcceptableLost()
        showArmyDialog(toShow = false)
    }
}