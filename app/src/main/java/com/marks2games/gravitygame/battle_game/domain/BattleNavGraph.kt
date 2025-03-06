package com.marks2games.gravitygame.battle_game.domain

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.marks2games.gravitygame.battle_game.data.room_database.DatabaseViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleMapScreen
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.matchmakingScreen.MatchmakingScreen
import com.marks2games.gravitygame.battle_game.ui.screens.matchmakingScreen.MatchmakingViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen.SelectArmyScreen
import com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.selectMapScreen.SelectMapScreen
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.core.domain.authentication.GoogleSign
import com.marks2games.gravitygame.core.domain.navigation.Destinations
import com.marks2games.gravitygame.core.domain.navigation.Matchmaking
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountScreen
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountViewModel
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuScreen
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingScreen
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticScreen
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticViewModel

fun NavGraphBuilder.battleNavGraph (
    activity: Activity,
    navController: NavHostController,
    googleSign: GoogleSign,
    context: Context,
    databaseModel: DatabaseViewModel,
    battleModel: BattleViewModel,
    selectArmyModel: SelectArmyViewModel,
    timerModel: TimerViewModel,
    tutorialModel: TutorialViewModel,
    settingModel: SettingViewModel,
    mainMenuModel: MainMenuViewModel,
    statisticModel: StatisticViewModel,
    matchmakingModel: MatchmakingViewModel,
    accountModel: AccountViewModel,
){

    navigation(startDestination = Destinations.MAINMENU.name, route = "BattleGame") {
        composable(route = "${Destinations.MATCHMAKING.name}?roomId={roomId}",
            arguments = listOf(navArgument(Matchmaking.ROOM_ID_ARG) { nullable = true })
        ) { backStackEntry ->
            MatchmakingScreen(
                matchmakingModel = matchmakingModel,
                onMatchConfirmed = {
                    navController.navigate(Destinations.BATTLEMAP.name)
                    Log.d("Player", "Player: ${battleModel.playerData.value.player}")
                },
                timerModel = timerModel,
                onBackMainMenuScreen = {
                    navController.navigate(Destinations.MAINMENU.name)
                    matchmakingModel.showSignInDialog(false)
                },
                context = context,
                googleSign = googleSign,
                roomId = backStackEntry.arguments?.getString(Matchmaking.ROOM_ID_ARG)
            )
        }
        composable(route = Destinations.SELECTARMY.name) {
            SelectArmyScreen(
                onOfflineButtonClicked = { navController.navigate(Destinations.BATTLEMAP.name) },
                onOnlineButtonClicked = { navController.navigate(Destinations.MATCHMAKING.name) },
                battleModel = battleModel,
                selectArmyModel = selectArmyModel,
                tutorialModel = tutorialModel,
                context = context,
                settingsModel = settingModel,
                onBackButtonClick = { navController.navigate(Destinations.MAINMENU.name) },
            )
        }

        composable(route = Destinations.BATTLEMAP.name) {
            BattleMapScreen(
                battleModel = battleModel,
                timerModel = timerModel,
                tutorialModel = tutorialModel,
                endOfGame = { navController.navigate(Destinations.MAINMENU.name) },
                settingsModel = settingModel,
                context = context,
                databaseModel = databaseModel,
                selectArmyModel = selectArmyModel
            )
        }

        composable(route = Destinations.SELECTMAP.name) {
            SelectMapScreen(
                battleModel = battleModel,
                onNextButtonClicked = { navController.navigate(Destinations.SELECTARMY.name) }
            )
        }

        composable(route = Destinations.MAINMENU.name){
            MainMenuScreen(
                onBattleButtonClick = {navController.navigate(Destinations.SELECTMAP.name)},
                mainMenuModel = mainMenuModel,
                activity = activity,
                context = context,
                onSettingClick = { navController.navigate(Destinations.SETTINGS.name) },
                onAccountClick = { navController.navigate(Destinations.ACCOUNT.name) },
                onEmpireButtonClick = { navController.navigate("BuilderGame") },
                battleModel = battleModel,
                googleSign = googleSign
            )
        }

        composable(route = Destinations.SETTINGS.name){
            SettingScreen(
                context = context,
                onBackButtonClick = { navController.navigate(Destinations.MAINMENU.name) },
                settingModel = settingModel
            )
        }

        composable(route = Destinations.ACCOUNT.name){
            AccountScreen(
                onStatisticButtonClick = { navController.navigate(Destinations.STATISTICS.name) },
                //onAchievementsButtonClick = { navController.navigate(Destinations.ACHIEVEMENTS.name) },
                onBackButtonClick = { navController.navigate(Destinations.MAINMENU.name) },
                accountModel = accountModel,
                context = context,
                googleSign = googleSign
            )
        }

        composable(route = Destinations.STATISTICS.name){
            StatisticScreen(
                databaseModel = databaseModel,
                onBackButtonClick = { navController.navigate(Destinations.MAINMENU.name) },
                statisticModel = statisticModel,
                context = context
            )
        }
    }
}