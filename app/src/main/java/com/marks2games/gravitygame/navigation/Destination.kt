package com.marks2games.gravitygame.navigation


interface Destination {
    val route: String
}

object SelectArmy : Destination {
    override val route = "SelectArmy"
}

object Battle : Destination {
    override val route = "Battle"
}

object SelectMap : Destination {
    override val route = "SelectArmy"
}

object MainMenu: Destination{
    override val route = "MainMenu"
}

object Settings: Destination{
    override val route = "Settings"
}

object Account: Destination{
    override val route = "Account"
}

object Statistics: Destination{
    override val route = "Statistics"
}

object Achievements: Destination{
    override val route = "Achievements"
}

enum class Destinations(name: Destination?){
    SELECTARMY(name = SelectArmy),
    BATTLEMAP(name = Battle),
    SELECTMAP(name = SelectMap),
    MAINMENU(name = MainMenu),
    SETTINGS(name = Settings),
    ACCOUNT(name = Account),
    STATISTICS(name = Statistics),
    ACHIEVEMENTS(name = Achievements)
}