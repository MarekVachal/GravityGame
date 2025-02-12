package com.marks2games.gravitygame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.marks2games.gravitygame.database.AppDatabase
import com.marks2games.gravitygame.database.BattleRepository
import com.marks2games.gravitygame.database.DatabaseViewModel
import com.marks2games.gravitygame.database.ViewModelFactory
import com.marks2games.gravitygame.navigation.Destinations
import com.marks2games.gravitygame.timer.TimerViewModel
import com.marks2games.gravitygame.tutorial.TutorialViewModel
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountScreen
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleMapScreen
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuScreen
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel
import com.marks2games.gravitygame.ui.screens.selectArmyScreen.SelectArmyScreen
import com.marks2games.gravitygame.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.marks2games.gravitygame.ui.screens.selectMapScreen.SelectMapScreen
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingScreen
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticScreen
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticViewModel
import com.marks2games.gravitygame.ui.theme.GravityGameTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.firebase.FcmToken
import com.marks2games.gravitygame.firebase.Notification
import com.marks2games.gravitygame.navigation.Matchmaking
import com.marks2games.gravitygame.signIn.GoogleSign
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountViewModel
import com.marks2games.gravitygame.ui.screens.matchmakingScreen.MatchmakingScreen
import com.marks2games.gravitygame.ui.screens.matchmakingScreen.MatchmakingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var database: FirebaseDatabase
    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var fcmToken: FcmToken
    @Inject
    lateinit var notification: Notification
    private lateinit var navController: NavHostController
    private lateinit var googleSign: GoogleSign

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        closeAndroidBars(window = window)
        requestNotificationPermission()
        val roomDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "battle_results.db"
        ).build()
        notification.createNotificationChannel(this)
        googleSign = GoogleSign(context = this, auth = auth)
        fcmToken.retrieveAndSaveFcmToken()


        setContent {
            GravityGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    navController = rememberNavController()
                    ScreenSetup(
                        activity = this,
                        database = roomDatabase,
                        owner = this,
                        window = window,
                        navController = navController,
                        googleSign = googleSign
                    )
                    handleIntent(intent)
                }
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Log.d("FCM", "Intent: $intent")
            val roomId = intent?.getStringExtra("roomId")
            Log.d("FCM", "Room Id in Extra: $roomId")
            val route = if (!roomId.isNullOrEmpty()) {
                "${Matchmaking.route}?roomId=$roomId"
            } else {
                Destinations.MAINMENU.name
            }
        Log.d("FCM", "Route: $route")
        navController.navigate(route)
    }
}



@Composable
fun ScreenSetup(
    activity: Activity,
    database: AppDatabase,
    owner: ViewModelStoreOwner,
    window: Window,
    navController: NavHostController,
    googleSign: GoogleSign
) {
    val context = LocalContext.current
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
    val matchmakingModel: MatchmakingViewModel = viewModel()
    val accountModel: AccountViewModel = viewModel()

    LaunchedEffect(Unit) {
        loadSettings(
            context = context,
            settingsModel = settingModel,
            window = window
        )
    }

    NavHost(navController = navController, startDestination = Destinations.MAINMENU.name) {
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
                endOfGame = {
                    navController.navigate(Destinations.MAINMENU.name)
                },
                showBattleResultMap = {
                    battleModel.showEndOfGameDialog(false)
                    battleModel.showEndOfGameViaCapitulationDialog(false)
                },
                settingsModel = settingModel,
                context = context,
                databaseModel = databaseModel,
                selectArmyModel = selectArmyModel
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
                onAccountClick = { navController.navigate(Destinations.ACCOUNT.name) },
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
            WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
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

private fun loadSettings(
    context: Context,
    settingsModel: SettingViewModel,
    window: Window
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppSettings", Context.MODE_PRIVATE)
    val showTutorial = sharedPreferences.getBoolean("ShowTutorial", true)
    val language = sharedPreferences.getString("language", "en")?: ""
    val keepScreenOn = sharedPreferences.getBoolean("keepScreenOn", true)
    settingsModel.changeShowTutorial(toShow = showTutorial, context = context)
    settingsModel.setLanguage(context = context, language = language)
    settingsModel.setChosenLanguage(language = language)
    settingsModel.changeKeepScreenOn(enabled = keepScreenOn, context = context)
    setScreenOn(enabled = keepScreenOn, window = window)

}

private fun setScreenOn(enabled: Boolean, window: Window){
    if(enabled){
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}


