package com.marks2games.gravitygame.battle_game.domain

import com.marks2games.gravitygame.battle_game.data.model.Location
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleEngineTest {
    private val start = Location(0, listOf(1))
    private val destination = Location(1, listOf(0, 2))
    private val enemyBase = Location(2, listOf(1))
    private val engine = BattleEngine().apply {
        setMap(
            Battlefield(player1Base = 0, player2Base = 2, shipLimitOnPosition = 3, shipLimitOnMap = 5),
            listOf(start, destination, enemyBase),
        )
    }

    @Test
    fun `moving and undoing a fleet updates positions and the turn record`() {
        engine.createArmy(Players.PLAYER1, cruisers = 1, destroyers = 0, ghosts = 0)
        val cruiser = start.myShipList.single { it.type == ShipType.CRUISER }

        engine.moveFleet(0, 1, mapOf(ShipType.CRUISER to 1), acceptableLoss = 2)

        assertEquals(listOf(cruiser), destination.myShipList.toList())
        assertEquals(1, cruiser.currentPosition)
        assertTrue(cruiser.hasMoved)
        assertEquals(2, destination.myAcceptableLost.intValue)
        assertEquals(listOf(mapOf(cruiser to 1)), engine.movementRecord.value.movementRecordOfTurn)

        engine.undoLastMove()

        assertTrue(destination.myShipList.isEmpty())
        assertTrue(cruiser in start.myShipList)
        assertEquals(0, cruiser.currentPosition)
        assertFalse(cruiser.hasMoved)
        assertTrue(engine.movementRecord.value.movementRecordOfTurn.isEmpty())
    }

    @Test
    fun `capturing both bases is a draw`() {
        start.owner.value = Players.PLAYER2
        enemyBase.owner.value = Players.PLAYER1

        assertEquals(BattleResultEnum.DRAW, engine.result(Players.PLAYER1))
        assertEquals(BattleResultEnum.DRAW, engine.result(Players.PLAYER2))
    }
}
