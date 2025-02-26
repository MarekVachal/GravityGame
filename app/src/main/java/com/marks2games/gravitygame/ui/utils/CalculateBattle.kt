package com.marks2games.gravitygame.ui.utils

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.Players
import com.marks2games.gravitygame.models.Ship
import com.marks2games.gravitygame.models.ShipType
import com.marks2games.gravitygame.models.deepCopy
import com.marks2games.gravitygame.models.mapOfShips

fun calculateBattle(
    location: Location,
    player: Players,
    opponent: Players,
    isSimulation: Boolean,
    writeDestroyedShips: (Boolean, Int, Int) -> Unit
): Triple<Players, MutableMap<ShipType, Int>, MutableMap<ShipType, Int>> {
    val battleLocation = if(isSimulation) location.deepCopy() else location
    var myTargetHp = 0
    var enemyTargetHp = 0
    var myTarget: ShipType? = null
    var enemyTarget: ShipType? = null
    var myFirepower = 0
    var enemyFirepower = 0
    var myLostCount = 0
    var enemyLostCount = 0
    var myWin = false
    var enemyWin = false
    var isTurnFinished = true
    val myLostShips: MutableMap<ShipType, Int> = ShipType.entries.associateWith { 0 }.toMutableMap()
    val enemyLostShips: MutableMap<ShipType, Int> = ShipType.entries.associateWith { 0 }.toMutableMap()

    while (!myWin && !enemyWin){
        if (isTurnFinished) {
            myFirepower = calcFirepower(
                myShipList = battleLocation.myShipList,
                enemyShipList = battleLocation.enemyShipList,
                location = battleLocation,
                player = player
            )
            enemyFirepower = calcFirepower(
                myShipList = battleLocation.enemyShipList,
                enemyShipList = battleLocation.myShipList,
                location = battleLocation,
                player = opponent
            )
        }
        Log.d("Battle", "My Firepower: $myFirepower")
        Log.d("Battle", "Enemy firepower: $enemyFirepower")

        if (myFirepower == 0 && enemyFirepower == 0) {
            Log.d("Battle", "Both players have 0 firepower")
            return Triple(Players.NONE, myLostShips, enemyLostShips)
        }

        if (myTarget == null) {
            myTarget = getPriorityUnit(battleLocation.enemyShipList) ?: return Triple(player, myLostShips, enemyLostShips)
            myTargetHp = mapOfShips[myTarget]?.hp?:0
        }
        Log.d("Battle", "My target: $myTarget")
        if(enemyTarget == null) {
            enemyTarget = getPriorityUnit(battleLocation.myShipList) ?: return Triple(opponent, myLostShips, enemyLostShips)
            enemyTargetHp = mapOfShips[enemyTarget]?.hp?:0
        }
        Log.d("Battle", "Enemy target: $enemyTarget")

        if(myFirepower != 0){
            val damageToMyTarget = minOf(myFirepower, myTargetHp)
            myTargetHp -= damageToMyTarget
            myFirepower -= damageToMyTarget
            Log.d("Battle", "My firepower after attack: $myFirepower")
            Log.d("Battle", "My damage to target: $damageToMyTarget")
            Log.d("Battle", "My target HP after attack: $myTargetHp")
            if (myTargetHp == 0) {
                killUnit(battleLocation.enemyShipList, myTarget)
                enemyLostShips[myTarget] = enemyLostShips.getOrDefault(myTarget, 0) +1
                myTarget = null
                Log.d("Battle", "Enemy ship was killed")
                enemyLostCount++
            }
        }

        if(enemyFirepower != 0) {
            val damageToEnemyTarget = minOf(enemyFirepower, enemyTargetHp)
            enemyTargetHp -= damageToEnemyTarget
            enemyFirepower -= damageToEnemyTarget
            Log.d("Battle", "Enemy firepower after attack: $enemyFirepower")
            Log.d("Battle", "Enemy damage to target: $damageToEnemyTarget")
            Log.d("Battle", "Enemy target HP after attack: $enemyTargetHp")
            if (enemyTargetHp == 0) {
                killUnit(battleLocation.myShipList, enemyTarget)
                myLostShips[enemyTarget] = myLostShips.getOrDefault(enemyTarget, 0) +1
                enemyTarget = null
                Log.d("Battle", "My ship was killed")
                myLostCount++
            }
        }

        isTurnFinished = myFirepower == 0 && enemyFirepower == 0

        if(isTurnFinished) {
            myWin = enemyLostCount >=
                    battleLocation.enemyAcceptableLost.intValue ||
                    battleLocation.enemyShipList.isEmpty()
            enemyWin = myLostCount >=
                    battleLocation.myAcceptableLost.intValue ||
                    battleLocation.myShipList.isEmpty()
        }
    }

    writeDestroyedShips(
        isSimulation,
        myLostCount,
        enemyLostCount
    )

    return when {
        myWin && enemyWin -> Triple(Players.NONE, myLostShips, enemyLostShips)
        myWin -> Triple(player, myLostShips, enemyLostShips)
        else -> Triple(opponent, myLostShips, enemyLostShips)
    }
}

private fun killUnit(shipList: SnapshotStateList<Ship>, targetShipType: ShipType) {
    val shipInIssue = shipList.firstOrNull { it.type == targetShipType }
    shipList.remove(shipInIssue)
}
/**
 * Determines the dominant ShipType from a list of ships based on count and priority.
 *
 * @param shipList The list of ships to analyze.
 * @return The ShipType with the highest count, or in case of ties, the one with the highest priority.
 *         Returns null if the list is empty.
 */
private fun getPriorityUnit(shipList: SnapshotStateList<Ship>): ShipType? {
    if (shipList.isEmpty()) return null
    //var dominantShipType: ShipType? = null
    //var nMax = 0
    val shipTypeCounts = shipList.groupingBy { it.type }.eachCount()
    val maxCount = shipTypeCounts.values.maxOrNull() ?: 0
    val maxCountShipTypes = shipTypeCounts.filterValues { it == maxCount }.keys
    if(maxCountShipTypes.size == 1){
        return maxCountShipTypes.first()
    }
    return maxCountShipTypes.maxByOrNull { shipType ->
        mapOfShips[shipType]?.priority ?: 0
    }

    /*
    shipTypeCount.forEach {
        if (it.value > nMax || (it.value == nMax && (mapOfShips[it.key]?.priority
                ?: 0) > (mapOfShips[dominantShipType]?.priority ?: 0))
        ) {
            dominantShipType = it.key
            nMax = it.value
        }
    }
    return dominantShipType

     */
}

private fun calcFirepower(
    myShipList: SnapshotStateList<Ship>,
    enemyShipList: SnapshotStateList<Ship>,
    location: Location,
    player: Players
): Int {
    var firepower = 0
    var destroyerFirepower = 0
    myShipList.forEach {
        if (it.type == ShipType.DESTROYER) {
            destroyerFirepower += it.firepower
        } else {
            firepower += it.firepower
        }
    }
    enemyShipList.forEach {
        if (it.type == ShipType.GHOST) {
            destroyerFirepower -= (mapOfShips[ShipType.DESTROYER]?.firepower ?: 0)
        }
    }
    destroyerFirepower = maxOf(destroyerFirepower, 0)
    firepower += minOf(destroyerFirepower, enemyShipList.size)
    firepower += if (location.owner.value == player) 1 else 0
    return firepower
}