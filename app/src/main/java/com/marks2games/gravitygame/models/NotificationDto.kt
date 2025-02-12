package com.marks2games.gravitygame.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val message: NotificationHead
)

@Serializable
data class NotificationHead(
    val token: String,
    val notification: NotificationContent,
    val data: NotificationData
)

@Serializable
data class NotificationContent(
    val title: String,
    val body: String
)

@Serializable
data class NotificationData(
    val roomId: String
)
