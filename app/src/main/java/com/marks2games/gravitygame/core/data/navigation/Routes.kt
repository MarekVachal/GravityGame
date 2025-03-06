package com.marks2games.gravitygame.core.data.navigation

import android.app.Activity
import android.content.Context
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.marks2games.gravitygame.battle_game.data.room_database.AppDatabase
import com.marks2games.gravitygame.battle_game.data.room_database.BattleRepository
import com.marks2games.gravitygame.battle_game.data.room_database.DatabaseViewModel
import com.marks2games.gravitygame.battle_game.data.room_database.ViewModelFactory
import com.marks2games.gravitygame.battle_game.domain.battleNavGraph
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.matchmakingScreen.MatchmakingViewModel
import com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.building_game.domain.builderNavGraph
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TradeViewModel
import com.marks2games.gravitygame.building_game.ui.viewmodel.TransportViewModel
import com.marks2games.gravitygame.core.data.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.authentication.GoogleSign
import com.marks2games.gravitygame.loadSettings
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountViewModel
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticViewModel

@Composable
fun Routes(
    activity: Activity,
    database: AppDatabase,
    owner: ViewModelStoreOwner,
    window: Window,
    navController: NavHostController,
    googleSign: GoogleSign,
    sharedPreferences: SharedPreferencesRepository,
    context: Context
) {
    val tradeModel: TradeViewModel = viewModel()
    val transportModel: TransportViewModel = viewModel()
    val planetModel: PlanetViewModel = viewModel()
    val empireViewModel: EmpireViewModel = viewModel()
    val repository = BattleRepository(database.battleResultDao())
    val databaseModel: DatabaseViewModel = ViewModelProvider(
        owner, ViewModelFactory(repository)
    )[DatabaseViewModel::class.java]
    val battleModel: BattleViewModel = viewModel()
    val selectArmyModel: SelectArmyViewModel = viewModel()
    val timerModelForBattle: TimerViewModel = viewModel()
    val tutorialModel: TutorialViewModel = viewModel()
    val settingModel: SettingViewModel = viewModel()
    val mainMenuModel: MainMenuViewModel = viewModel()
    val statisticModel: StatisticViewModel = viewModel()
    val matchmakingModel: MatchmakingViewModel = viewModel()
    val accountModel: AccountViewModel = viewModel()
    val timerModelForBuilder: TimerViewModel = viewModel()

    LaunchedEffect(Unit) {
        loadSettings(
            context = context,
            settingsModel = settingModel,
            window = window,
            sharedPreferences = sharedPreferences
        )
    }

    NavHost(navController, startDestination = "BattleGame") {
        battleNavGraph(
            activity = activity,
            navController = navController,
            googleSign = googleSign,
            context = context,
            databaseModel = databaseModel,
            battleModel = battleModel,
            selectArmyModel = selectArmyModel,
            timerModel = timerModelForBattle,
            tutorialModel = tutorialModel,
            settingModel = settingModel,
            mainMenuModel = mainMenuModel,
            statisticModel = statisticModel,
            matchmakingModel = matchmakingModel,
            accountModel = accountModel
        )
        builderNavGraph(
            navController = navController,
            empireViewModel = empireViewModel,
            timerModel = timerModelForBuilder,
            planetModel = planetModel,
            transportModel = transportModel,
            tradeModel = tradeModel
        )
    }
}