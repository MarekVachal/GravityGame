package com.example.gravitygame.ui.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips
import java.lang.Integer.min
import kotlin.math.max

fun calculateBattle(location: Location): Players {
    var myHp = 0
    var enemyHp = 0

    var myFir = 0
    var enemyFir = 0

    var myLost = 0
    var enemyLost = 0

    var myWin = false
    var enemyWin = false

    do {
        if (myFir == 0 && enemyFir == 0) {
            myFir = calcFirepower(location.myShipList, location.enemyShipList)
            enemyFir = calcFirepower(location.enemyShipList, location.myShipList)
        }

        if (myFir == 0 && enemyFir == 0) {
            break
        }

        val myTarget = getPriorityUnit(location.myShipList)
        val enemyTarget = getPriorityUnit(location.enemyShipList)

        if (myHp == 0) {
            myHp = mapOfShips[myTarget]?.hp!!
        }
        if (enemyHp == 0) {
            enemyHp = mapOfShips[enemyTarget]?.hp!!
        }

        myHp -= enemyFir
        enemyHp -= myFir
        myFir = 0
        enemyFir = 0

        if (myHp <= 0) {
            killUnit(location.myShipList, myTarget)
            enemyFir = -myHp
            myHp = 0
            myLost += 1
        }

        if (enemyHp <= 0) {
            killUnit(location.enemyShipList, enemyTarget)
            myFir = -enemyHp
            enemyHp = 0
            enemyLost += 1
        }

        myWin = enemyLost >= location.enemyAcceptableLost
        enemyWin = myLost >= location.myAcceptableLost

    } while ((!myWin && !enemyWin) || myFir > 0 || enemyFir > 0)

    if (myWin && !enemyWin) {
        return Players.PLAYER1
    }
    if (!myWin && enemyWin) {
        return Players.PLAYER2
    }
    return Players.NONE
}

private fun killUnit(shipList: SnapshotStateList<Ship>, stype: ShipType) {
    val shipInIssue = shipList.firstOrNull { it.type == stype }
    shipList.remove(shipInIssue)
}

private fun getPriorityUnit(shipList: SnapshotStateList<Ship>): ShipType {
    var stype = ShipType.CRUISER
    var nMax = 0
    val n = mutableMapOf<ShipType, Int>()
    shipList.forEach {
        n[it.type] = (n[it.type] ?: 0) + 1
    }
    n.forEach {
        if (it.value > nMax || (it.value == nMax && (mapOfShips[it.key]?.priority
                ?: 0) > (mapOfShips[stype]?.priority ?: 0))
        ) {
            stype = it.key
            nMax = it.value
        }
    }
    return stype
}


private fun calcFirepower(
    shipList: SnapshotStateList<Ship>,
    shipListOther: SnapshotStateList<Ship>
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
    return firepower
}