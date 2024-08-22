package com.example.gravitygame.ai

import com.example.gravitygame.maps.BattleMap
import com.example.gravitygame.models.Cruiser
import com.example.gravitygame.models.Destroyer
import com.example.gravitygame.models.Ghost
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.Warper
import kotlin.random.Random

fun createAiArmy(battleMap: BattleMap, startLocation: Int): List<Ship>{
    val enemyArmy: MutableList<Ship> = mutableListOf(Warper(0))
    val maxUnits = battleMap.shipLimitOnMap - 1
    val cruisers = Random.nextInt(1, maxUnits)
    val destroyers = Random.nextInt(1, maxUnits - cruisers)
    val ghosts = maxUnits - cruisers - destroyers

    require(ghosts > 0)

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

    enemyArmy.forEach { ship ->
        ship.currentPosition = startLocation
        ship.startingPosition = startLocation
    }
    return enemyArmy.toList()
}