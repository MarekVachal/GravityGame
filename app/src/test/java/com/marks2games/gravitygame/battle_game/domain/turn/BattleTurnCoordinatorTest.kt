package com.marks2games.gravitygame.battle_game.domain.turn

import com.marks2games.gravitygame.battle_game.data.BattleGameRepository
import com.marks2games.gravitygame.battle_game.data.BattleGameRepositoryFactory
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.battle_game.data.model.LocationList
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.EndOfGameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.battle_game.data.room_database.BattleRepository
import com.marks2games.gravitygame.battle_game.data.room_database.BattleResult
import com.marks2games.gravitygame.battle_game.domain.BattleEngine
import com.marks2games.gravitygame.battle_game.domain.BattleLosses
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleTurnCoordinatorTest {
    @Test
    fun `timer expiry and manual request resolve and save one turn`() = runBlocking {
        val engine = mockk<BattleEngine>(relaxUnitFun = true)
        every { engine.locationList } returns MutableStateFlow(LocationList())
        every { engine.conductBattles(Players.PLAYER1, Players.PLAYER2) } returns BattleLosses(2, 3)
        every { engine.result(Players.PLAYER1) } returns BattleResultEnum.WIN

        var listenerDetached = false
        val opponentReady = CompletableDeferred<Unit>()
        val onlineGame = mockk<BattleGameRepository>(relaxUnitFun = true)
        every { onlineGame.listenForCapitulation(any()) } returns { listenerDetached = true }
        var syncCount = 0
        coEvery { onlineGame.sendUpdatedLocations(any(), any(), any(), any()) } coAnswers {
            if (++syncCount == 2) opponentReady.await()
        }
        val factory = mockk<BattleGameRepositoryFactory>()
        every { factory.create() } returns onlineGame

        val savedResult = CompletableDeferred<BattleResult>()
        val history = mockk<BattleRepository>()
        coEvery { history.insertBattleResult(any()) } coAnswers {
            savedResult.complete(firstArg())
        }
        val players = SharedPlayerDataRepository().apply { updateIsOnline(true) }
        val turns = BattleTurnCoordinator(engine, players, factory, history)
        val expireTimer = slot<() -> Unit>()
        val clock = mockk<BattleClock>(relaxUnitFun = true)
        every { clock.remainingSeconds } returns 0
        every { clock.makeTimer(30, capture(expireTimer)) } returns Unit

        val battle = launch {
            turns.runBattle(clock, secondsPerTurn = 30, resultScope = this@runBlocking) {}
        }
        try {
            withTimeout(5_000) {
                turns.state.first { it.phase == BattleTurnPhase.READY }
                expireTimer.captured()
                turns.requestTurn()
                opponentReady.complete(Unit)

                val result = savedResult.await()
                assertEquals(BattleResultEnum.WIN, result.result)
                assertEquals(1, result.turn)
                assertEquals(2, result.myShipLost)
                assertEquals(3, result.enemyShipDestroyed)
                assertEquals(EndOfGameType.REGULAR, turns.state.value.endReason)
            }
            coVerify(exactly = 2) { onlineGame.sendUpdatedLocations(any(), any(), any(), any()) }
            coVerify(exactly = 1) { history.insertBattleResult(any()) }
            verify(exactly = 1) { onlineGame.deleteRoom(false) }
        } finally {
            battle.cancelAndJoin()
        }
        assertTrue(listenerDetached)
    }
}
