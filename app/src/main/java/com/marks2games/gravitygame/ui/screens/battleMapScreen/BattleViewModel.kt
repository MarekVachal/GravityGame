package com.marks2games.gravitygame.ui.screens.battleMapScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ai.GameState
import com.marks2games.gravitygame.ai.MCTS
import com.marks2games.gravitygame.database.BattleResult
import com.marks2games.gravitygame.database.DatabaseViewModel
import com.marks2games.gravitygame.firebase.BattleGameRepositoryFactory
import com.marks2games.gravitygame.firebase.models.SimplifiedMove
import com.marks2games.gravitygame.models.toShipType
import com.marks2games.gravitygame.maps.BattleMap
import com.marks2games.gravitygame.models.BattleMapEnum
import com.marks2games.gravitygame.maps.TinyMap
import com.marks2games.gravitygame.models.Cruiser
import com.marks2games.gravitygame.models.Destroyer
import com.marks2games.gravitygame.models.EndOfGameType
import com.marks2games.gravitygame.models.Ghost
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.LocationList
import com.marks2games.gravitygame.models.Ship
import com.marks2games.gravitygame.models.ShipType
import com.marks2games.gravitygame.models.Warper
import com.marks2games.gravitygame.timer.TimerViewModel
import com.marks2games.gravitygame.models.PlayerData
import com.marks2games.gravitygame.models.Players
import com.marks2games.gravitygame.ui.utils.calculateBattle
import com.marks2games.gravitygame.models.MovementRecord
import com.marks2games.gravitygame.models.SharedPlayerDataRepository
import com.marks2games.gravitygame.ui.screens.selectArmyScreen.SelectArmyUiState
import com.marks2games.gravitygame.models.BattleResultEnum
import com.marks2games.gravitygame.models.ProgressIndicatorType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sentry.Sentry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val sharedPlayerModel: SharedPlayerDataRepository,
    private val battleGameRepositoryFactory: BattleGameRepositoryFactory
): ViewModel() {

    private val _movementUiState = MutableStateFlow(MovementUiState())
    val movementUiState: StateFlow<MovementUiState> = _movementUiState.asStateFlow()
    private var _locationListUiState = MutableStateFlow(LocationList())
    val locationListUiState: StateFlow<LocationList> = _locationListUiState.asStateFlow()
    private val _movementRecord = MutableStateFlow(MovementRecord())
    val movementRecord: StateFlow<MovementRecord> = _movementRecord.asStateFlow()
    val playerData: StateFlow<PlayerData> = sharedPlayerModel.playerData
    var battleMap: BattleMap = when(playerData.value.battleMap){
        BattleMapEnum.TINY -> TinyMap()
    }
    private val mctsIterations = 500
    private val difficulty = 5

    fun showTimer(toShow: Boolean){
        _movementUiState.update { state ->
            state.copy(
                showTimer = toShow
            )
        }
    }

    fun isMyLocation(location: Int): Boolean?{
        val locationOwner = locationListUiState.value.locationList[location].owner.value
        return when(locationOwner){
            sharedPlayerModel.playerData.value.player -> true
            sharedPlayerModel.playerData.value.opponent -> false
            else -> null
        }
    }

    fun initializeBattleGameRepository(playerData: PlayerData){
        _movementUiState.update { state ->
            state.copy(
                battleGameRepository = battleGameRepositoryFactory.create(playerData)
            )
        }
    }

    suspend fun updateLocations(
        isSetup: Boolean,
        timerModel: TimerViewModel,
        databaseModel: DatabaseViewModel
    ) {
        movementUiState.value.battleGameRepository?.sendUpdatedLocations(
            updatedLocations = locationListUiState.value.locationList,
            onOpponentReady = { newLocationList ->
                updateOpponentData(simplifiedMove = newLocationList, isSetup = isSetup) },
            timeoutMillis = getTimeoutMillis(timerModel = timerModel),
            onOpponentDisconnected = {
                sharedPlayerModel.updateBattleResult(BattleResultEnum.WIN)
                endOfGame(
                    timerModel = timerModel,
                    isCapitulation = false,
                    isOpponentDisconnected = true,
                    databaseModel = databaseModel,
                )
            }
            )?: return
    }

    private fun updateOpponentData(simplifiedMove: SimplifiedMove, isSetup: Boolean) {
        val newList = locationListUiState.value.locationList
        if (!isSetup) {
            simplifiedMove.acceptableLost.forEach { record ->
                newList[record.locationId].enemyAcceptableLost.intValue = record.lostValue
            }
            simplifiedMove.simplifiedShipList.forEach { ship ->
                val shipInIssue = newList[ship.startingPosition!!].enemyShipList.find { foundShip ->
                    ship.id == foundShip.id
                }
                if (shipInIssue != null) {
                    newList[ship.currentPosition!!].enemyShipList.add(shipInIssue)
                    newList[ship.startingPosition!!].enemyShipList.remove(shipInIssue)
                    shipInIssue.startingPosition = ship.currentPosition
                }
            }
        } else {
            simplifiedMove.simplifiedShipList.forEach { ship ->
                val createdShip = when (ship.shipType.toShipType()) {
                    ShipType.CRUISER -> Cruiser(ship.id)
                    ShipType.DESTROYER -> Destroyer(ship.id)
                    ShipType.GHOST -> Ghost(ship.id)
                    ShipType.WARPER -> Warper(ship.id)
                    null -> return
                }
                createdShip.currentPosition = ship.currentPosition
                createdShip.startingPosition = ship.startingPosition
                newList[ship.startingPosition!!].enemyShipList.add(createdShip)
            }
        }
        _locationListUiState.update { state ->
            state.copy(locationList = newList)
        }
    }

    private fun getTimeoutMillis(timerModel: TimerViewModel): Long{
        val minute = timerModel.timerUiState.value.minute ?: 0
        val second = timerModel.timerUiState.value.second ?: 0
        return ((minute * 60 + second) * 1000) + 2000L
    }

    fun isOnlineGame(isOnline: Boolean) {
        sharedPlayerModel.updateIsOnline(isOnline)
    }

    private fun updateBattleMap(battleMap: BattleMapEnum) {
        sharedPlayerModel.updateBattleMap(battleMap)
    }

    fun onDragStart(offset: Offset, location: Int) {
        if (isAnyShipInLocation(location = location)) {
            val correctionSize = battleMap.planetSize.value
            val rect = movementUiState.value.mapBoxCoordinates[location]
            val correctionY = (rect?.top ?: 0.0f) - correctionSize
            val correctionX = (rect?.left ?: 0.0f) - correctionSize
            _movementUiState.update { state ->
                state.copy(
                    iconPositionX = offset.x + correctionX,
                    iconPositionY = offset.y + correctionY
                )
            }
            setStartLocationMovementOrder(location)
        }
    }


    fun onDragEnd() {
        val offset = Offset(
            movementUiState.value.iconPositionX, movementUiState.value.iconPositionY
        )
        val endLocation = determineLocationFromOffset(
            offset, movementUiState.value.mapBoxCoordinates
        )
        endLocation?.let {
            setEndLocationMovementOrder(it)
        } ?: cleanAfterUnsuccessfulMovement()
        changeDraggingIconVisible(false)
    }

    fun onDrag(dragAmount: Offset) {
        _movementUiState.update { state ->
            state.copy(
                iconPositionX = dragAmount.x + movementUiState.value.iconPositionX,
                iconPositionY = dragAmount.y + movementUiState.value.iconPositionY
            )
        }
    }

    fun createMapBoxPosition(location: Int, coordinates: Rect) {
        val mapBoxCoordinates = movementUiState.value.mapBoxCoordinates.toMutableMap()
        mapBoxCoordinates[location] = coordinates
        _movementUiState.update { state ->
            state.copy(mapBoxCoordinates = mapBoxCoordinates.toMap())
        }
    }

    private fun determineLocationFromOffset(
        offset: Offset,
        mapBoxPositions: Map<Int, Rect>
    ): Int? {
        return mapBoxPositions.entries.firstOrNull { (_, rect) -> rect.contains(offset) }?.key
    }

    private fun changeDraggingIconVisible(isVisible: Boolean) {
        _movementUiState.update { state ->
            state.copy(
                draggingIconVisible = isVisible
            )
        }
    }

    private fun setStartLocationMovementOrder(position: Int) {
        changeMovementPosition(isStart = true, position = position)
        if (!isWarperPresent(position)) {
            movementUiState.value.startPosition?.let {
                getAccessibleConnections(it)
                changeDraggingIconVisible(true)
            }
        } else {
            openAllLocations(position)
            changeDraggingIconVisible(true)
        }
    }

    private fun setEndLocationMovementOrder(position: Int) {
        if (movementUiState.value.startPosition != null) {
            if (movementUiState.value.startPosition == position) {
                cleanAfterUnsuccessfulMovement()
            } else {
                if (!isWarperPresent(movementUiState.value.startPosition) && checkArmyReach(position = position)) {
                    changeMovementPosition(isStart = false, position = position)
                    changeWarperPresent(isPresent = false)
                    showArmyDialog(toShow = true)
                } else if (isWarperPresent(movementUiState.value.startPosition)) {
                    changeMovementPosition(isStart = false, position = position)
                    changeWarperPresent(isPresent = true)
                    showArmyDialog(toShow = true)
                } else {
                    cleanAfterUnsuccessfulMovement()
                }
            }
        }
    }

    fun callBattleResultInBattleInfo(result: BattleResultEnum, context: Context): String {
        return when (result) {
            BattleResultEnum.WIN -> context.getString(R.string.winBattleResultForBattleInfo)
            BattleResultEnum.LOSE -> context.getString(R.string.lostBattleResultForBattleInfo)
            BattleResultEnum.DRAW -> context.getString(R.string.drawBattleResultForBattleInfo)
        }
    }

    private fun showProgressIndicator(toShow: Boolean, progressIndicatorType: ProgressIndicatorType) {
        _movementUiState.update { state ->
            state.copy(
                showProgressIndicator = toShow,
                progressIndicatorType = progressIndicatorType
            )
        }
    }

    fun showCapitulateInfoDialog(toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(showCapitulateInfoDialog = toShow)
        }
    }

    fun writeDestroyedShips(isSimulation: Boolean, myLostShip: Int, enemyLostShip: Int) {
        if (!isSimulation) {
            val newEnemyLostShip = movementUiState.value.enemyShipsDestroyed + enemyLostShip
            val newMyLostShip = movementUiState.value.myLostShips + myLostShip
            _movementUiState.update { state ->
                state.copy(
                    enemyShipsDestroyed = newEnemyLostShip,
                    myLostShips = newMyLostShip
                )
            }
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

    fun checkRemoveShip(shipType: ShipType): Boolean {
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
    ): Boolean {
        return checkAddShipAccordingToNumberOfShips(shipType = shipType) &&
                checkAddShipAccordingToMovementRestriction(
                    isWarperPresent = isWarperPresent,
                    endLocation = endLocation,
                    ship = shipType,
                    startLocation = startLocation
                ) &&
                checkShipLimitOnPosition()
    }

    private fun checkAddShipAccordingToNumberOfShips(shipType: ShipType): Boolean {
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
        if (ship == ShipType.WARPER) {
            return true
        } else if (!isWarperPresent) {
            return true
        } else {
            val connectionList = startLocation?.let {
                locationListUiState.value.locationList[it].getConnectionsList()
            }
            connectionList?.forEach { if (it == endLocation && !checkOwnersRestriction(startLocation, endLocation)) return true } ?: return false
        }
        return false
    }

    private fun turnCounter() {
        val count = movementUiState.value.turn + 1
        _movementUiState.update { state ->
            state.copy(turn = count)
        }
    }

    fun createBattleMap(selectedMap: BattleMapEnum) {
        battleMap = when (selectedMap) {
            BattleMapEnum.TINY -> TinyMap()
        }
        _locationListUiState.update { state ->
            state.copy(locationList = battleMap.locationList)
        }
        _locationListUiState.value.locationList[battleMap.player1Base].owner.value = Players.PLAYER1
        _locationListUiState.value.locationList[battleMap.player2Base].owner.value = Players.PLAYER2
        updateBattleMap(selectedMap)
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
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList)
        }
    }

    private fun findPlayerBaseLocation(): Int {
        return if (sharedPlayerModel.playerData.value.player == Players.PLAYER1) {
            battleMap.player1Base
        } else {
            battleMap.player2Base
        }
    }

    fun findOpponentBaseLocation(): Int {
        return if (sharedPlayerModel.playerData.value.opponent == Players.PLAYER1) {
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

    private fun wasBattleOnLocation(location: Int, wasBattle: Boolean) {
        val newLocationList = locationListUiState.value.locationList.toMutableList()
        newLocationList[location].wasBattleHere.value = wasBattle
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList.toList())
        }
    }

    private fun cleanBattleOnLocations() {
        locationListUiState.value.locationList.forEach { location ->
            wasBattleOnLocation(
                location.id,
                wasBattle = false
            )
        }
    }

    private fun setMapsOfShipsBeforeBattle(location: Int) {
        val newLocationList = locationListUiState.value.locationList
        val myOriginalShipList: MutableMap<ShipType, Int> = newLocationList[location].myShipList
            .groupingBy { it.type }
            .eachCount()
            .toMutableMap()
        val enemyOriginalShipList: MutableMap<ShipType, Int> =
            newLocationList[location].enemyShipList
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

        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList)
        }
    }

    private fun conductBattles() {
        val newLocationList = locationListUiState.value.locationList
        _locationListUiState.value.locationList.forEach {
            if (it.myShipList.isNotEmpty() && it.enemyShipList.isNotEmpty()) {
                wasBattleOnLocation(location = it.id, wasBattle = true)
                setMapsOfShipsBeforeBattle(it.id)
                val (winner, myLostShips, enemyLostShips) = (calculateBattle(
                    location = it,
                    playerData = sharedPlayerModel.playerData.value,
                    isSimulation = false,
                    battleModel = this
                )
                        )
                when (winner) {
                    Players.PLAYER1 -> newLocationList[it.id].owner.value = Players.PLAYER1
                    Players.PLAYER2 -> newLocationList[it.id].owner.value = Players.PLAYER2
                    Players.NONE -> newLocationList[it.id].owner.value =
                        newLocationList[it.id].owner.value
                }
                newLocationList[it.id].mapMyLost.clear()
                newLocationList[it.id].mapMyLost.putAll(myLostShips)
                newLocationList[it.id].mapEnemyLost.clear()
                newLocationList[it.id].mapEnemyLost.putAll(enemyLostShips)
                newLocationList[it.id].lastBattleResult = when (winner) {
                    Players.PLAYER1 -> {
                        when (sharedPlayerModel.playerData.value.player == Players.PLAYER1) {
                            true -> BattleResultEnum.WIN
                            false -> BattleResultEnum.LOSE
                        }
                    }

                    Players.PLAYER2 -> {
                        when (sharedPlayerModel.playerData.value.player == Players.PLAYER1) {
                            true -> BattleResultEnum.LOSE
                            false -> BattleResultEnum.WIN
                        }
                    }

                    Players.NONE -> BattleResultEnum.DRAW
                }
            }
            if (it.myShipList.isNotEmpty() && it.enemyShipList.isEmpty() && it.owner.value != sharedPlayerModel.playerData.value.player) {
                newLocationList[it.id].owner.value = sharedPlayerModel.playerData.value.player
            }
            if (it.myShipList.isEmpty() && it.enemyShipList.isNotEmpty() && it.owner.value != sharedPlayerModel.playerData.value.opponent) {
                newLocationList[it.id].owner.value = sharedPlayerModel.playerData.value.opponent
            }
        }
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList)
        }
    }

    private fun cleanEnemyAcceptableLost() {
        val newLocationList = locationListUiState.value.locationList.toMutableList()
        newLocationList.forEach { location -> location.enemyAcceptableLost.intValue = 1 }
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList.toList())
        }
    }

    suspend fun setOnClickButtonNextTurn(
        navigateToMainMenuScreen: () -> Unit,
        context: Context,
        timerModel: TimerViewModel,
        databaseModel: DatabaseViewModel
    ) {
        if (!movementUiState.value.endOfGame) {
            if (hasExceededShipLimit(findPlayerBaseLocation())) {
                Toast.makeText(
                    context,
                    context.getString(R.string.manyShipsOnBaseLocation),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                finishTurn(
                    timerModel = timerModel,
                    databaseModel = databaseModel
                )

            }
        } else {
            navigateToMainMenuScreen()
            showEndOfGameDialog(false)
            changeEndOfGameState(false)
            showCapitulateInfoDialog(false)
        }
    }

    fun resetUiStateForNewBattle() {
        showProgressIndicator(false, ProgressIndicatorType.NEW_TURN)
        changeEndOfGameState(false)
        showCapitulateInfoDialog(false)
        showEndOfGameDialog(false)
    }

    fun setOnClickButtonNextTurnText(context: Context): String {
        return if (!movementUiState.value.endOfGame) {
            context.getString(R.string.nextTurn)
        } else {
            context.getString(R.string.exit)
        }
    }

    suspend fun finishTurn(timerModel: TimerViewModel, databaseModel: DatabaseViewModel) {
        timerModel.stopTimer()
        cleanBattleOnLocations()
        cleanHasMoved()
        cleanRecordsForTurn()
        cleanMovementValues()
        if(movementUiState.value.turn == 0){
            randomMoveForFirstTurn()
        }
        turnCounter()
        if (playerData.value.isOnline) {
            showProgressIndicator(
                toShow = true,
                progressIndicatorType = ProgressIndicatorType.WAITING_FOR_MOVE
            )
            try {
                updateLocations(
                    isSetup = false,
                    timerModel = timerModel,
                    databaseModel = databaseModel
                )
                conductBattles()
            } catch (e: Exception) {
                Sentry.captureException(e)
                return
            }
        } else {
            showProgressIndicator(
                toShow = true,
                progressIndicatorType = ProgressIndicatorType.AI_CALCULATE
            )
            aiMove()
            conductBattles()
            cleanEnemyAcceptableLost()
        }
        checkEndCondition(timerModel = timerModel, databaseModel = databaseModel)
        if (!movementUiState.value.endOfGame) {
            showNewTurnDialog()
            timerModel.resetTimer()
            timerModel.startTimer()
        }
    }

    private suspend fun showNewTurnDialog(){
        showProgressIndicator(
            toShow = true,
            progressIndicatorType = ProgressIndicatorType.NEW_TURN
        )
        delay(1000)
        showProgressIndicator(
            toShow = false,
            progressIndicatorType = ProgressIndicatorType.NEW_TURN
        )
    }

    private fun writeDataToDatabase(databaseModel: DatabaseViewModel) {
        databaseModel.insertBattleResult(
            BattleResult(
                result = sharedPlayerModel.playerData.value.playerBattleResult,
                timestamp = System.currentTimeMillis(),
                enemyShipDestroyed = movementUiState.value.enemyShipsDestroyed,
                myShipLost = movementUiState.value.myLostShips,
                turn = movementUiState.value.turn
            )
        )
        cleanBattleResult()
    }

    private fun cleanBattleResult() {
        _movementUiState.value = _movementUiState.value.copy(
            enemyShipsDestroyed = 0,
            myLostShips = 0,
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
            playerData = sharedPlayerModel.playerData.value
        )
        updateUIWithBestMove(bestMove)
        updateEnemyRecord(bestMove)
    }

    fun cleanEnemyRecord() {
        val newList = movementRecord.value.enemyRecord.toMutableList()
        newList.clear()
        _movementRecord.update { state ->
            state.copy(enemyRecord = newList.toList())
        }
    }

    private fun updateEnemyRecord(gameState: GameState) {
        val newList: MutableList<Ship> = mutableListOf()
        gameState.locationList.forEach { location ->
            location.enemyShipList.forEach { ship ->
                if (ship.startingPosition != ship.currentPosition) newList.add(ship)
            }
        }
        _movementRecord.update { state ->
            state.copy(enemyRecord = newList.toList())
        }
    }

    private fun updateUIWithBestMove(gameState: GameState) {
        _locationListUiState.update { state ->
            state.copy(locationList = gameState.locationList)
        }
    }

    private fun checkEndCondition(timerModel: TimerViewModel, databaseModel: DatabaseViewModel) {
        val player: Players
        val opponent: Players
        val playerBase: Int
        val opponentBase: Int
        when(sharedPlayerModel.playerData.value.player) {
            Players.PLAYER1 -> {
                player = Players.PLAYER1
                opponent = Players.PLAYER2
                playerBase = battleMap.player1Base
                opponentBase = battleMap.player2Base
            }
            Players.PLAYER2 -> {
                player = Players.PLAYER2
                opponent = Players.PLAYER1
                playerBase = battleMap.player2Base
                opponentBase = battleMap.player1Base
            }
            else -> return
        }
        val locationList = locationListUiState.value.locationList

        if (locationList[playerBase].owner.value == opponent &&
            locationList[opponentBase].owner.value == player
        ) {
            sharedPlayerModel.updateBattleResult(BattleResultEnum.DRAW)
            endOfGame(
                timerModel = timerModel,
                isCapitulation = false,
                isOpponentDisconnected = false,
                databaseModel = databaseModel
            )
        } else if (locationList[playerBase].owner.value == opponent ||
            locationList.all { it.myShipList.isEmpty() } ) {
            sharedPlayerModel.updateBattleResult(BattleResultEnum.LOSE)
            endOfGame(
                timerModel = timerModel,
                isCapitulation = false,
                isOpponentDisconnected = false,
                databaseModel = databaseModel
            )
        } else if (locationList[opponentBase].owner.value == player ||
            locationList.all { it.enemyShipList.isEmpty() }) {
            sharedPlayerModel.updateBattleResult(BattleResultEnum.WIN)
            endOfGame(
                timerModel = timerModel,
                isCapitulation = false,
                isOpponentDisconnected = false,
                databaseModel = databaseModel
            )
        }
    }

    fun capitulate(timerModel: TimerViewModel, databaseModel: DatabaseViewModel) {
        sharedPlayerModel.updateBattleResult(BattleResultEnum.LOSE)
        timerModel.cancelTimer()
        movementUiState.value.battleGameRepository?.capitulate() ?: return
        endOfGame(
            timerModel = timerModel,
            isCapitulation = true,
            isOpponentDisconnected = false,
            databaseModel = databaseModel
        )
    }

    fun listenForCapitulation(timerModel: TimerViewModel, databaseModel: DatabaseViewModel) {
        movementUiState.value.battleGameRepository?.listenForCapitulation {
                sharedPlayerModel.updateBattleResult(BattleResultEnum.WIN)
                endOfGame(
                    timerModel = timerModel,
                    isCapitulation = true,
                    isOpponentDisconnected = false,
                    databaseModel = databaseModel
                )
            }

    }

    private fun changeEndOfGameType(endOfGameType: EndOfGameType){
        _movementUiState.update { state ->
            state.copy(endOfGameType = endOfGameType)
        }
    }

    private fun endOfGame(
        timerModel: TimerViewModel,
        isCapitulation: Boolean,
        isOpponentDisconnected: Boolean,
        databaseModel: DatabaseViewModel
    ) {
        if (isCapitulation) {
            changeEndOfGameType(EndOfGameType.CAPITULATION)
            showEndOfGameDialog(true)
        } else if (isOpponentDisconnected){
            changeEndOfGameType(EndOfGameType.DISCONNECTED)
            showEndOfGameDialog(true)
        } else {
            changeEndOfGameType(EndOfGameType.REGULAR)
            showEndOfGameDialog(true)
        }
        changeEndOfGameState(true)
        showProgressIndicator(false, ProgressIndicatorType.NEW_TURN)
        writeDataToDatabase(databaseModel)
        movementUiState.value.battleGameRepository?.deleteRoom(isOpponentDisconnected)?: return
        timerModel.cancelTimer()
    }

    private fun changeEndOfGameState(isEnd: Boolean) {
        _movementUiState.update { state ->
            state.copy(endOfGame = isEnd)
        }
    }

    fun showEndOfGameDialog(toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(showEndOfGameDialog = toShow)
        }
    }

    fun showLocationInfoDialog(toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(showLocationInfoDialog = toShow)
        }
    }

    private fun moveShips(locationList: MutableList<Location>, shipType: ShipType) {
        val shipInIssueNullable: Ship? =
            movementUiState.value.startPosition?.let { startLocation ->
                locationListUiState.value.locationList[startLocation].myShipList.firstOrNull { ship ->
                    ship.type == shipType && !ship.hasMoved
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
        val warperOnPosition = location.countShipsByType(ShipType.WARPER)
        val acceptableLost = location.myAcceptableLost.intValue.toFloat()
        _movementUiState.update { state ->
            state.copy(acceptableLost = acceptableLost,
                cruiserOnPosition = cruiserOnPosition,
                destroyerOnPosition = destroyerOnPosition,
                ghostOnPosition = ghostOnPosition,
                warperOnPosition = warperOnPosition
                )
        }
    }

    fun closeLocationInfoDialog() {
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

        _movementUiState.update { state ->
            state.copy(cruiserOnPosition = cruiserOnPosition,
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
    }

    fun checkToShowBattleInfo(location: Int) {
        if (locationListUiState.value.locationList[location].wasBattleHere.value) {
            showBattleInfo(location = location, toShow = true)
        } else {
            return
        }
    }

    fun showBattleInfo(location: Int, toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(
                showBattleInfoOnLocation = toShow,
                indexOfBattleLocationToShow = location
            )
        }
    }

    fun addShip(shipType: ShipType) {
        when (shipType) {
            ShipType.CRUISER -> {
                _movementUiState.update { state ->
                    state.copy(cruiserToMove = movementUiState.value.cruiserToMove.dec(),
                        cruiserOnPosition = movementUiState.value.cruiserOnPosition.inc(),
                        movingCruisers = movementUiState.value.movingCruisers.inc())
                }
            }

            ShipType.DESTROYER -> {
                _movementUiState.update { state ->
                    state.copy(destroyerToMove = movementUiState.value.destroyerToMove.dec(),
                        destroyerOnPosition = movementUiState.value.destroyerOnPosition.inc(),
                        movingDestroyers = movementUiState.value.movingDestroyers.inc()
                    )
                }
            }

            ShipType.GHOST -> {
                _movementUiState.update { state ->
                    state.copy(
                        ghostToMove = movementUiState.value.ghostToMove.dec(),
                        ghostOnPosition = movementUiState.value.ghostOnPosition.inc(),
                        movingGhosts = movementUiState.value.movingGhosts.inc()
                    )
                }
            }

            ShipType.WARPER -> {
                _movementUiState.update { state ->
                    state.copy(
                        warperToMove = movementUiState.value.warperToMove.dec(),
                        warperOnPosition = movementUiState.value.warperOnPosition.inc(),
                        movingWarpers = movementUiState.value.movingWarpers.inc()
                    )
                }
            }
        }
    }

    fun removeShip(shipType: ShipType) {
        when (shipType) {
            ShipType.CRUISER -> {
                _movementUiState.update { state ->
                    state.copy(
                        cruiserToMove = movementUiState.value.cruiserToMove.inc(),
                        cruiserOnPosition = movementUiState.value.cruiserOnPosition.dec(),
                        movingCruisers = movementUiState.value.movingCruisers.dec()
                    )
                }
            }

            ShipType.DESTROYER -> {
                _movementUiState.update { state ->
                    state.copy(
                        destroyerToMove = movementUiState.value.destroyerToMove.inc(),
                        destroyerOnPosition = movementUiState.value.destroyerOnPosition.dec(),
                        movingDestroyers = movementUiState.value.movingDestroyers.dec()
                    )
                }
            }

            ShipType.GHOST -> {
                _movementUiState.update { state ->
                    state.copy(
                        ghostToMove = movementUiState.value.ghostToMove.inc(),
                        ghostOnPosition = movementUiState.value.ghostOnPosition.dec(),
                        movingGhosts = movementUiState.value.movingGhosts.dec()
                    )
                }
            }

            ShipType.WARPER -> {
                _movementUiState.update { state ->
                    state.copy(
                        warperToMove = movementUiState.value.warperToMove.inc(),
                        warperOnPosition = movementUiState.value.warperOnPosition.dec(),
                        movingWarpers = movementUiState.value.movingWarpers.dec()
                    )
                }
            }
        }
    }

    // Functions for movement records
    private fun updateRecordsForTurn() {
        val indexOfEndLocation = movementUiState.value.endPosition
        val movementMap: MutableMap<Ship, Int> = mutableMapOf()
        val movementRecord: MutableList<Map<Ship, Int>> =
            movementRecord.value.movementRecordOfTurn.toMutableList()
        indexOfEndLocation?.let {
            locationListUiState.value.locationList[it].myShipList
                .forEach { ship ->
                    if (ship.justMoved)
                        movementMap[ship] = indexOfEndLocation
                }
        }
        movementRecord.add(movementMap.toMap())
        _movementRecord.update { state ->
            state.copy(movementRecordOfTurn = movementRecord.toList())
        }
    }

    private fun cleanRecordsForTurn() {
        val newRecord = movementRecord.value.movementRecordOfTurn.toMutableList()
        newRecord.clear()
        _movementRecord.update { state ->
            state.copy(movementRecordOfTurn = newRecord.toList())
        }
    }

    fun undoAttack() {
        if (movementRecord.value.movementRecordOfTurn.isNotEmpty()) {
            val newLocationList: MutableList<Location> =
                locationListUiState.value.locationList.toMutableList()
            val indexOfLastMove = movementRecord.value.movementRecordOfTurn.size - 1
            movementRecord.value.movementRecordOfTurn[indexOfLastMove].forEach { map ->
                val ship = map.key
                ship.startingPosition?.let { newLocationList[it].myShipList.add(ship) }
                ship.currentPosition?.let { newLocationList[it].myShipList.remove(ship) }
                ship.currentPosition = ship.startingPosition
                ship.hasMoved = false
            }
            _locationListUiState.update { state ->
                state.copy(locationList = newLocationList)
            }
            val newMovementRecord = movementRecord.value.movementRecordOfTurn.toMutableList()
            newMovementRecord.removeAt(indexOfLastMove)
            _movementRecord.update { state ->
                state.copy(movementRecordOfTurn = newMovementRecord.toList())
            }
        }
    }

    fun getNumberShipsForRecord(
        shipType: ShipType,
        location1: Int,
        location2: Int,
        isMyRecord: Boolean
    ): Int {
        val listOfMove =
            if (isMyRecord) {
                movementRecord.value.movementRecordOfTurn.flatMap { map -> map.filter { it.value == location2 || it.value == location1 }.keys }
            } else {
                movementRecord.value.enemyRecord.filter {
                    (it.currentPosition == location2 && it.currentPosition != it.startingPosition) ||
                            (it.currentPosition == location1 && it.currentPosition != it.startingPosition)
                }
            }
        val shipListLocation2 = if (isMyRecord) {
            locationListUiState.value.locationList[location2].myShipList
        } else {
            movementRecord.value.enemyRecord.filter { it.currentPosition == location2 || it.startingPosition == location2 }
        }
        val size2 =
            shipListLocation2.filter { ship -> ship in listOfMove && ship.type == shipType && ship.currentPosition == location2 && ship.startingPosition == location1 }.size
        val shipListLocation1 = if (isMyRecord) {
            locationListUiState.value.locationList[location1].myShipList
        } else {
            movementRecord.value.enemyRecord.filter { it.currentPosition == location1 || it.startingPosition == location1 }
        }
        val size1 =
            shipListLocation1.filter { ship -> ship in listOfMove && ship.type == shipType && ship.currentPosition == location1 && ship.startingPosition == location2 }.size
        return size1 + size2
    }

    private fun changeWarperPresent(isPresent: Boolean) {
        _movementUiState.update { state ->
            state.copy(isWarperPresent = isPresent)
        }
    }

    fun setLocationForInfo(location: Int) {
        _movementUiState.update { state ->
            state.copy(locationForInfo = location)
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
        val opponent = sharedPlayerModel.playerData.value.opponent
        return startLocationOwner == opponent && endLocationOwner == opponent
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
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList.toList())
        }
        movementUiState.value.endPosition?.let { changeAcceptableLost(position = it) }
        updateRecordsForTurn()
        cleanJustMoved()
        cleanMovementValues()
        changeWarperPresent(isPresent = false)
    }

    private fun changeAcceptableLost(position: Int) {
        val newLocationList: MutableList<Location> =
            locationListUiState.value.locationList.toMutableList()
        val intValue: Int = movementUiState.value.acceptableLost.toInt()
        newLocationList[position].myAcceptableLost.intValue = intValue
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList.toList())
        }
    }

    fun changeValueAcceptableLost(value: Float) {
        _movementUiState.update { state ->
            state.copy(acceptableLost = value)
        }
    }

    fun changeShipTypeToShow(shipType: ShipType) {
        _movementUiState.update { state ->
            state.copy(shipTypeToShow = shipType)
        }
    }

    fun showShipInfoDialog(toShow: Boolean) {
        _movementUiState.value = _movementUiState.value.copy(showShipInfoDialog = toShow)
    }

    private fun showArmyDialog(toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(showArmyDialog = toShow)
        }
    }

    private fun cleanAfterUnsuccessfulMovement() {
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
            _movementUiState.update { state ->
                state.copy(startPosition = position)
            }
        } else {
            _movementUiState.update { state ->
                state.copy(endPosition = position)
            }
        }
    }

    private fun openAllLocations(location: Int) {
        val newLocationList: MutableList<Location> =
            locationListUiState.value.locationList.toMutableList()
        newLocationList.forEach {
            if (it.id != location && canAddMoreShips(it.id)) it.accessible = true
        }
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList)
        }
    }

    private fun isAnyShipInLocation(location: Int): Boolean {
        return locationListUiState.value.locationList[location].myShipList.any { it.currentPosition == location && !it.hasMoved }
    }

    private fun isWarperPresent(location: Int?): Boolean {
        return location?.let {
            locationListUiState.value.locationList[it].myShipList.any { ship ->
                ship.type == ShipType.WARPER && ship.currentPosition == location
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
        _locationListUiState.update { state ->
            state.copy(locationList = newLocationList)
        }
    }

    fun getNumberOfShip(
        location: Int?,
        shipType: ShipType,
        isForEnemy: Boolean = false
    ): Int {
        return location?.let {
            if (!isForEnemy) {
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

    private fun hasExceededShipLimit(location: Int?): Boolean {
        return location?.let {
            val locationInstance = locationListUiState.value.locationList[it]
            locationInstance.hasExceededShipLimit(battleMap.shipLimitOnPosition)
        } ?: false
    }

    private fun cleanMovingShip() {
        _movementUiState.update { state ->
            state.copy(
                movingCruisers = 0,
                movingDestroyers = 0,
                movingGhosts = 0,
                movingWarpers = 0
            )
        }
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
        _movementUiState.update { state ->
            state.copy(acceptableLost = 1.0f)
        }
    }

    fun cleanMovementValues() {
        cleanPositions()
        cleanAccessibleLocations()
        cleanMovingShip()
        cleanAcceptableLost()
        showArmyDialog(toShow = false)
    }

    fun setShipsOnPositionString(shipType: ShipType, movementUiState: MovementUiState): String {
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.cruiserOnPosition.toString()
            ShipType.DESTROYER -> movementUiState.destroyerOnPosition.toString()
            ShipType.GHOST -> movementUiState.ghostOnPosition.toString()
            ShipType.WARPER -> movementUiState.warperOnPosition.toString()
        }
    }

    fun setShipsToMoveString(shipType: ShipType, movementUiState: MovementUiState): String {
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.cruiserToMove.toString()
            ShipType.DESTROYER -> movementUiState.destroyerToMove.toString()
            ShipType.GHOST -> movementUiState.ghostToMove.toString()
            ShipType.WARPER -> movementUiState.warperToMove.toString()
        }
    }

    fun setEndOfGameText(
        isTitle: Boolean,
        context: Context
    ): String {
        return if (isTitle) {
            when (sharedPlayerModel.playerData.value.playerBattleResult) {
                BattleResultEnum.WIN -> context.getString(R.string.titleWinGame)
                BattleResultEnum.LOSE -> context.getString(R.string.titleLostGame)
                BattleResultEnum.DRAW -> context.getString(R.string.titleDrawGame)
            }
        } else {
            when(movementUiState.value.endOfGameType){
                EndOfGameType.CAPITULATION -> context.getString(R.string.winViaCapitulation)
                EndOfGameType.DISCONNECTED -> context.getString(R.string.winViaOpponentDisconnection)
                EndOfGameType.REGULAR -> {
                    when (sharedPlayerModel.playerData.value.playerBattleResult) {
                        BattleResultEnum.WIN -> context.getString(R.string.winGame)
                        BattleResultEnum.LOSE -> context.getString(R.string.lostGame)
                        BattleResultEnum.DRAW -> context.getString(R.string.drawGame)
                    }
                }
            }
        }
    }

    private fun randomMoveForFirstTurn() {
        val startingIndex = if (playerData.value.player == Players.PLAYER1) {
            battleMap.player1Base
        } else {
            battleMap.player2Base
        }
        if(locationListUiState.value.locationList[startingIndex].myShipList.size > battleMap.shipLimitOnPosition){
            val newList = locationListUiState.value.locationList
            val shipList = newList[startingIndex].myShipList.toMutableList()
            do {
                val possibleLocations: MutableList<Int> = mutableListOf()
                val connections = newList[startingIndex].getConnectionsList().filter { targetId ->
                    val targetLocation = newList[targetId]
                    targetLocation.enemyShipList.size < battleMap.shipLimitOnPosition
                }
                val currentIsValid = newList[startingIndex].myShipList.size < battleMap.shipLimitOnPosition
                possibleLocations.addAll(connections)
                if (currentIsValid){
                    possibleLocations.add(startingIndex)
                }
                val ship = shipList.random()
                val randomLocation = possibleLocations.random()
                newList[startingIndex].myShipList.remove(ship)
                newList[randomLocation].myShipList.add(ship)
                ship.currentPosition = randomLocation
                shipList.remove(ship)
            } while (shipList.isNotEmpty())
            _locationListUiState.update { state ->
                state.copy(
                    locationList = newList
                )
            }
        }
    }
}