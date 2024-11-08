package com.example.gravitygame

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.gravitygame.database.AppDatabase
import com.example.gravitygame.database.BattleRepository
import com.example.gravitygame.database.DatabaseViewModel
import com.example.gravitygame.database.ViewModelFactory
import com.example.gravitygame.navigation.Destinations
import com.example.gravitygame.timer.TimerViewModel
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.ui.screens.accountScreen.AccountScreen
import com.example.gravitygame.ui.screens.battleMapScreen.BattleMapScreen
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.example.gravitygame.ui.screens.mainMenuScreen.MainMenuScreen
import com.example.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel
import com.example.gravitygame.ui.screens.selectArmyScreen.SelectArmyScreen
import com.example.gravitygame.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.example.gravitygame.ui.screens.selectMapScreen.SelectMapScreen
import com.example.gravitygame.ui.screens.settingScreen.SettingScreen
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.example.gravitygame.ui.screens.statisticScreen.StatisticScreen
import com.example.gravitygame.ui.screens.statisticScreen.StatisticViewModel
import com.example.gravitygame.ui.theme.GravityGameTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        closeAndroidBars(window = window)
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "battle_results.db"
        ).build()

        setContent {
            GravityGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenSetup(activity = this, database = database, owner = this)
                }
            }
        }
        FirebaseApp.initializeApp(this)
    }
}

@Composable
fun ScreenSetup(activity: Activity, database: AppDatabase, owner: ViewModelStoreOwner) {

    val navController: NavHostController = rememberNavController()
    val repository = BattleRepository(database.battleResultDao())
    val databaseModel: DatabaseViewModel = ViewModelProvider(
        owner, ViewModelFactory(repository)
    )[DatabaseViewModel::class.java]
    val battleModel: BattleViewModel = viewModel()
    val selectArmyModel: SelectArmyViewModel = viewModel()
    val timerModel: TimerViewModel = viewModel()
    val tutorialModel: TutorialViewModel = viewModel()
    val settingModel: SettingViewModel = viewModel()
    val mainMenuModel: MainMenuViewModel = viewModel()
    val statisticModel: StatisticViewModel = viewModel()
    val context = LocalContext.current
    loadSettings(context = context, settingsModel = settingModel)


    NavHost(navController = navController, startDestination = Destinations.MAINMENU.name) {
        composable(route = Destinations.SELECTARMY.name) {
            SelectArmyScreen(
                onNextButtonClicked = {
                    navController.navigate(Destinations.BATTLEMAP.name)
                },
                battleModel = battleModel,
                selectArmyModel = selectArmyModel,
                tutorialModel = tutorialModel,
                context = context,
                settingsModel = settingModel
            )
        }

        composable(route = Destinations.BATTLEMAP.name) {
            BattleMapScreen(
                battleModel = battleModel,
                timerModel = timerModel,
                tutorialModel = tutorialModel,
                endOfGame = {
                    navController.navigate(Destinations.MAINMENU.name)
                    battleModel.showEndOfGameDialog(false)
                    timerModel.cancelTimer()
                    battleModel.changeEndOfGameState(false)
                },
                settingsModel = settingModel,
                context = context,
                databaseModel = databaseModel
            )
        }

        composable(route = Destinations.SELECTMAP.name) {
            SelectMapScreen(
                battleModel = battleModel,
                onNextButtonClicked = {
                    navController.navigate(Destinations.SELECTARMY.name)
                }
            )
        }

        composable(route = Destinations.MAINMENU.name){
            MainMenuScreen(
                onBattleButtonClick = {navController.navigate(Destinations.SELECTMAP.name)},
                mainMenuModel = mainMenuModel,
                activity = activity,
                context = context,
                onSettingClick = { navController.navigate(Destinations.SETTINGS.name) },
                onAccountClick = { navController.navigate(Destinations.ACCOUNT.name) }
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
                onAchievementsButtonClick = { navController.navigate(Destinations.ACHIEVEMENTS.name) }
            )
        }

        composable(route = Destinations.STATISTICS.name){
            StatisticScreen(
                databaseModel = databaseModel,
                onBackButtonClick = {
                    navController.navigate(Destinations.MAINMENU.name)
                },
                statisticModel = statisticModel,
                context = context
            )
        }
    }
}

@Suppress("DEPRECATION")
private fun closeAndroidBars(window: Window){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.decorView.windowInsetsController?.hide(
            android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars()
        )
    } else {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )
            }
        }
    }
}


private fun loadSettings(context: Context, settingsModel: SettingViewModel) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppSettings", Context.MODE_PRIVATE)
    val showTutorial = sharedPreferences.getBoolean("ShowTutorial", true)
    val language = sharedPreferences.getString("language", "en")?: ""
    settingsModel.changeShowTutorial(toShow = showTutorial, context = context)
    settingsModel.setLanguage(context = context, language = language)
    settingsModel.setChosenLanguage(language = language)
}