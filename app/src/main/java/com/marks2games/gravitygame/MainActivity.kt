package com.marks2games.gravitygame

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.marks2games.gravitygame.battle_game.data.room_database.AppDatabase
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.marks2games.gravitygame.ui.theme.GravityGameTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.core.data.datasource.GoogleAuthHelper
import com.marks2games.gravitygame.core.domain.FcmToken
import com.marks2games.gravitygame.core.domain.Notification
import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.navigation.Matchmaking
import com.marks2games.gravitygame.core.data.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    @Inject
    lateinit var sharedPreferences: SharedPreferencesRepository
    @Inject
    lateinit var googleAuthHelper: GoogleAuthHelper
    private lateinit var navController: NavHostController

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
        fcmToken.retrieveAndSaveFcmToken()
        googleAuthHelper.setActivity(this)

        setContent {
            GravityGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    Routes(
                        activity = this,
                        database = roomDatabase,
                        owner = this,
                        window = window,
                        navController = navController,
                        sharedPreferences = sharedPreferences,
                        context = this
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
                "BattleGame"
            }
        Log.d("FCM", "Route: $route")
        navController.navigate(route)
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

fun loadSettings(
    context: Context,
    settingsModel: SettingViewModel,
    window: Window,
    sharedPreferences: SharedPreferencesRepository
) {
    //You have to refactor this to use UseCase
    Log.d("Settings", "loadSettings")
    CoroutineScope(Dispatchers.IO).launch {
        val showTutorial = sharedPreferences.getShowTutorial()
        val language = sharedPreferences.getLanguage()
        val keepScreenOn = sharedPreferences.getKeepScreenOn()

        withContext(Dispatchers.Main){
            settingsModel.changeShowTutorial(toShow = showTutorial)
            settingsModel.setLanguage(context = context, language = language)
            settingsModel.setChosenLanguage(language = language)
            settingsModel.changeKeepScreenOn(enabled = keepScreenOn)
            setScreenOn(enabled = keepScreenOn, window = window)
        }
    }
    Log.d("Settings", "loadSettings end")


}

private fun setScreenOn(enabled: Boolean, window: Window){
    if(enabled){
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}


