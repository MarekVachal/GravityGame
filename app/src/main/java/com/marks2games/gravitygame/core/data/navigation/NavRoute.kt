package com.marks2games.gravitygame.core.data.navigation

sealed class NavRoute (val route: String){
    data object EmpireOverview : NavRoute("EmpireOverview")
    data object TradeScreen: NavRoute("TradeScreen")
    data object ResearchScreen: NavRoute("ResearchScreen")
}