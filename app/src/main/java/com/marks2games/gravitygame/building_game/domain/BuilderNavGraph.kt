package com.marks2games.gravitygame.building_game.domain

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.building_game.ui.screen.EmpireOverview
import com.marks2games.gravitygame.building_game.ui.screen.PlanetScreen
import com.marks2games.gravitygame.building_game.ui.screen.TradeScreen
import com.marks2games.gravitygame.building_game.ui.screen.TransportScreen
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TradeViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.data.navigation.NavRoute

fun NavGraphBuilder.builderNavGraph(
    navController: NavController,
    planetModel: PlanetViewModel,
    empireViewModel: EmpireViewModel,
    timerModel: TimerViewModel,
    transportModel: TransportViewModel,
    tradeModel: TradeViewModel
){

    navigation(startDestination = NavRoute.EmpireOverview.route, route = "BuilderGame"){
        composable(NavRoute.EmpireOverview.route){
            EmpireOverview(
                empireModel = empireViewModel,
                timerModel = timerModel,
                onPlanetClick = {navController.navigate(NavRoute.PlanetScreen.createRoute(it))}
            )
        }
        composable(NavRoute.PlanetScreen.route,
            arguments = listOf(navArgument("planetId") {type = NavType.IntType})
        ) { backStackEntry ->
            val planetId = backStackEntry.arguments?.getInt("planetId")
            PlanetScreen(
                planetModel = planetModel,
                empire = empireViewModel.getEmpireState(),
                makeTradepower = empireViewModel::makeTradepower,
                planetId = planetId,
                increaseExpeditions = empireViewModel::increaseExpedition,
                createArmyUnit = empireViewModel::createArmyUnit,
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