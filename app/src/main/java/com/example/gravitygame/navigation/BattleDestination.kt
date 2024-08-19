package com.example.gravitygame.navigation


interface BattleDestination {
    val route: String
}

object SelectArmy : BattleDestination {
    override val route = "SelectArmy"
}

object Battle : BattleDestination {
    override val route = "Battle"
}

object SelectMap : BattleDestination {
    override val route = "SelectArmy"
}

enum class Destination(name: BattleDestination?){
    SELECTARMY(name = SelectArmy), BATTLEMAP(name = Battle), SELECTMAP(name = SelectMap)
}