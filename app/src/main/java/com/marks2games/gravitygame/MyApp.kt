package com.marks2games.gravitygame

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp
import io.sentry.Sentry
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var database: FirebaseDatabase
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        FirebaseApp.initializeApp(this)
        //PlayGamesSdk.initialize(this)
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override fun onActivityResumed(p0: Activity) {
        Log.d("AppState", "App is in foreground")
        updateState(PlayerState.ACTIVE)
    }

    override fun onActivityPaused(p0: Activity) {
        Log.d("AppState", "App is in background")
        updateState(PlayerState.BACKGROUND)
    }

    private fun updateState(state: PlayerState){
        val playerId = auth.uid ?: return
        val playerStateRef = database.reference
            .child("players")
            .child(playerId)
            .child("state")
        playerStateRef.setValue(state.name)
            .addOnSuccessListener {
                Log.d("AppState", "State updated to $state")
            }
            .addOnFailureListener { e ->
                Log.d("AppState", "Failed to update state ${e.message}")
                Sentry.captureException(e)
            }

    }
    override fun onActivityDestroyed(p0: Activity) { }
    override fun onActivityCreated(p0: Activity, p1: Bundle?) { }
    override fun onActivityStarted(p0: Activity) { }
    override fun onActivityStopped(p0: Activity) { }
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) { }
}

enum class PlayerState{
    ACTIVE,
    BACKGROUND,
    DEAD
}