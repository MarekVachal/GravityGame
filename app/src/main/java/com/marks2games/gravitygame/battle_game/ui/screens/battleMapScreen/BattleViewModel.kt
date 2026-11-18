package com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleMapEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.EndOfGameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.battle_game.domain.BattleEngine
import com.marks2games.gravitygame.battle_game.domain.Battlefield
import com.marks2games.gravitygame.battle_game.domain.turn.BattleClock
import com.marks2games.gravitygame.battle_game.domain.turn.BattleTurnCoordinator
import com.marks2games.gravitygame.battle_game.ui.maps.BattleMap
import com.marks2games.gravitygame.battle_game.ui.maps.TinyMap
import com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen.SelectArmyUiState
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val sharedPlayerModel: SharedPlayerDataRepository,
    private val engine: BattleEngine,
    private val turnCoordinator: BattleTurnCoordinator,
) : ViewModel() {

    private val _movementUiState = MutableStateFlow(MovementUiState())
    val movementUiState = _movementUiState.asStateFlow()
    val locationListUiState = engine.locationList
    val movementRecord = engine.movementRecord
    val turnState = turnCoordinator.state
    var battleMap: BattleMap = sharedPlayerModel.getBattleMap()
        private set

    init {
        setEngineMap()
    }

    fun createBattleMap(selectedMap: BattleMapEnum) {
        battleMap = when (selectedMap) {
            BattleMapEnum.TINY -> TinyMap()
        }
        sharedPlayerModel.updateBattleMap(selectedMap)
        setEngineMap()
    }

    private fun setEngineMap() {
        engine.setMap(
            field = Battlefield(
                player1Base = battleMap.player1Base,
                player2Base = battleMap.player2Base,
                shipLimitOnPosition = battleMap.shipLimitOnPosition,
                shipLimitOnMap = battleMap.shipLimitOnMap,
            ),
            locations = battleMap.locationList,
        )
    }

    private fun createArmyList(selectArmyUiState: SelectArmyUiState) {
        engine.createArmy(
            player = getPlayer(),
            cruisers = selectArmyUiState.numberCruisers,
            destroyers = selectArmyUiState.numberDestroyers,
            ghosts = selectArmyUiState.numberGhosts,
        )
    }

    suspend fun runBattle(army: SelectArmyUiState, timer: BattleClock) {
        _movementUiState.update {
            it.copy(showCapitulateInfoDialog = false, showEndOfGameDialog = true)
        }
        createArmyList(army)
        turnCoordinator.runBattle(
            timer = timer,
            secondsPerTurn = battleMap.secondsForTurn,
            resultScope = viewModelScope,
            clearMovement = ::cleanMovementValues,
        )
    }

    fun hasExceededBaseLimit(): Boolean = engine.hasExceededBaseLimit(getPlayer())

    fun finishTurn() = turnCoordinator.requestTurn()

    fun capitulate() = turnCoordinator.capitulate()


    fun getPlayer(): Players {
        return sharedPlayerModel.getPlayer()
    }

    fun isMyLocation(location: Int): Boolean?{
        val locationOwner = locationListUiState.value.locationList[location].owner.value
        return when(locationOwner){
            sharedPlayerModel.playerData.value.player -> true
            sharedPlayerModel.playerData.value.opponent -> false
            else -> null
        }
    }

    fun isOnlineGame(isOnline: Boolean) {
        sharedPlayerModel.updateIsOnline(isOnline)
    }

    fun onDragStart(offset: Offset, location: Int) {
        if (engine.canStartMove(location)) {
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
        engine.markReachableLocations(position, sharedPlayerModel.playerData.value.opponent)
        changeDraggingIconVisible(true)
    }

    private fun setEndLocationMovementOrder(position: Int) {
        val start = movementUiState.value.startPosition ?: return
        val hasWarper = engine.hasWarper(start)
        if (start != position && (hasWarper || engine.isReachable(position))) {
            changeMovementPosition(isStart = false, position = position)
            changeWarperPresent(hasWarper)
            showArmyDialog(toShow = true)
        } else {
            cleanAfterUnsuccessfulMovement()
        }
    }

    fun callBattleResultInBattleInfo(result: BattleResultEnum, context: Context): String {
        return when (result) {
            BattleResultEnum.WIN -> context.getString(R.string.winBattleResultForBattleInfo)
            BattleResultEnum.LOSE -> context.getString(R.string.lostBattleResultForBattleInfo)
            BattleResultEnum.DRAW -> context.getString(R.string.drawBattleResultForBattleInfo)
        }
    }

    fun showCapitulateInfoDialog(toShow: Boolean) {
        _movementUiState.update { state ->
            state.copy(showCapitulateInfoDialog = toShow)
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
        val state = movementUiState.value
        return checkAddShipAccordingToNumberOfShips(shipType) && engine.canAddShip(
            shipType = shipType,
            start = startLocation,
            destination = endLocation,
            isWarperPresent = isWarperPresent,
            shipsAlreadySelected = state.movingCruisers + state.movingDestroyers +
                state.movingGhosts + state.movingWarpers,
            opponent = sharedPlayerModel.playerData.value.opponent,
        )
    }

    private fun checkAddShipAccordingToNumberOfShips(shipType: ShipType): Boolean {
        return when (shipType) {
            ShipType.CRUISER -> movementUiState.value.cruiserToMove
            ShipType.DESTROYER -> movementUiState.value.destroyerToMove
            ShipType.GHOST -> movementUiState.value.ghostToMove
            ShipType.WARPER -> movementUiState.value.warperToMove
        } > 0
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

    fun attack() {
        val state = movementUiState.value
        val start = state.startPosition ?: return
        val destination = state.endPosition ?: return
        engine.moveFleet(
            start = start,
            destination = destination,
            fleet = mapOf(
                ShipType.CRUISER to state.movingCruisers,
                ShipType.DESTROYER to state.movingDestroyers,
                ShipType.GHOST to state.movingGhosts,
                ShipType.WARPER to state.movingWarpers,
            ),
            acceptableLoss = state.acceptableLost.toInt(),
        )
        cleanMovementValues()
        changeWarperPresent(isPresent = false)
    }

    fun undoAttack() = engine.undoLastMove()

    private fun changeAcceptableLost(position: Int) {
        engine.setAcceptableLoss(position, movementUiState.value.acceptableLost.toInt())
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
        engine.clearReachableLocations()
    }

    private fun cleanPositions() {
        changeMovementPosition(isStart = true, position = null)
        changeMovementPosition(isStart = false, position = null)
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

    private fun cleanAcceptableLost() {
        _movementUiState.update { state ->
            state.copy(acceptableLost = 1.0f)
        }
    }

    fun cleanMovementValues() {
        cleanPositions()
        engine.clearReachableLocations()
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
            when(turnState.value.endReason ?: EndOfGameType.REGULAR){
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
}
