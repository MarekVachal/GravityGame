package com.marks2games.gravitygame.building_game.domain

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.ui.screen.EmpireOverview
import com.marks2games.gravitygame.building_game.ui.screen.PlanetScreen
import com.marks2games.gravitygame.building_game.ui.screen.ResearchScreen
import com.marks2games.gravitygame.building_game.ui.screen.TradeScreen
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.ResearchViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TradeViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.data.navigation.NavRoute
import com.marks2games.gravitygame.core.domain.navigation.Destinations

fun NavGraphBuilder.builderNavGraph(
    navController: NavController,
    empireViewModel: EmpireViewModel,
    transportModel: TransportViewModel,
    tradeModel: TradeViewModel,
    researchModel: ResearchViewModel,
    planetModel: PlanetViewModel
){

    navigation(startDestination = NavRoute.EmpireOverview.route, route = "BuilderGame"){
        composable(NavRoute.EmpireOverview.route){
            EmpireOverview(
                empireModel = empireViewModel,
                transportModel = transportModel,
                onBackButtonClicked = { navController.navigate(Destinations.MAINMENU.name) },
                toResearchScreen = { navController.navigate(NavRoute.ResearchScreen.route) },
                toPlanetScreen = { planetId: Int?, empire: Empire, testEmpire: Empire ->
                    planetModel.launchPlanetScreen(planetId, empire, testEmpire)
                    navController.navigate(NavRoute.PlanetScreen.route)
                }
            )
        }
        composable(NavRoute.TradeScreen.route){
            TradeScreen(
                tradeModel = tradeModel,
            )
        }
        composable(NavRoute.ResearchScreen.route){
            ResearchScreen(
                empireModel = empireViewModel,
                researchModel = researchModel,
                toEmpireScreenClicked = { navController.navigate(NavRoute.EmpireOverview.route) }
            )
        }
        composable(NavRoute.PlanetScreen.route){
            PlanetScreen(
                planetModel = planetModel,
                transportModel = transportModel,
                onBackButtonClicked = { planetId: Int?, actions: List<Action> ->
                    empireViewModel.updateActionsAfterBackToEmpireScreen(planetId, actions)
                    navController.navigate(NavRoute.EmpireOverview.route)
                },
                toResearchScreen = { navController.navigate(NavRoute.ResearchScreen.route) },
                toEmpireScreen = { planetId: Int?, actions: List<Action> ->
                    empireViewModel.updateActionsAfterBackToEmpireScreen(planetId, actions)
                    navController.navigate(NavRoute.EmpireOverview.route)
                }
            )
        }
    }
}