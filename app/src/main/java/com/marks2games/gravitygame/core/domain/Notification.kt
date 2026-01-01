package com.marks2games.gravitygame.core.domain

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.FirebaseDatabase
import com.marks2games.gravitygame.core.data.model.enum_class.PlayerState
import com.marks2games.gravitygame.core.data.model.NotificationContent
import com.marks2games.gravitygame.core.data.model.NotificationData
import com.marks2games.gravitygame.core.data.model.NotificationDto
import com.marks2games.gravitygame.core.data.model.NotificationHead
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.sentry.Sentry
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("TrustAllX509TrustManager")
class Notification @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation){
            json()
        }
    }
    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/gravitygame-446320/messages:send"

    suspend fun sendNotificationToPlayer(roomId: String, playerId: String, title: String, body: String, context: Context){
        withContext(Dispatchers.IO){
            try {
                val playerStateRef = database.reference
                    .child("players")
                    .child(playerId)
                    .child("state")
                val snapshot = playerStateRef.get().await()
                val snapshotState = snapshot.getValue(String::class.java)
                val state = snapshotState?.toPlayerState()
                Log.d("AppState", "sendNotificationToPlayer: Player $playerId state: $state")

                if (state == PlayerState.BACKGROUND) {
                    Log.d("AppState", "sendNotificationToPlayer: Player is in background. Sending notification.")
                    sendNotification(roomId, playerId, title, body, context)
                } else {
                    Log.d("sendNotificationToPlayer", "Player is active. Notification not sent.")
                }
            } catch (e: Exception){
                Log.d("AppState", "Failed try bloc: ${e.message}")
                Sentry.captureException(e)
            }
        }

    }

    private suspend fun sendNotification(roomId: String, playerId: String, title: String, body: String, context: Context) {
        if (playerId.isBlank()) {
            Log.e("FCM", "Invalid playerId: $playerId")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                Log.d("FCM", "Fetching FCM token for playerId: $playerId")
                val fcmTokenRef = firestore.collection("fcmTokens").document(playerId)
                val snapshot = fcmTokenRef.get().await()
                val token = snapshot.getString("token")
                Log.d("FCM", "Room id in sendNotification: $roomId")

                if (token != null) {
                    val notificationData = NotificationDto(
                        message = NotificationHead(
                            token = token,
                            notification = NotificationContent(
                                title = title,
                                body = body
                            ),
                            data = NotificationData(roomId = roomId)
                        )
                    )
                    sendNotificationRequest(notificationData, context)
                } else {
                    Log.e("FCM", "Token not found for playerId: $playerId")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error fetching FCM token, sendNotificationToPlayer: ${e.message}")
                Log.e("FCM", "Stack trace: ${Log.getStackTraceString(e)}")
                Sentry.captureException(e)
            }
        }

    }


    private suspend fun sendNotificationRequest(notificationData: NotificationDto, context: Context) {
        val accessToken = getAccessToken(context = context) ?: throw Exception("Unable to get access token")

        withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.post(fcmUrl) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                    setBody(notificationData)
                }

                if (response.status.isSuccess()) {
                    Log.d("FCM", "Notification sent successfully: ${response.status}")
                } else {
                    Log.e(
                        "FCM",
                        "Failed to send notification: ${response.status} - ${response.bodyAsText()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error while sending notification: ${e.message}")
                Sentry.captureException(e)
            }
        }
    }

    private suspend fun getAccessToken(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("service-account.json")
                val credentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

                credentials.refreshIfExpired()
                Log.d("FCM", "Access token gained: ${credentials.accessToken.tokenValue}")
                credentials.accessToken.tokenValue
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("FCM", "Failed to get access token: ${e.message}")
                Sentry.captureException(e)
                null
            }
        }
    }

    fun createNotificationChannel(mContext: Context){
        val channelId = "default_channel"
        val channelName = "Default Channel"
        val description = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = description
        val context = Context.NOTIFICATION_SERVICE
        val manager = mContext.getSystemService(context) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun String.toPlayerState(): PlayerState? {
    return try {
        PlayerState.valueOf(this)
    } catch (e: IllegalArgumentException){
        null
    }
}

