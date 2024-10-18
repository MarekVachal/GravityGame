package com.example.gravitygame.ui.screens.battleMapScreen

import android.content.Context
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import com.example.gravitygame.R
import com.example.gravitygame.ai.GameState
import com.example.gravitygame.ai.MCTS
import com.example.gravitygame.database.BattleResult
import com.example.gravitygame.database.DatabaseViewModel
import com.example.gravitygame.maps.BattleMap
import com.example.gravitygame.maps.BattleMapEnum
import com.example.gravitygame.maps.TinyMap
import com.example.gravitygame.models.Cruiser
import com.example.gravitygame.models.Destroyer
import com.example.gravitygame.models.Ghost
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.LocationList
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.Warper
import com.example.gravitygame.timer.TimerViewModel
import com.example.gravitygame.models.PlayerData
import com.example.gravitygame.models.Players
import com.example.gravitygame.ui.utils.calculateBattle
import com.example.gravitygame.ui.utils.MovementRecord
import com.example.gravitygame.ui.screens.selectArmyScreen.SelectArmyUiState
import com.example.gravitygame.ui.utils.BattleResultEnum
import com.example.gravitygame.ui.utils.ProgressIndicatorType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BattleViewModel : ViewModel() {

    private val _movementUiState = MutableStateFlow(MovementUiState())
    val movementUiState: StateFlow<MovementUiState> = _movementUiState.asStateFlow()
    private var _locationListUiState = MutableStateFlow(LocationList())
    val locationListUiState: StateFlow<LocationList> = _locationListUiState.asStateFlow()
    private val _movementRecord = MutableStateFlow(MovementRecord())
    val movementRecord: StateFlow<MovementRecord> = _movementRecord.asStateFlow()
    var battleMap: BattleMap = TinyMap()
    val playerData: PlayerData = PlayerData()
    private val mctsIterations = 500
    private val difficulty = 5

    fun callBattleResultInBattleInfo(result: BattleResultEnum, context: Context): String{
        return when(result){
            BattleResultEnum.WIN -> context.getString(R.string.winBattleResultForBattleInfo)
            BattleResultEnum.LOSE -> context.getString(R.string.lostBattleResultForBattleInfo)
            BattleResultEnum.DRAW -> context.getString(R.string.drawBattleResultForBattleInfo)
        }
    }

    private fun showProgressIndicator(toShow: Boolean, progressIndicatorType: ProgressIndicatorType){
        _movementUiState.value = _movementUiState.value.copy(
            showProgressIndicator = toShow,
            progressIndicatorType = progressIndicatorType
        )
    }

    fun writeDestroyedShips(isSimulation: Boolean, myLostShip: Int, enemyLostShip: Int){
        if (!isSimulation){
            val newEnemyLostShip = movementUiState.value.enemyShipsDestroyed + enemyLostShip
            val newMyLostShip = movementUiState.value.myLostShips + myLostShip
            _movementUiState.value = _movementUiState.value.copy(
                enemyShipsDestroyed = newEnemyLostShip,
                myLostShips = newMyLostShip
            )
        }
    }

    fun getShipImage(shipType: ShipType): Int {
        return when (shipType) {
            ShipType.CRUISER -> R.drawable.cruiser
            ShipType.DESTROYER -> R.drawable.destroyer
            ShipType.GHOST -> R.drawable.ghost
            ShipType.WARPER -> R.drawable.warper
        }
    }

    fun checkRemoveShip(shipType: ShipType): Boolean{
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.value.movingCruisers
            ShipType.DESTROYER -> movementUiState.value.movingDestroyers
            ShipType.GHOST -> movementUiState.value.movingGhosts
            ShipType.WARPER -> movementUiState.value.movingWarpers
        } > 0
    }

    fun checkAddShip(
        isWarperPresent: Boolean,
        shipType: ShipType,
        startLocation: Int?,
        endLocation: Int?
    ): Boolean{
        val isEnabled = checkAddShipAccordingToNumberOfShips(shipType = shipType) && checkAddShipAccordingToMovementRestriction(isWarperPresent = isWarperPresent, endLocation = endLocation, ship = shipType, startLocation = startLocation) && checkShipLimitOnPosition()
        return isEnabled
    }

    private fun checkAddShipAccordingToNumberOfShips(shipType: ShipType): Boolean{
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.value.cruiserToMove
            ShipType.DESTROYER -> movementUiState.value.destroyerToMove
            ShipType.GHOST -> movementUiState.value.ghostToMove
            ShipType.WARPER -> movementUiState.value.warperToMove
        } > 0
    }

    private fun checkAddShipAccordingToMovementRestriction(
        isWarperPresent: Boolean,
        ship: ShipType,
        startLocation: Int?,
        endLocation: Int?
    ): Boolean {
        var isAccesable = false
        if (ship == ShipType.WARPER) {
            isAccesable = true
        } else if (!isWarperPresent) {
            isAccesable = true
        } else {
            val connectionList = startLocation?.let {
                locationListUiState.value.locationList[it].getConnectionsList()
            }
            connectionList?.forEach { if (it == endLocation) isAccesable = true } ?: return false
        }
        return isAccesable
    }

    private fun turnCounter(){
        val count = movementUiState.value.turn + 1
        _movementUiState.value = _movementUiState.value.copy(turn = count)
    }

    fun createMapBoxPositions(location: Int, coordinates: Rect){
        val mapBoxCoordinates: MutableMap<Int, Rect> = movementUiState.value.mapBoxCoordinates.toMutableMap()
        mapBoxCoordinates[location] = coordinates
        _movementUiState.value = _movementUiState.value.copy(mapBoxCoordinates = mapBoxCoordinates.toMap())
    }

    fun createBattleMap(selectedMap: BattleMapEnum) {
        battleMap = when(selectedMap){
            BattleMapEnum.TINY -> TinyMap()
        }
        _locationListUiState.value =
            _locationListUiState.value.copy(locationList = battleMap.locationList)
        playerData.battleMap = selectedMap
    }

    fun createArmyList(selectArmyUiState: SelectArmyUiState) {
        val newLocationList = locationListUiState.value.locationList
        newLocationList.forEach { it.myShipList.clear() }
        newLocationList.forEach { it.enemyShipList.clear() }
        var indexNumber = 0
        val startingLocationIndex = findPlayerBaseLocation()
        val location = newLocationList[startingLocationIndex]
        for (i in 1..selectArmyUiState.numberCruisers) {
            location.myShipList.add(Cruiser(indexNumber))
            indexNumber++
        }
        for (i in 1..selectArmyUiState.numberDestroyers) {
            location.myShipList.add(Destroyer(indexNumber))
            indexNumber++
        }
        for (i in 1..selectArmyUiState.numberGhosts) {
            location.myShipList.add(Ghost(indexNumber))
            indexNumber++
        }
        location.myShipList.add(Warper(indexNumber))
        location.myShipList.forEach { ship ->
            ship.currentPosition = startingLocationIndex
            ship.startingPosition = startingLocationIndex
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    fun findPlayerBaseLocation(): Int {
        return if (playerData.player == Players.PLAYER1){
            battleMap.player1Base
        } else {
            battleMap.player2Base
        }
    }

    private fun findOpponentBaseLocation(): Int{
        return if (playerData.opponent == Players.PLAYER1){
            battleMap.player1Base
        } else {
            battleMap.player2Base
        }
    }

    fun initializeEnemyShipList(enemyShipList: List<Ship>) {
        val startingLocationIndex = findOpponentBaseLocation()
        enemyShipList.forEach { ship ->
            ship.currentPosition = startingLocationIndex
            ship.startingPosition = startingLocationIndex
        }
        _locationListUiState.value.locationList[startingLocationIndex].enemyShipList.addAll(
            enemyShipList
        )

    }

    private fun wasBattleOnLocation(location: Int, wasBattle: Boolean){
        val newLocationList = locationListUiState.value.locationList.toMutableList()
        newLocationList[location].wasBattleHere.value = wasBattle
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList.toList())
    }

    private fun cleanBattleOnLocations(){
        locationListUiState.value.locationList.forEach {
            location -> wasBattleOnLocation(location.id, wasBattle = false) }
    }

    private fun setMapsOfShipsBeforeBattle(location: Int){
        val newLocationList = locationListUiState.value.locationList
        val myOriginalShipList: MutableMap<ShipType, Int> = newLocationList[location].myShipList
            .groupingBy { it.type }
            .eachCount()
            .toMutableMap()
        val enemyOriginalShipList: MutableMap<ShipType, Int> = newLocationList[location].enemyShipList
            .groupingBy { it.type }
            .eachCount()
            .toMutableMap()
        ShipType.entries.forEach { shipType ->
            myOriginalShipList.putIfAbsent(shipType, 0)
            enemyOriginalShipList.putIfAbsent(shipType, 0)
        }
        newLocationList[location].originalMyShipList.clear()
        newLocationList[location].originalMyShipList.putAll(myOriginalShipList)
        newLocationList[location].originalEnemyShipList.clear()
        newLocationList[location].originalEnemyShipList.putAll(enemyOriginalShipList)

        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    private fun conductBattles(){
        val newLocationList = locationListUiState.value.locationList
        _locationListUiState.value.locationList.forEach {
            if (it.myShipList.isNotEmpty() && it.enemyShipList.isNotEmpty()) {
                wasBattleOnLocation(location = it.id, wasBattle = true)
                setMapsOfShipsBeforeBattle(it.id)
                val (player, mapMyLost, mapEnemyLost) = (calculateBattle(
                    location = it,
                    playerData = playerData,
                    isSimulation = false,
                    battleModel = this)
                        )
                when (player) {
                    Players.PLAYER1 -> newLocationList[it.id].owner.value = Players.PLAYER1
                    Players.PLAYER2 -> newLocationList[it.id].owner.value = Players.PLAYER2
                    Players.NONE -> newLocationList[it.id].owner.value = newLocationList[it.id].owner.value
                }
                val newMapMyLost: MutableMap<ShipType, Int> = mapMyLost
                val newMapEnemyLost: MutableMap<ShipType, Int> = mapEnemyLost
                newLocationList[it.id].mapMyLost.clear()
                newLocationList[it.id].mapMyLost.putAll(newMapMyLost)
                newLocationList[it.id].mapEnemyLost.clear()
                newLocationList[it.id].mapEnemyLost.putAll(newMapEnemyLost)
                newLocationList[it.id].lastBattleResult = when(player){
                    Players.PLAYER1 -> {
                        when(playerData.player == Players.PLAYER1){
                            true -> BattleResultEnum.WIN
                            false -> BattleResultEnum.LOSE
                        }
                    }
                    Players.PLAYER2 -> {
                        when(playerData.player == Players.PLAYER1){
                            true -> BattleResultEnum.LOSE
                            false -> BattleResultEnum.WIN
                        }
                    }
                    Players.NONE -> BattleResultEnum.DRAW
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

    }

    private fun cleanEnemyAcceptableLost(){
        val newLocationList = locationListUiState.value.locationList.toMutableList()
        newLocationList.forEach { location -> location.enemyAcceptableLost.intValue = 1 }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList.toList())
    }

    suspend fun finishTurn(timerModel: TimerViewModel, databaseModel: DatabaseViewModel) {
        timerModel.stopTimer()
        cleanBattleOnLocations()
        cleanHasMoved()
        cleanRecordsForTurn()
        cleanMovementValues()
        turnCounter()
        when(playerData.isOnline){
            true -> {
                showProgressIndicator(toShow = true, progressIndicatorType = ProgressIndicatorType.WAITING_FOR_PLAYER)
            }
            false -> {
                showProgressIndicator(toShow = true, progressIndicatorType = ProgressIndicatorType.AI_CALCULATE)
                aiMove()
                conductBattles()
                cleanEnemyAcceptableLost()
            }
        }
        checkEndCondition()
        showProgressIndicator(toShow = true, progressIndicatorType = ProgressIndicatorType.NEW_TURN)
        delay(1000)
        showProgressIndicator(toShow = false, progressIndicatorType = ProgressIndicatorType.NEW_TURN)
        if(!movementUiState.value.endOfGame){
            timerModel.resetTimer()
            timerModel.startTimer()
        } else {
            databaseModel.insertBattleResult(
                BattleResult(
                    result = playerData.playerBattleResult,
                    timestamp = System.currentTimeMillis(),
                    enemyShipDestroyed = movementUiState.value.enemyShipsDestroyed,
                    myShipLost = movementUiState.value.myLostShips,
                    turn = movementUiState.value.turn
                )
            )
            cleanBattleResult()
        }
        /*
        val gameData = GameData()
        gameData.updateLocationList(locationListTrue = locationList, armyList = armyList)
        val jsonData = Json.encodeToString(gameData)
        */
    }

    private fun cleanBattleResult(){
        _movementUiState.value = _movementUiState.value.copy(
            enemyShipsDestroyed = 0,
            myLostShips =  0,
            turn = 0
        )
    }

    private fun initializeGameState(): GameState {
        val locationList = locationListUiState.value.locationList
        val gameMap = battleMap
        val battleModel = this
        return GameState(
            locationList = locationList,
            battleModel = battleModel,
            gameMap = gameMap
        )
    }

    private suspend fun aiMove() {
        val state = initializeGameState()
        val mcts = MCTS(mctsIterations, difficulty)
        val bestMove = mcts.findBestMove(
            initialState = state,
            playerData = playerData
        )
        updateUIWithBestMove(bestMove)
        updateEnemyRecord(bestMove)
    }

    fun cleanEnemyRecord(){
        val newList = movementRecord.value.enemyRecord.toMutableList()
        newList.clear()
        _movementRecord.value = _movementRecord.value.copy(enemyRecord = newList.toList())
    }

    private fun updateEnemyRecord(state: GameState){
        val newList: MutableList<Ship> = mutableListOf()
        state.locationList.forEach {
            location -> location.enemyShipList.forEach {
                ship -> if(ship.startingPosition != ship.currentPosition) newList.add(ship)
            }
        }
        _movementRecord.value = _movementRecord.value.copy(enemyRecord = newList.toList())
    }

    private fun updateUIWithBestMove(state: GameState) {
        _locationListUiState.value =
            _locationListUiState.value.copy(locationList = state.locationList)
    }

    private fun checkEndCondition() {
        val player1Base = battleMap.player1Base
        val player2Base = battleMap.player2Base
        val locationList = locationListUiState.value.locationList

        if (locationList[player1Base].owner.value == Players.PLAYER2 &&
            locationList[player2Base].owner.value == Players.PLAYER1
            ){
            playerData.playerBattleResult = BattleResultEnum.DRAW
            endOfGame()
        } else if (locationList[player1Base].owner.value == Players.PLAYER2 ||
            locationList.all { it.myShipList.isEmpty() }
            ) {
            when(playerData.player){
                Players.PLAYER1 -> playerData.playerBattleResult = BattleResultEnum.LOSE
                Players.PLAYER2 -> playerData.playerBattleResult = BattleResultEnum.WIN
                Players.NONE -> playerData.playerBattleResult = BattleResultEnum.DRAW
            }
            endOfGame()
        } else if (locationList[player2Base].owner.value == Players.PLAYER1 ||
            locationList.all { it.enemyShipList.isEmpty() }
            ) {
            when(playerData.player){
                Players.PLAYER1 -> playerData.playerBattleResult = BattleResultEnum.WIN
                Players.PLAYER2 -> playerData.playerBattleResult = BattleResultEnum.LOSE
                Players.NONE -> playerData.playerBattleResult = BattleResultEnum.DRAW
            }
            endOfGame()
        }
    }

    private fun endOfGame() {
        showEndOfGameDialog(true)
        changeEndOfGameState(true)
    }

    fun changeEndOfGameState(isEnd: Boolean){
        _movementUiState.value = _movementUiState.value.copy(endOfGame = isEnd)
    }

    fun showEndOfGameDialog(toShow: Boolean) {
        _movementUiState.value = _movementUiState.value.copy(showEndOfGameDialog = toShow)
    }

    fun showLocationInfoDialog(toShow: Boolean) {
        _movementUiState.value = _movementUiState.value.copy(showLocationInfoDialog = toShow)
    }

    private fun moveShips(locationList: MutableList<Location>, shipType: ShipType) {
        val shipInIssueNullable: Ship? =
            movementUiState.value.startPosition?.let { startLocation ->
                locationListUiState.value.locationList[startLocation].myShipList.firstOrNull {
                    ship -> ship.type == shipType && !ship.hasMoved
                }
            }
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
        val location = locationListUiState.value.locationList[movementUiState.value.locationForInfo]
        val cruiserOnPosition = location.countShipsByType(ShipType.CRUISER)
        val destroyerOnPosition = location.countShipsByType(ShipType.DESTROYER)
        val ghostOnPosition = location.countShipsByType(ShipType.GHOST)
        val warperOnPosition =location.countShipsByType(ShipType.WARPER)
        val acceptableLost = location.myAcceptableLost.intValue.toFloat()
        _movementUiState.value = _movementUiState.value.copy(
            acceptableLost = acceptableLost,
            cruiserOnPosition = cruiserOnPosition,
            destroyerOnPosition = destroyerOnPosition,
            ghostOnPosition = ghostOnPosition,
            warperOnPosition = warperOnPosition,
        )
    }

    fun closeLocationInfoDialog(){
        showLocationInfoDialog(toShow = false)
        changeAcceptableLost(position = movementUiState.value.locationForInfo)
        cleanAcceptableLost()
    }

    fun initializeArmyDialogValues() {
        var cruiserOnPosition = 0
        var destroyerOnPosition = 0
        var ghostOnPosition = 0
        var warperOnPosition = 0
        movementUiState.value.endPosition?.let {
            val location = locationListUiState.value.locationList[it]
            cruiserOnPosition = location.countShipsByType(ShipType.CRUISER)
            destroyerOnPosition = location.countShipsByType(ShipType.DESTROYER)
            ghostOnPosition = location.countShipsByType(ShipType.GHOST)
            warperOnPosition = location.countShipsByType(ShipType.WARPER)
        }

        var cruiserToMove = 0
        var destroyerToMove = 0
        var ghostToMove = 0
        var warperToMove = 0
        movementUiState.value.startPosition?.let {
            val location = locationListUiState.value.locationList[it]
            cruiserToMove = location.countMovableShips(ShipType.CRUISER)
            destroyerToMove = location.countMovableShips(ShipType.DESTROYER)
            ghostToMove = location.countMovableShips(ShipType.GHOST)
            warperToMove = location.countMovableShips(ShipType.WARPER)
        }

        val acceptableLost = movementUiState.value.endPosition?.let {
            locationListUiState.value.locationList[it].myAcceptableLost.intValue.toFloat()
        } ?: 1.0f

        _movementUiState.value = _movementUiState.value.copy(
            cruiserOnPosition = cruiserOnPosition,
            destroyerOnPosition = destroyerOnPosition,
            ghostOnPosition = ghostOnPosition,
            warperOnPosition = warperOnPosition,
            cruiserToMove = cruiserToMove,
            destroyerToMove = destroyerToMove,
            ghostToMove = ghostToMove,
            warperToMove = warperToMove,
            acceptableLost = acceptableLost
        )
    }

    fun checkToShowBattleInfo(location: Int){
        if(locationListUiState.value.locationList[location].wasBattleHere.value){
            showBattleInfo(location = location, toShow = true)
        } else {
            return
        }
    }

    fun showBattleInfo(location: Int, toShow: Boolean){
        _movementUiState.value = _movementUiState.value.copy(
            showBattleInfoOnLocation = toShow,
            indexOfBattleLocationToShow = location
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

    // Functions for movement records
    private fun updateRecordsForTurn() {
        val indexOfEndLocation = movementUiState.value.endPosition
        val movementMap: MutableMap<Ship, Int> = mutableMapOf()
        val movementRecord: MutableList<Map<Ship, Int>> = movementRecord.value.movementRecordOfTurn.toMutableList()
        indexOfEndLocation?.let {
            locationListUiState.value.locationList[it].myShipList
                .forEach { ship ->
                    if (ship.justMoved)
                        movementMap[ship] = indexOfEndLocation
                }
        }
        movementRecord.add(movementMap.toMap())
        _movementRecord.value = _movementRecord.value.copy(movementRecordOfTurn = movementRecord.toList())
    }

    private fun cleanRecordsForTurn() {
        val newRecord = movementRecord.value.movementRecordOfTurn.toMutableList()
        newRecord.clear()
        _movementRecord.value = _movementRecord.value.copy(movementRecordOfTurn = newRecord.toList())
    }

    fun undoAttack() {
        if (movementRecord.value.movementRecordOfTurn.isNotEmpty()) {
            val newLocationList: MutableList<Location> = locationListUiState.value.locationList.toMutableList()
            val indexOfLastMove = movementRecord.value.movementRecordOfTurn.size - 1
            movementRecord.value.movementRecordOfTurn[indexOfLastMove].forEach { map ->
                val ship = map.key
                ship.startingPosition?.let {newLocationList[it].myShipList.add(ship)}
                ship.currentPosition?.let {newLocationList[it].myShipList.remove(ship)}
                ship.currentPosition = ship.startingPosition
                ship.hasMoved = false
            }
            _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)

            val newMovementRecord = movementRecord.value.movementRecordOfTurn.toMutableList()
            newMovementRecord.removeAt(indexOfLastMove)
            _movementRecord.value = _movementRecord.value.copy(movementRecordOfTurn = newMovementRecord.toList())

        }
    }

    fun getNumberShipsForRecord(
        shipType: ShipType,
        location1: Int,
        location2: Int,
        isMyRecord: Boolean
    ): Int {
        val listOfMove =
            if(isMyRecord){
                movementRecord.value.movementRecordOfTurn.flatMap {
                    map -> map.filter { it.value == location2 || it.value == location1 }.keys }
            } else {
                movementRecord.value.enemyRecord.filter {
                    (it.currentPosition == location2 && it.currentPosition != it.startingPosition) ||
                    (it.currentPosition == location1 && it.currentPosition != it.startingPosition)
                }
            }
        val shipListLocation2 = if(isMyRecord){
            locationListUiState.value.locationList[location2].myShipList
        } else {
            movementRecord.value.enemyRecord.filter { it.currentPosition == location2 || it.startingPosition == location2}
        }
        val size2 = shipListLocation2.filter { ship -> ship in listOfMove && ship.type == shipType && ship.currentPosition == location2 && ship.startingPosition == location1 }.size
        val shipListLocation1 = if(isMyRecord) {
            locationListUiState.value.locationList[location1].myShipList
        } else {
            movementRecord.value.enemyRecord.filter { it.currentPosition == location1 || it.startingPosition == location1 }
        }
        val size1 = shipListLocation1.filter { ship -> ship in listOfMove && ship.type == shipType && ship.currentPosition == location1 && ship.startingPosition == location2}.size
        return size1 + size2
    }

    private fun changeWarperPresent(isPresent: Boolean) {
        _movementUiState.value = _movementUiState.value.copy(isWarperPresent = isPresent)
    }

    fun setLocationForInfo(location: Int) {
        _movementUiState.value = _movementUiState.value.copy(locationForInfo = location)
    }

    fun setStartLocationMovementOrder(position: Int) {
        if (isAnyShipInLocation(position)) {
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
                    changeWarperPresent(isPresent = false)
                    showArmyDialog(toShow = true)
                } else if (isWarperPresent(movementUiState.value.startPosition)) {
                    changeMovementPosition(isStart = false, position = position)
                    changeWarperPresent(isPresent = true)
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
                locationListUiState.value.locationList[it].myShipList.size < (battleMap.shipLimitOnPosition) &&
                !checkOwnersRestriction(startLocation = location, endLocation = it)
                }
            ) {
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
        _locationListUiState.value =
            _locationListUiState.value.copy(locationList = newLocationList.toList())
        movementUiState.value.endPosition?.let { changeAcceptableLost(position = it) }
        updateRecordsForTurn()
        cleanJustMoved()
        cleanMovementValues()
        changeWarperPresent(isPresent = false)
    }

    private fun changeAcceptableLost(position: Int){
        val newLocationList: MutableList<Location> = locationListUiState.value.locationList.toMutableList()
        val intValue: Int = movementUiState.value.acceptableLost.toInt()
        newLocationList[position].myAcceptableLost.intValue = intValue
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList.toList())
    }

    fun changeValueAcceptableLost(value: Float) {
        _movementUiState.value = _movementUiState.value.copy(acceptableLost = value)
    }

    fun changeShipTypeToShow(shipType: ShipType){
        _movementUiState.value = _movementUiState.value.copy(shipTypeToShow = shipType)
    }

    fun showShipInfoDialog(toShow: Boolean){
        _movementUiState.value = _movementUiState.value.copy(showShipInfoDialog = toShow)
    }

    private fun showArmyDialog(toShow: Boolean) {
        _movementUiState.value = _movementUiState.value.copy(showArmyDialog = toShow)
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
            if (it.id != location && canAddMoreShips(it.id)) it.accessible = true
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    private fun isAnyShipInLocation(location: Int): Boolean {
        return locationListUiState.value.locationList[location].myShipList.any { it.currentPosition == location && !it.hasMoved }
    }

    private fun isWarperPresent(location: Int?): Boolean {
        return location?.let {
            locationListUiState.value.locationList[it].myShipList.any {
                ship -> ship.type == ShipType.WARPER && ship.currentPosition == location
            }
        } ?: false
    }

    private fun checkArmyReach(position: Int?): Boolean {
        return position?.let { locationListUiState.value.locationList[it].accessible } ?: false
    }

    private fun cleanHasMoved() {
        val newLocationList = locationListUiState.value.locationList
        newLocationList.forEach { location ->
            location.myShipList.forEach { ship ->
                ship.hasMoved = false
            }
        }
        _locationListUiState.value = _locationListUiState.value.copy(locationList = newLocationList)
    }

    fun getNumberOfShip(
        location: Int?,
        shipType: ShipType,
        isForEnemy: Boolean = false
    ): Int {
        return location?.let {
            if(!isForEnemy){
                locationListUiState.value.locationList[it].countShipsByType(shipType = shipType)
            } else {
                locationListUiState.value.locationList[it].countEnemyShipsByType(shipType = shipType)
            }
        } ?: 0
    }

    private fun canAddMoreShips(location: Int?): Boolean {
        return location?.let {
            val locationInstance = locationListUiState.value.locationList[it]
            locationInstance.canAddMoreShips(battleMap.shipLimitOnPosition)
        } ?: false
    }
    
    fun hasExceededShipLimit(location: Int?): Boolean{
        return location?.let { 
            val locationInstance = locationListUiState.value.locationList[it]
            locationInstance.hasExceededShipLimit(battleMap.shipLimitOnPosition)
        } ?: false
    }

    private fun cleanMovingShip() {
        _movementUiState.value = _movementUiState.value.copy(
            movingCruisers = 0,
            movingDestroyers = 0,
            movingGhosts = 0,
            movingWarpers = 0
        )
    }

    private fun checkShipLimitOnPosition(): Boolean {
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
        return battleMap.shipLimitOnPosition > totalShipsOnPosition
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

    fun setShipsOnPositionString(shipType: ShipType, movementUiState: MovementUiState): String{
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.cruiserOnPosition.toString()
            ShipType.DESTROYER -> movementUiState.destroyerOnPosition.toString()
            ShipType.GHOST -> movementUiState.ghostOnPosition.toString()
            ShipType.WARPER -> movementUiState.warperOnPosition.toString()
        }
    }

    fun setShipsToMoveString(shipType: ShipType, movementUiState: MovementUiState): String{
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.cruiserToMove.toString()
            ShipType.DESTROYER -> movementUiState.destroyerToMove.toString()
            ShipType.GHOST -> movementUiState.ghostToMove.toString()
            ShipType.WARPER -> movementUiState.warperToMove.toString()
        }
    }

    fun setEndOfGameText(isTitle: Boolean, context: Context): String{
        val string = if(isTitle){
            when(playerData.playerBattleResult){
                BattleResultEnum.WIN -> context.getString(R.string.titleWinGame)
                BattleResultEnum.LOSE -> context.getString(R.string.titleLostGame)
                BattleResultEnum.DRAW -> context.getString(R.string.titleDrawGame)
            }
        } else {
            when(playerData.playerBattleResult){
                BattleResultEnum.WIN -> context.getString(R.string.winGame)
                BattleResultEnum.LOSE -> context.getString(R.string.lostGame)
                BattleResultEnum.DRAW -> context.getString(R.string.drawGame)
            }
        }
        return string
    }
}