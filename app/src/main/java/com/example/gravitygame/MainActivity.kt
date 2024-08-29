package com.example.gravitygame

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravitygame.navigation.Destination
import com.example.gravitygame.timer.CoroutineTimer
import com.example.gravitygame.timer.TimerViewModel
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.ui.screens.battleMapScreen.BattleMapScreen
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.example.gravitygame.ui.screens.mainMenuScreen.MainMenuScreen
import com.example.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel
import com.example.gravitygame.ui.screens.selectArmyScreen.SelectArmyScreen
import com.example.gravitygame.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.example.gravitygame.ui.screens.selectMapScreen.SelectMapScreen
import com.example.gravitygame.ui.screens.settingScreen.SettingScreen
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.example.gravitygame.ui.theme.GravityGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        closeAndroidBars(window = window)
        setContent {
            GravityGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenSetup(activity = this)
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(activity: Activity) {
    val navController: NavHostController = rememberNavController()
    val battleModel: BattleViewModel = viewModel()
    val selectArmyModel: SelectArmyViewModel = viewModel()
    val timerModel: TimerViewModel = viewModel()
    val tutorialModel: TutorialViewModel = viewModel()
    val settingModel: SettingViewModel = viewModel()
    val mainMenuModel: MainMenuViewModel = viewModel()
    var timer: CoroutineTimer? = null
    val context = LocalContext.current
    loadSettings(context = context, settingsModel = settingModel)
    timer = CoroutineTimer(
        timerModel = timerModel,
        finishTurn = { timer?.let { battleModel.finishTurn(timer = it) } })


    NavHost(navController = navController, startDestination = Destination.MAINMENU.name) {
        composable(route = Destination.SELECTARMY.name) {
            SelectArmyScreen(
                onNextButtonClicked = { navController.navigate(Destination.BATTLEMAP.name)},
                battleModel = battleModel,
                selectArmyModel = selectArmyModel,
                tutorialModel = tutorialModel,
                context = context,
                settingsModel = settingModel
            )
        }
        composable(route = Destination.BATTLEMAP.name) {
            BattleMapScreen(
                battleModel = battleModel,
                timerModel = timerModel,
                tutorialModel = tutorialModel,
                timer = timer,
                endOfGame = {
                    navController.navigate(Destination.MAINMENU.name)
                    battleModel.showEndOfGameDialog(false)
                },
                settingsModel = settingModel,
                context = context
            )
        }
        composable(route = Destination.SELECTMAP.name) {
            SelectMapScreen(
                battleModel = battleModel,
                onNextButtonClicked = { navController.navigate(Destination.SELECTARMY.name) }
            )

        }
        composable(route = Destination.MAINMENU.name){
            MainMenuScreen(
                onBattleButtonClick = {navController.navigate(Destination.SELECTMAP.name)},
                mainMenuModel = mainMenuModel,
                activity = activity,
                onSettingClick = { navController.navigate(Destination.SETTINGS.name) }
            )
        }
        composable(route = Destination.SETTINGS.name){
            SettingScreen(
                context = context,
                onBackButtonClick = { navController.navigate(Destination.MAINMENU.name) },
                settingModel = settingModel
            )
        }
    }
}

fun closeAndroidBars(window: Window){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val insetsController = window.insetsController
        if (insetsController != null) {
            insetsController.hide(
                android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars()
            )
            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        @Suppress("DEPRECATION")
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

fun loadSettings(context: Context, settingsModel: SettingViewModel) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppSettings", Context.MODE_PRIVATE)
    val showTutorial = sharedPreferences.getBoolean("ShowTutorial", true)
    val language = sharedPreferences.getString("language", "en")?: ""
    settingsModel.changeShowTutorial(toShow = showTutorial, context = context)
    settingsModel.setLanguage(context = context, language = language)
    settingsModel.setChosenLanguage(language = language)
}