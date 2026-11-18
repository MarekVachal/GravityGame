package com.marks2games.gravitygame.core.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import javax.inject.Inject

/**
 * Local notification setup. Remote match notifications are sent by the backend
 * when a second player joins a room.
 */
class Notification @Inject constructor() {
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "default_channel",
            "Default Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel description"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
