package com.marks2games.gravitygame.core.domain

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class FcmToken @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun registerInstallation() {
        if (auth.uid == null) return
        FirebaseMessaging.getInstance().register()
            .addOnFailureListener { error ->
                Log.e("FCM", "FCM installation registration failed", error)
            }
        // register() returns Task<Void>; the registered FID arrives in onRegistered().
    }

    fun handleRegistered(installationId: String) {
        val playerId = auth.uid ?: return
        if (installationId.isBlank()) return

        // Refresh on every callback, including when the FID has not changed.
        // The existing collection is retained so deployed readers keep working.
        val registration = mapOf(
            "fid" to installationId,
            // HTTP v1's legacy token target also accepts FIDs during migration.
            // Replace the old token so older app versions can still send to this player.
            "token" to installationId,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("fcmTokens").document(playerId)
            .set(registration, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("FCM", "FCM installation registration saved")
            }
            .addOnFailureListener { error ->
                Log.e("FCM", "Failed to save FCM installation registration", error)
            }
    }

    fun handleUnregistered(installationId: String) {
        val playerId = auth.uid ?: return
        val registrationRef = firestore.collection("fcmTokens").document(playerId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(registrationRef)
            // A delayed callback must not remove a newer registration or another device.
            if (snapshot.getString("fid") == installationId) {
                val changes = mutableMapOf<String, Any>(
                    "fid" to FieldValue.delete(),
                    "timestamp" to FieldValue.serverTimestamp()
                )
                if (snapshot.getString("token") == installationId) {
                    changes["token"] = FieldValue.delete()
                }
                transaction.update(registrationRef, changes)
            }
        }.addOnFailureListener { error ->
            Log.e("FCM", "Failed to remove FCM installation registration", error)
        }
    }
}
