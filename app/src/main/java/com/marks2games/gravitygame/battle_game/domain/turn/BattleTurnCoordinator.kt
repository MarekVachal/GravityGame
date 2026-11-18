package com.marks2games.gravitygame.battle_game.domain.turn

import com.marks2games.gravitygame.battle_game.data.BattleGameRepository
import com.marks2games.gravitygame.battle_game.data.BattleGameRepositoryFactory
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.EndOfGameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.battle_game.data.room_database.BattleRepository
import com.marks2games.gravitygame.battle_game.data.room_database.BattleResult
import com.marks2games.gravitygame.battle_game.domain.BattleEngine
import io.sentry.Sentry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

interface BattleClock {
    val remainingSeconds: Long
    fun makeTimer(seconds: Int, onFinish: () -> Unit)
    fun stopTimer()
    fun cancelTimer()
    fun restartTurn()
}

enum class BattleTurnPhase {
    SETUP, READY, WAITING_FOR_OPPONENT, CALCULATING_AI, STARTING_NEXT_TURN, FINISHED
}

data class BattleTurnState(
    val turn: Int = 0,
    val phase: BattleTurnPhase = BattleTurnPhase.SETUP,
    val timed: Boolean = false,
    val endReason: EndOfGameType? = null,
)

class BattleTurnCoordinator @Inject constructor(
    private val engine: BattleEngine,
    private val playerData: SharedPlayerDataRepository,
    private val repositoryFactory: BattleGameRepositoryFactory,
    private val battleHistory: BattleRepository,
) {
    private val _state = MutableStateFlow(BattleTurnState())
    val state = _state.asStateFlow()
    private var session: Session? = null

    private class Session(
        val scope: CoroutineScope,
        val resultScope: CoroutineScope,
        val timer: BattleClock,
        val onlineGame: BattleGameRepository?,
        val player: Players,
        val opponent: Players,
        val clearMovement: () -> Unit,
    ) {
        var playerLosses = 0
        var opponentLosses = 0
    }

    suspend fun runBattle(
        timer: BattleClock,
        secondsPerTurn: Int,
        resultScope: CoroutineScope,
        clearMovement: () -> Unit,
    ): Unit = coroutineScope {
        session?.scope?.cancel()
        val players = playerData.playerData.value
        val onlineGame = if (players.isOnline) repositoryFactory.create() else null
        val game = Session(this, resultScope, timer, onlineGame, players.player, players.opponent, clearMovement)
        session = game
        _state.value = BattleTurnState()
        if (onlineGame == null) timer.cancelTimer() else timer.stopTimer()

        var detach: (() -> Unit)? = null
        try {
            detach = onlineGame?.listenForCapitulation {
                completeBattle(game, BattleResultEnum.WIN, EndOfGameType.CAPITULATION)
            }
            if (onlineGame == null) {
                engine.createOfflineOpponent(game.opponent)
            } else if (isActive(game)) {
                syncOpponent(game, isSetup = true)
            }
            if (isActive(game)) {
                engine.clearEnemyRecord()
                _state.update { it.copy(phase = BattleTurnPhase.READY, timed = onlineGame != null) }
                if (onlineGame != null) {
                    timer.makeTimer(secondsPerTurn) { requestTurn(game) }
                }
            }
            awaitCancellation()
        } finally {
            detach?.invoke()
            if (session === game) {
                session = null
                timer.cancelTimer()
            }
        }
    }

    fun requestTurn() {
        session?.let(::requestTurn)
    }

    private fun requestTurn(game: Session) {
        if (!isActive(game) || _state.value.phase != BattleTurnPhase.READY) return

        val phase = if (game.onlineGame == null) {
            BattleTurnPhase.CALCULATING_AI
        } else {
            BattleTurnPhase.WAITING_FOR_OPPONENT
        }
        _state.update { it.copy(phase = phase) }
        game.scope.launch { resolveTurn(game) }
    }

    private suspend fun resolveTurn(game: Session) {
        if (!isActive(game)) return
        game.timer.stopTimer()
        engine.prepareTurn()
        game.clearMovement()
        if (_state.value.turn == 0) engine.distributeStartingFleet(game.player)
        _state.update { it.copy(turn = it.turn + 1) }

        if (game.onlineGame != null) {
            try {
                syncOpponent(game, isSetup = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Sentry.captureException(e)
                return
            }
        } else {
            engine.playAiTurn(game.player, game.opponent)
        }

        if (!isActive(game)) return
        val losses = engine.conductBattles(game.player, game.opponent)
        game.playerLosses += losses.player
        game.opponentLosses += losses.opponent
        if (game.onlineGame == null) engine.resetEnemyAcceptableLoss()

        val result = engine.result(game.player)
        if (result != null) {
            completeBattle(game, result, EndOfGameType.REGULAR)
            return
        }

        _state.update { it.copy(phase = BattleTurnPhase.STARTING_NEXT_TURN) }
        delay(1_000)
        if (!isActive(game)) return
        _state.update { it.copy(phase = BattleTurnPhase.READY) }
        if (game.onlineGame != null) game.timer.restartTurn()
    }

    private suspend fun syncOpponent(game: Session, isSetup: Boolean) {
        game.onlineGame?.sendUpdatedLocations(
            updatedLocations = engine.locationList.value.locationList,
            onOpponentReady = { if (isActive(game)) engine.applyOpponentMove(it, isSetup) },
            timeoutMillis = game.timer.remainingSeconds * 1_000 + 2_000,
            onOpponentDisconnected = {
                completeBattle(game, BattleResultEnum.WIN, EndOfGameType.DISCONNECTED)
            },
        )
    }

    fun capitulate() {
        val game = session ?: return
        if (!isActive(game)) return

        val onlineGame = game.onlineGame
        if (onlineGame == null) {
            playerData.updateBattleResult(BattleResultEnum.LOSE)
            game.timer.cancelTimer()
            return
        }

        onlineGame.capitulate()
        completeBattle(game, BattleResultEnum.LOSE, EndOfGameType.CAPITULATION)
    }

    private fun completeBattle(game: Session, result: BattleResultEnum, reason: EndOfGameType) {
        if (!isActive(game)) return

        playerData.updateBattleResult(result)
        val record = BattleResult(
            result = result,
            timestamp = System.currentTimeMillis(),
            enemyShipDestroyed = game.opponentLosses,
            myShipLost = game.playerLosses,
            turn = _state.value.turn,
        )
        _state.update { it.copy(turn = 0, phase = BattleTurnPhase.FINISHED, endReason = reason) }
        game.timer.cancelTimer()
        game.resultScope.launch { battleHistory.insertBattleResult(record) }
        game.onlineGame?.deleteRoom(reason == EndOfGameType.DISCONNECTED)
    }

    private fun isActive(game: Session): Boolean =
        session === game && game.scope.isActive && _state.value.endReason == null
}
