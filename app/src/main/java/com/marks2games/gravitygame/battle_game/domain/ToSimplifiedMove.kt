package com.marks2games.gravitygame.battle_game.domain

import com.marks2games.gravitygame.battle_game.data.model.realtime_database.AcceptableLost
import com.marks2games.gravitygame.battle_game.data.model.realtime_database.SimplifiedMove
import com.marks2games.gravitygame.battle_game.data.model.realtime_database.SimplifiedShip
import com.marks2games.gravitygame.battle_game.data.model.Location

fun List<Location>.toSimplifiedMove(): SimplifiedMove {
    val acceptableLostList = this.map { location ->
        AcceptableLost(
            locationId = location.id,
            lostValue = location.myAcceptableLost.intValue
        )
    }

    val simplifiedShips = this.flatMap { location ->
        val shipList = location.myShipList
        shipList.map { ship ->
            SimplifiedShip(
                id = ship.id,
                currentPosition = ship.currentPosition,
                startingPosition = ship.startingPosition,
                shipType = ship.type.name
            )
        }
    }

    return SimplifiedMove(
        acceptableLost = acceptableLostList,
        simplifiedShipList = simplifiedShips
    )
}