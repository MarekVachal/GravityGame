package com.marks2games.gravitygame.battle_game.domain

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.marks2games.gravitygame.battle_game.data.model.Location
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.core.data.model.Ship
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.battle_game.data.model.deepCopy
import com.marks2games.gravitygame.core.data.model.mapOfShips

/**
 * Calculates the outcome of a battle between two players.
 *
 * This function simulates a battle between two players at a given location,
 * taking into account their ship types and acceptable losses.
 * The battle continues until one player either loses all their ships or reaches their
 * acceptable loss limit.
 *
 * @param location The location where the battle takes place. Contains information about the
 *                 ships of both players and their acceptable losses.
 * @param player The player in this app (the "my" side).
 * @param opponent The opposing player (the "enemy" side).
 * @param isSimulation A boolean indicating whether this is a simulation or a real battle.
 *                     If true, a deep copy of the location is used to avoid modifying
 *                     the original data.
 * @param writeDestroyedShips A lambda function to be called at the end of the battle,
 *                            providing information about the number of ships lost by each
 *                            player. The parameters are:
 *                              - isSimulation: Boolean indicating if it was a simulation.
 *                              - myLostCount: The number of ships lost by the 'my' side.
 *                              - enemyLostCount: The number of ships lost by the 'enemy' side.
 *
 * @return A Triple containing:
 *          - The winning player (or Players.NONE in case of a draw).
 *          - A MutableMap representing the ships lost by the 'my' player, where the key is the
 *            ShipType and the value is the number of ships of that type lost.
 *          - A MutableMap representing the ships lost by the 'enemy' player, where the key is
 *            the ShipType and the value is the number of ships of that type lost.
 */
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

/**
 * Removes the first ship from the given list that matches the specified ship type.
 *
 * This function iterates through the provided `shipList` and searches for a ship whose
 * `type` property matches the `targetShipType`. If a match is found, that ship is removed
 * from the list. If no match is found, the list remains unchanged.
 *
 * @param shipList The list of ships to search and potentially remove from. This is a `SnapshotStateList`
 *                 which is a mutable list that Compose can observe for changes.
 * @param targetShipType The `ShipType` to look for within the ships in the list.
 * @throws NoSuchElementException if shipList is empty
 * @throws ConcurrentModificationException if the shipList is modified externally while the function is running.
 */
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

/**
 * Calculates the firepower of the player's fleet at a given location.
 *
 * This function calculates the total firepower of the player's ships,
 * taking into account the presence of enemy "ghost" ships and the
 * ownership of the location.
 *
 * @param myShipList A list of the player's ships.
 * @param enemyShipList A list of the enemy's ships.
 * @param location The location where the firepower is being calculated.
 * @param player The player for whom the firepower is being calculated.
 * @return The total calculated firepower.
 *
 * **Calculation Breakdown:**
 * 1. **Base Firepower:** The function iterates through the `myShipList` and sums the `firepower` of each ship.
 *    - Destroyer ships have their firepower counted separately in `destroyerFirepower`.
 *    - All other ship types have their firepower added to the main `firepower` counter.
 * 2. **Ghost Ship Penalty:** The function then iterates through `enemyShipList`.
 *    - If a ship of type `ShipType.GHOST` is present, it reduces the `destroyerFirepower` by the base firepower of a Destroyer ship (retrieved from `mapOfShips`).
 *    - If no Destroyer firepower is present in mapOfShips, it will remove 0.
 * 3. **Minimum Destroyer Firepower:** Ensures that `destroyerFirepower` is not negative by using `maxOf(destroyerFirepower, 0)`.
 * 4. **Destroyer Firepower Contribution:**  The function adds a portion of the remaining `destroyerFirepower` to the main firepower.
 *    - The amount added is the minimum between `destroyerFirepower` and the number of enemy ships (`enemyShipList.size`).
 * 5. **Location Ownership Bonus:** If the location is owned by the player, an additional 1 is added to the `firepower`.
 * 6. **Return Value:** The function returns the final calculated `firepower`.
 */
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