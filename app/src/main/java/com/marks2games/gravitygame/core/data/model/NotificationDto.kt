package com.marks2games.gravitygame.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val message: NotificationHead
)

@Serializable
data class NotificationHead(
    val notification: NotificationContent,
    val data: NotificationData,
    val fid: String? = null,
    // Only for recipients that have not migrated to Installation ID yet.
    val token: String? = null
) {
    init {
        require((!fid.isNullOrBlank()) xor (!token.isNullOrBlank())) {
            "Exactly one FCM recipient (fid or token) is required"
        }
        require(fid == null || token == null) {
            "FCM fid and token are mutually exclusive"
        }
    }

    companion object {
        fun forRecipient(
            installationId: String?,
            legacyToken: String?,
            notification: NotificationContent,
            data: NotificationData
        ): NotificationHead {
            val fid = installationId?.takeIf { it.isNotBlank() }
            return NotificationHead(
                notification = notification,
                data = data,
                fid = fid,
                token = if (fid == null) legacyToken?.takeIf { it.isNotBlank() } else null
            )
        }
    }
}

@Serializable
data class NotificationContent(
    val title: String,
    val body: String
)

@Serializable
data class NotificationData(
    val roomId: String
)
