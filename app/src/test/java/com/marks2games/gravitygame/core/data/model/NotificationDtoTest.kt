package com.marks2games.gravitygame.core.data.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class NotificationDtoTest {
    private val notification = NotificationContent("Turn", "Your opponent finished their turn")
    private val data = NotificationData("room-123")

    @Test
    fun installationIdTakesPriorityAndSerializesOnlyFidTarget() {
        val message = NotificationHead.forRecipient(
            installationId = "registered-installation",
            legacyToken = "old-registration-token",
            notification = notification,
            data = data
        )
        val payload = Json.parseToJsonElement(
            Json.encodeToString(NotificationDto(message))
        ).jsonObject.getValue("message").jsonObject

        assertEquals("registered-installation", payload.getValue("fid").jsonPrimitive.content)
        assertFalse(payload.containsKey("token"))
        assertEquals("room-123", payload.getValue("data").jsonObject.getValue("roomId").jsonPrimitive.content)
        assertEquals("Turn", payload.getValue("notification").jsonObject.getValue("title").jsonPrimitive.content)
    }

    @Test
    fun unmigratedRecipientSerializesOnlyLegacyTokenTarget() {
        val message = NotificationHead.forRecipient(
            installationId = null,
            legacyToken = "legacy-registration-token",
            notification = notification,
            data = data
        )
        val payload = Json.parseToJsonElement(Json.encodeToString(message)).jsonObject

        assertEquals("legacy-registration-token", payload.getValue("token").jsonPrimitive.content)
        assertFalse(payload.containsKey("fid"))
    }

    @Test
    fun blankInstallationIdFallsBackToLegacyToken() {
        val message = NotificationHead.forRecipient(" ", "legacy-token", notification, data)

        assertEquals("legacy-token", message.token)
        assertEquals(null, message.fid)
    }

    @Test
    fun missingOrBlankTargetsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            NotificationHead.forRecipient(null, null, notification, data)
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotificationHead.forRecipient(" ", " ", notification, data)
        }
    }

    @Test
    fun mutuallyExclusiveTargetsCannotBeSentTogether() {
        assertThrows(IllegalArgumentException::class.java) {
            NotificationHead(notification, data, fid = "installation", token = "legacy-token")
        }
    }
}
