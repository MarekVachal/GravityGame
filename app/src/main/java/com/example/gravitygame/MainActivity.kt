package com.example.gravitygame

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravitygame.ui.screen.BattleMapScreen
import com.example.gravitygame.navigation.Destination
import com.example.gravitygame.ui.screen.SelectArmyScreen
import com.example.gravitygame.ui.screen.SelectMapScreen
import com.example.gravitygame.ui.theme.GravityGameTheme
import com.example.gravitygame.ui.utils.CoroutineTimer
import com.example.gravitygame.viewModels.BattleViewModel
import com.example.gravitygame.viewModels.ProgressIndicatorViewModel
import com.example.gravitygame.viewModels.SelectArmyViewModel
import com.example.gravitygame.viewModels.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController!!.hide(
                android.view.WindowInsets.Type.statusBars()
                        or android.view.WindowInsets.Type.navigationBars()
            )
        }
        setContent {
            GravityGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenSetup()
                }
            }
        }
    }
}

@Composable
fun ScreenSetup() {
    val navController = rememberNavController()
    val battleModel: BattleViewModel = viewModel()
    val selectArmyModel: SelectArmyViewModel = viewModel()
    val progressIndicatorModel: ProgressIndicatorViewModel = viewModel()
    val timerModel: TimerViewModel = viewModel()
    var timer: CoroutineTimer? = null
    timer = CoroutineTimer(
        timerModel = timerModel,
        finishTurn = { timer?.let { battleModel.finishTurn(timer = it) } })

    NavHost(navController = navController, startDestination = Destination.SELECTMAP.name) {
        composable(route = Destination.SELECTARMY.name) {
            SelectArmyScreen(
                progressIndicatorModel = progressIndicatorModel,
                onNextButtonClicked = { navController.navigate(Destination.BATTLEMAP.name)},
                battleModel = battleModel,
                selectArmyModel = selectArmyModel
            )
        }
        composable(route = Destination.BATTLEMAP.name) {
            BattleMapScreen(
                battleModel = battleModel,
                timerModel = timerModel,
                progressIndicatorModel = progressIndicatorModel,
                timer = timer,
                endOfGame = {
                    navController.navigate(Destination.SELECTMAP.name)
                    battleModel.showEndOfGameDialog(false)
                } /* TODO Change to main screen */
            )
        }
        composable(route = Destination.SELECTMAP.name) {
            SelectMapScreen(
                battleModel = battleModel,
                onNextButtonClicked = { navController.navigate(Destination.SELECTARMY.name) }
            )

        }
    }
}