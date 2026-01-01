package com.marks2games.gravitygame.battle_game.domain.ai

import com.marks2games.gravitygame.battle_game.ui.maps.BattleMap
import com.marks2games.gravitygame.core.data.model.Cruiser
import com.marks2games.gravitygame.core.data.model.Destroyer
import com.marks2games.gravitygame.core.data.model.Ghost
import com.marks2games.gravitygame.core.data.model.Ship
import com.marks2games.gravitygame.core.data.model.Warper
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