package com.marks2games.gravitygame.core.data.navigation

sealed class NavRoute (val route: String){
    object EmpireOverview : NavRoute("EmpireOverview")
    object PlanetScreen : NavRoute("PlanetScreen/{planetId}") {
        fun createRoute(planetId: Int) = "PlanetScreen/$planetId"
    }
    object TransportScreen: NavRoute("TransportScreen")
    object TradeScreen: NavRoute("TradeScreen")
}