package com.marks2games.gravitygame.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.marks2games.gravitygame.firebase.models.Room
import com.marks2games.gravitygame.firebase.models.SimplifiedMove
import com.marks2games.gravitygame.models.Location
import com.marks2games.gravitygame.models.Players
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class BattleGameRepository(
    private val roomRef: DatabaseReference,
    private val player: Players,
    private val auth: FirebaseAuth
){
    suspend fun sendUpdatedLocations(
        updatedLocations: List<Location>,
        onOpponentReady: (SimplifiedMove) -> Unit,
        timeoutMillis: Long,
        onOpponentDisconnected: ()-> Unit
    ): Unit = withContext(Dispatchers.IO) {
        val myMove = updatedLocations.toSimplifiedMove()
        when (player) {
            Players.PLAYER1 -> roomRef.child("player1LocationList").setValue(myMove)
            Players.PLAYER2 -> roomRef.child("player2LocationList").setValue(myMove)
            Players.NONE -> return@withContext
        }

        val result = withTimeoutOrNull(timeoutMillis) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val room = snapshot.getValue(Room::class.java) ?: return
                        val opponentLocations = when (player) {
                            Players.PLAYER1 -> room.player2LocationList
                            Players.PLAYER2 -> room.player1LocationList
                            else -> return
                        }

                        if (opponentLocations != null) {
                            onOpponentReady(opponentLocations)
                            when (player) {
                                Players.PLAYER1 -> roomRef.child("player2LocationList")
                                    .setValue(null)
                                Players.PLAYER2 -> roomRef.child("player1LocationList")
                                    .setValue(null)
                                else -> return
                            }
                            roomRef.removeEventListener(this)
                            continuation.resume(Unit)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Sentry.captureException(error.toException())
                        continuation.resumeWith(Result.failure(error.toException()))
                    }
                }

                roomRef.addValueEventListener(listener)

                continuation.invokeOnCancellation {
                    roomRef.removeEventListener(listener)
                }
            }
        }
        if (result == null) {
            onOpponentDisconnected()
        }
    }

    fun capitulate() {
        val capitulationField = when(player){
            Players.PLAYER1 -> "player1Capitulated"
            Players.PLAYER2 -> "player2Capitulated"
            Players.NONE -> return
        }
        roomRef.child("capitulation").child(capitulationField).setValue(true)
            .addOnFailureListener { e ->
                Sentry.captureException(e)
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
                val capitulated = snapshot.getValue(Boolean::class.java) ?: false
                if (capitulated) {
                    onOpponentCapitulated()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Sentry.captureException(error.toException())
            }
        })
    }

    fun deleteRoom(isOpponentDisconnected: Boolean) {
        roomRef.get().addOnSuccessListener { snapshot ->
            val room = snapshot.getValue(Room::class.java) ?: return@addOnSuccessListener
            val player: String
            val opponent: String
            if (room.player1Id == auth.uid.toString()) {
                player = "player1Id"
                opponent = "player2Id"
            } else {
                player = "player2Id"
                opponent = "player1Id"
            }
            if(!isOpponentDisconnected){
                if (snapshot.child(opponent).value == "") {
                    roomRef.removeValue()
                        .addOnFailureListener { error ->
                            Sentry.captureException(error)
                        }
                } else {
                    roomRef.child(player).setValue("")
                        .addOnFailureListener { error ->
                            Sentry.captureException(error)
                        }
                }
            } else {
                roomRef.removeValue()
                    .addOnFailureListener { error ->
                        Sentry.captureException(error)
                    }
            }
        }.addOnFailureListener { e ->
            Sentry.captureException(e)
        }
    }
}