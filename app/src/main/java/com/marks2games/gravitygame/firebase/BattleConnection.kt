package com.marks2games.gravitygame.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.Players
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BattleConnection(
    private val roomRef: DatabaseReference,
    private val player: Players
){
    suspend fun sendUpdatedLocations(
        updatedLocations: List<Location>,
        onOpponentReady: (SimplifiedMove) -> Unit
    ): Unit = suspendCoroutine{ continuation ->
        val myMove = updatedLocations.toSimplifiedMove()
        val playerKey = when(player){
            Players.PLAYER1 -> "player1LocationList"
            Players.PLAYER2 -> "player2LocationList"
            Players.NONE -> null
        }

        if (playerKey == null) {
            Log.e("UpdatingLocations", "Invalid player key. Aborting.")
            return@suspendCoroutine
        }

        val update = mapOf(playerKey to myMove)
        roomRef.updateChildren(update)
            .addOnSuccessListener {
                Log.d("UpdatingLocations", "Successfully updated $playerKey: $myMove")
            }
            .addOnFailureListener { error ->
                Log.e("UpdatingLocations", "Failed to update $playerKey: ${error.message}")
                Sentry.captureException(error)
                continuation.resumeWith(Result.failure(error))
            }

        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(Room::class.java)
                if (room == null) {
                    Log.e("UpdatingLocations", "Room data is null. Aborting listener.")
                    roomRef.removeEventListener(this)
                    return
                }

                val opponentLocations = if (playerKey == "player1LocationList"){
                    room.player2LocationList
                } else {
                    room.player1LocationList
                }
                if (opponentLocations != null) {
                    Log.d("UpdatingLocations", "Opponent's location list received: $opponentLocations")
                    onOpponentReady(opponentLocations)
                    cleanLocationLists()
                    roomRef.removeEventListener(this)
                    continuation.resume(Unit)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpdatingLocations", "Firebase listener cancelled: ${error.message}")
                Sentry.captureException(error.toException())
                continuation.resumeWith(Result.failure(error.toException()))
            }
        })
    }

    fun cleanLocationLists(){
        val updates = mapOf(
            "player1LocationList" to null,
            "player2LocationList" to null
        )
        roomRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("UpdatingLocations", "Cleared location lists in Firebase.")
            }
            .addOnFailureListener { error ->
                Log.e("UpdatingLocations", "Failed to clear location lists: ${error.message}")
                Sentry.captureException(error)
            }
    }

    fun capitulate() {
        val capitulationField = when(player){
            Players.PLAYER1 -> "player1Capitulated"
            Players.PLAYER2 -> "player2Capitulated"
            Players.NONE -> return
        }
        roomRef.child("capitulation").child(capitulationField).setValue(true)
            .addOnSuccessListener {
                Log.d("Capitulation", "Player $player capitulated.")
            }
            .addOnFailureListener { e ->
                Log.e("Capitulation", "Failed to set capitulation: ${e.message}")
            }
    }

    fun listenForCapitulation(onOpponentCapitulated: () -> Unit) {
        val child = if(player == Players.PLAYER1){
            "player2Capitulated"
        } else {
            "player1Capitulated"
        }
        roomRef.child("capitulation").child(child).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Capitulation", "Listen triggered.")
                val capitulated = snapshot.getValue(Boolean::class.java) ?: false
                Log.d("Capitulation", "Value: $capitulated")
                if (capitulated) {
                    Log.d("Capitulation", "Opponent capitulated.")
                    onOpponentCapitulated()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Capitulation", "Failed to listen for capitulation: ${error.message}")
            }
        })
    }
}