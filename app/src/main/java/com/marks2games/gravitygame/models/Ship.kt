package com.marks2games.gravitygame.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.marks2games.gravitygame.R

abstract class Ship(open val id: Int){
    abstract val name: String
    abstract val nameId: Int
    abstract val type: ShipType
    abstract val firepower: Int
    abstract val hp: Int
    abstract val priority: Int
    abstract val descriptionId: Int
    var currentPosition: Int? = null
    var startingPosition: Int? = null
    var hasMoved: Boolean by mutableStateOf(false)
    var justMoved: Boolean by mutableStateOf(false)
    abstract fun deepCopy(): Ship

}

data class Cruiser (override val id: Int) : Ship(id){
    override val name = R.string.cruiser.toString()
    override val nameId = R.string.cruiser
    override val type = ShipType.CRUISER
    override val firepower = 1
    override val hp = 10
    override val priority = 4
    override val descriptionId = R.string.cruiserInfo
    override fun deepCopy(): Cruiser {
        val copiedShip = Cruiser(id)
        copiedShip.currentPosition = this.currentPosition
        copiedShip.startingPosition = this.startingPosition
        copiedShip.hasMoved = this.hasMoved
        copiedShip.justMoved = this.justMoved
        return copiedShip
    }
}

data class Destroyer (override val id: Int) : Ship(id) {
    override val name = R.string.destroyer.toString()
    override val nameId = R.string.destroyer
    override val type = ShipType.DESTROYER
    override val firepower = 2
    override val hp = 10
    override val priority = 3
    override val descriptionId = R.string.destroyerInfo
    override fun deepCopy(): Destroyer {
        val copiedShip = Destroyer(id)
        copiedShip.currentPosition = this.currentPosition
        copiedShip.startingPosition = this.startingPosition
        copiedShip.hasMoved = this.hasMoved
        copiedShip.justMoved = this.justMoved
        return copiedShip
    }
}

data class Ghost (override val id: Int) : Ship(id){
    override val name = R.string.ghost.toString()
    override val nameId = R.string.ghost
    override val type = ShipType.GHOST
    override val firepower = 1
    override val hp = 5
    override val priority = 2
    override val descriptionId = R.string.ghostInfo
    override fun deepCopy(): Ghost {
        val copiedShip = Ghost(id)
        copiedShip.currentPosition = this.currentPosition
        copiedShip.startingPosition = this.startingPosition
        copiedShip.hasMoved = this.hasMoved
        copiedShip.justMoved = this.justMoved
        return copiedShip
    }
}

data class Warper (override val id: Int) : Ship(id){
    override val name = R.string.warper.toString()
    override val nameId = R.string.warper
    override val type = ShipType.WARPER
    override val firepower = 2
    override val hp = 1
    override val priority = 1
    override val descriptionId = R.string.warperInfo
    override fun deepCopy(): Warper {
        val copiedShip = Warper(id)
        copiedShip.currentPosition = this.currentPosition
        copiedShip.startingPosition = this.startingPosition
        copiedShip.hasMoved = this.hasMoved
        copiedShip.justMoved = this.justMoved
        return copiedShip
    }
}

enum class ShipType{
    CRUISER, DESTROYER, GHOST, WARPER
}

val mapOfShips = mapOf(ShipType.CRUISER to Cruiser(0), ShipType.DESTROYER to Destroyer(0), ShipType.GHOST to Ghost(0), ShipType.WARPER to Warper(0))