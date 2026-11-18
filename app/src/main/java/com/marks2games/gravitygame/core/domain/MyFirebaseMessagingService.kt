package com.marks2games.gravitygame.core.domain

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.marks2games.gravitygame.MainActivity
import com.marks2games.gravitygame.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmToken

    override fun onRegistered(installationId: String) {
        fcmTokenManager.handleRegistered(installationId)
    }

    override fun onUnregistered(installationId: String) {
        fcmTokenManager.handleUnregistered(installationId)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val isOpponentFound = remoteMessage.data["type"] == "OPPONENT_FOUND"
        val title = remoteMessage.notification?.title ?: getString(
            if (isOpponentFound) R.string.notificationTitleOpponentFound else R.string.newMessage
        )
        val body = remoteMessage.notification?.body ?: getString(
            if (isOpponentFound) R.string.notificationBodyOpponentFound else R.string.clickToOpenScreen
        )
        val roomId = remoteMessage.data["roomId"]

        Log.d("FCM", "Notification received: Title: $title, Body: $body, RoomId: $roomId")

        val intent = Intent(this, MainActivity::class.java).apply {
            //putExtra("destination", Destinations.MATCHMAKING.name)
            putExtra("roomId", roomId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.logo_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED){
                NotificationManagerCompat.from(this).notify(0, notification)
            } else {
                /*
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
                
                 */
            }
        } else {
            NotificationManagerCompat.from(this).notify(0, notification)
        }
    }
}