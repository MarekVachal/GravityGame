package com.marks2games.gravitygame.building_game.domain

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.marks2games.gravitygame.building_game.ui.screen.EmpireOverview
import com.marks2games.gravitygame.building_game.ui.screen.TradeScreen
import com.marks2games.gravitygame.building_game.ui.screen.TransportScreen
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TradeViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.data.navigation.NavRoute
import com.marks2games.gravitygame.core.domain.navigation.Destinations

fun NavGraphBuilder.builderNavGraph(
    navController: NavController,
    empireViewModel: EmpireViewModel,
    transportModel: TransportViewModel,
    tradeModel: TradeViewModel
){

    navigation(startDestination = NavRoute.EmpireOverview.route, route = "BuilderGame"){
        composable(NavRoute.EmpireOverview.route){
            EmpireOverview(
                empireModel = empireViewModel,
                onBackButtonClicked = { navController.navigate(Destinations.MAINMENU.name) }
            )
        }
        composable(NavRoute.TransportScreen.route){
            TransportScreen(
                planets = empireViewModel.getPlanetsState(),
                transportModel = transportModel,
                updatePlanets = empireViewModel::updatePlanets
            )
        }
        composable(NavRoute.TradeScreen.route){
            TradeScreen(
                empire = empireViewModel.getEmpireState(),
                tradeModel = tradeModel,
                updateEmpire = empireViewModel::updateEmpire
            )
        }
    }
}