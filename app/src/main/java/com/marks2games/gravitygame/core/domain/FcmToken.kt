package com.marks2games.gravitygame.core.domain

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class FcmToken @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
){
    fun retrieveAndSaveFcmToken() {
        val playerId = auth.uid ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Fetching token failed, retrieveAndSaveFcmToken", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "FCM Token retrieved: $token")

            checkAndSaveFcmToken(playerId, token)
        }
    }

    fun handleNewToken(newToken: String) {
        val playerId = auth.uid ?: return
        Log.d("FCM", "Handling new token for playerId: $playerId")
        checkAndSaveFcmToken(playerId, newToken)
    }

    private fun checkAndSaveFcmToken(playerId: String, newToken: String) {
        val tokenRef = firestore.collection("fcmTokens").document(playerId)

        tokenRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val storedToken = snapshot.getString("token")
                    if (storedToken == newToken) {
                        Log.d("FCM", "Token is already up to date, no need to update.")
                    } else {
                        saveFcmTokenToDatabase(tokenRef, newToken)
                    }
                } else {
                    Log.d("FCM", "No token found for player. Creating a new record.")
                    saveFcmTokenToDatabase(tokenRef, newToken)
                }
            }
            .addOnFailureListener { error ->
                Log.e("FCM", "Failed to check existing FCM token: ${error.message}")
            }
    }

    private fun saveFcmTokenToDatabase(tokenRef: DocumentReference, token: String) {
        val fcmTokenData = mapOf(
            "token" to token,
            "timestamp" to FieldValue.serverTimestamp()
        )

        tokenRef.set(fcmTokenData)
            .addOnSuccessListener {
                Log.d("FCM", "FCM token successfully saved/updated.")
            }
            .addOnFailureListener { error ->
                Log.e("FCM", "Failed to save FCM token: ${error.message}")
            }
    }

}