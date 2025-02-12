package com.marks2games.gravitygame.firebase

import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.ShipType

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

fun String.toShipType(): ShipType? {
    return try {
        ShipType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}
