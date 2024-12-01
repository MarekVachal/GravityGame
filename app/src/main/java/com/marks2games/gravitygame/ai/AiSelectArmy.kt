package com.marks2games.gravitygame.ai

import com.marks2games.gravitygame.maps.BattleMap
import com.marks2games.gravitygame.models.Cruiser
import com.marks2games.gravitygame.models.Destroyer
import com.marks2games.gravitygame.models.Ghost
import com.marks2games.gravitygame.models.Ship
import com.marks2games.gravitygame.models.Warper
import kotlin.random.Random

fun createAiArmy(battleMap: BattleMap): List<Ship>{
    val enemyArmy: MutableList<Ship> = mutableListOf(Warper(0))
    val maxUnits = battleMap.shipLimitOnMap - 1
    var cruisers: Int
    var destroyers: Int
    var ghosts: Int

    do {
        cruisers = Random.nextInt(1, maxUnits - 1)
        destroyers = Random.nextInt(1, maxUnits - cruisers)
        ghosts = maxUnits - cruisers - destroyers
    } while (ghosts < 1)

    createList(
        enemyArmy = enemyArmy,
        cruisers = cruisers,
        destroyers = destroyers,
        ghosts = ghosts)
    return enemyArmy.toList()
}

private fun createList(enemyArmy: MutableList<Ship>, cruisers: Int, destroyers: Int, ghosts: Int){
    var indexNumber = 1
    for(i in 1..cruisers){
        enemyArmy.add(Cruiser(indexNumber))
        indexNumber++
    }
    for(i in 1.. destroyers){
        enemyArmy.add(Destroyer(indexNumber))
        indexNumber++
    }
    for (i in 1..ghosts){
        enemyArmy.add(Ghost(indexNumber))
        indexNumber++
    }
}