package com.marks2games.gravitygame.core.data.navigation

sealed class NavRoute (val route: String){
    object EmpireOverview : NavRoute("EmpireOverview")
    object TransportScreen: NavRoute("TransportScreen")
    object TradeScreen: NavRoute("TradeScreen")
}