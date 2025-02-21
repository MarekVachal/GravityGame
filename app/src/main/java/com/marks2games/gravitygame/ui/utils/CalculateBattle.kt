package com.marks2games.gravitygame.ui.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.PlayerData
import com.marks2games.gravitygame.models.Players
import com.marks2games.gravitygame.models.Ship
import com.marks2games.gravitygame.models.ShipType
import com.marks2games.gravitygame.models.deepCopy
import com.marks2games.gravitygame.models.mapOfShips
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import java.lang.Integer.min
import kotlin.math.max

fun calculateBattle(
    location: Location,
    playerData: PlayerData,
    isSimulation: Boolean,
    battleModel: BattleViewModel
): Triple<Players, MutableMap<ShipType, Int>, MutableMap<ShipType, Int>> {
    val locationInUse = if(isSimulation) location.deepCopy() else location
    var myHp = 0
    var enemyHp = 0
    var playerFirepower = 0
    var enemyFirepower = 0
    var myLost = 0
    var enemyLost = 0
    var myWin = false
    var enemyWin = false
    val mapMyLost: MutableMap<ShipType, Int> = mutableMapOf(
        ShipType.CRUISER to 0,
        ShipType.DESTROYER to 0,
        ShipType.GHOST to 0,
        ShipType.WARPER to 0
    )
    val mapEnemyLost: MutableMap<ShipType, Int> = mutableMapOf(
        ShipType.CRUISER to 0,
        ShipType.DESTROYER to 0,
        ShipType.GHOST to 0,
        ShipType.WARPER to 0
    )

    do {
        if (playerFirepower == 0 && enemyFirepower == 0) {
            playerFirepower = calcFirepower(
                shipList = locationInUse.myShipList,
                shipListOther = locationInUse.enemyShipList,
                location = locationInUse,
                player = playerData.player
            )
            enemyFirepower = calcFirepower(
                shipList = locationInUse.enemyShipList,
                shipListOther = locationInUse.myShipList,
                location = locationInUse,
                player = playerData.opponent)
        }

        if (playerFirepower == 0 && enemyFirepower == 0) {
            break
        }

        val myTarget = getPriorityUnit(locationInUse.myShipList)
        val enemyTarget = getPriorityUnit(locationInUse.enemyShipList)

        if (myHp == 0) {
            myHp = mapOfShips[myTarget]?.hp?:0
        }
        if (enemyHp == 0) {
            enemyHp = mapOfShips[enemyTarget]?.hp?:0
        }

        myHp -= enemyFirepower
        enemyHp -= playerFirepower
        playerFirepower = 0
        enemyFirepower = 0

        if (myHp <= 0 && myTarget != null) {
            killUnit(locationInUse.myShipList, myTarget)
            enemyFirepower = -myHp
            myHp = 0
            myLost += 1
            mapMyLost[myTarget] = (mapMyLost[myTarget] ?: 0) +1
        }

        if (enemyHp <= 0 && enemyTarget != null) {
            killUnit(locationInUse.enemyShipList, enemyTarget)
            playerFirepower = -enemyHp
            enemyHp = 0
            enemyLost += 1
            mapEnemyLost[enemyTarget] = (mapEnemyLost[enemyTarget] ?: 0) +1
        }

        myWin = enemyLost >= locationInUse.enemyAcceptableLost.intValue || locationInUse.enemyShipList.isEmpty()
        enemyWin = myLost >= locationInUse.myAcceptableLost.intValue || locationInUse.myShipList.isEmpty()
    } while ((!myWin && !enemyWin) || playerFirepower > 0 || enemyFirepower > 0)

    battleModel.writeDestroyedShips(isSimulation = isSimulation, myLostShip = myLost, enemyLostShip = enemyLost)
    if (myWin && !enemyWin) {
        return Triple(playerData.player, mapMyLost, mapEnemyLost)
    }
    if (!myWin && enemyWin) {
        return Triple(playerData.opponent, mapMyLost, mapEnemyLost)
    }
    return Triple(Players.NONE, mapMyLost, mapEnemyLost)
}

private fun killUnit(shipList: SnapshotStateList<Ship>, targetShipType: ShipType) {
    val shipInIssue = shipList.firstOrNull { it.type == targetShipType }
    shipList.remove(shipInIssue)
}

private fun getPriorityUnit(shipList: SnapshotStateList<Ship>): ShipType? {
    if (shipList.isEmpty()) return null
    var dominantShipType: ShipType? = null
    var nMax = 0
    val shipCount = mutableMapOf<ShipType, Int>()
    shipList.forEach {
        shipCount[it.type] = (shipCount[it.type] ?: 0) + 1
    }
    shipCount.forEach {
        if (it.value > nMax || (it.value == nMax && (mapOfShips[it.key]?.priority
                ?: 0) > (mapOfShips[dominantShipType]?.priority ?: 0))
        ) {
            dominantShipType = it.key
            nMax = it.value
        }
    }
    return dominantShipType
}

private fun calcFirepower(
    shipList: SnapshotStateList<Ship>,
    shipListOther: SnapshotStateList<Ship>,
    location: Location,
    player: Players
): Int {
    var firepower = 0
    var destroyerFirepower = 0
    shipList.forEach {
        if (it.type == ShipType.DESTROYER) {
            destroyerFirepower += it.firepower
        } else {
            firepower += it.firepower
        }
    }
    shipListOther.forEach {
        if (it.type == ShipType.GHOST) {
            destroyerFirepower -= (mapOfShips[ShipType.DESTROYER]?.firepower ?: 0)
        }
    }
    destroyerFirepower = max(destroyerFirepower, 0)
    firepower += min(destroyerFirepower, shipListOther.size)
    firepower += if (location.owner.value == player) 1 else 0
    return firepower
}