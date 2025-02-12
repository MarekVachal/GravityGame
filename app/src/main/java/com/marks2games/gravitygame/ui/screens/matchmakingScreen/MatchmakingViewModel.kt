package com.marks2games.gravitygame.ui.screens.matchmakingScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.marks2games.gravitygame.firebase.Notification
import com.marks2games.gravitygame.firebase.Room
import com.marks2games.gravitygame.models.PlayerData
import com.marks2games.gravitygame.models.Players
import com.marks2games.gravitygame.models.SharedPlayerDataRepository
import com.marks2games.gravitygame.timer.CoroutineTimer
import com.marks2games.gravitygame.timer.TimerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.maps.toBattleMap
import com.marks2games.gravitygame.models.toGameType
import com.marks2games.gravitygame.signIn.AnonymousSign
import com.marks2games.gravitygame.signIn.GoogleSign

@HiltViewModel
class MatchmakingViewModel @Inject constructor(
    private val sharedPlayerModel: SharedPlayerDataRepository,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val notification: Notification
): ViewModel() {

    private val _matchmakingUiStates = MutableStateFlow(MatchmakingUiStates())
    val matchmakingUiStates = _matchmakingUiStates.asStateFlow()
    val playerData: StateFlow<PlayerData> = sharedPlayerModel.playerData
    private val TAG = "Matchmaking"
    private val anonymousSign = AnonymousSign(auth)

    fun restoreGameSession(roomId: String){
        val roomRef = database.reference.child("rooms").child(roomId)
        roomRef.get().addOnSuccessListener { snapshot ->
            val room = snapshot.getValue(Room::class.java)
            if(room != null){
                Log.d("NotificationHandler", "Game session restored for room: $roomId")
                sharedPlayerModel.updateRoomRef(roomRef)
                room.gameType.toGameType()?.let { sharedPlayerModel.updateGameType(it) }
                room.battleMap.toBattleMap()?.let { sharedPlayerModel.updateBattleMap(it) }
                sharedPlayerModel.updateIsOnline(true)
            } else {
                Log.e("NotificationHandler", "Room not found: $roomId")
            }
        }
    }

    fun showSignInDialog(toShow: Boolean){
        _matchmakingUiStates.update { state ->
            state.copy(
                toShowSignInDialog = toShow
            )
        }
    }

    fun signInAnonymously(){
        viewModelScope.launch {
            anonymousSign.signInAnonymously()
        }
        showSignInDialog(false)
    }

    fun signInWithGoogle(googleSign: GoogleSign, context: Context){
        viewModelScope.launch {
            val authListener = object : FirebaseAuth.AuthStateListener {
                override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                    if (auth.currentUser != null) {
                        findOrCreateRoom(context)
                        auth.removeAuthStateListener(this)
                    }
                }
            }
            auth.addAuthStateListener(authListener)
            googleSign.signInWithCredentialManager()
            showSignInDialog(false)
        }
    }

    fun startMatchmaking(context: Context){
        viewModelScope.launch {
            if(auth.currentUser == null){
                showSignInDialog(true)
            } else {
                findOrCreateRoom(context)
            }
        }

    }

    private fun findOrCreateRoom(context: Context) {
        val battleMapEnum = playerData.value.battleMap
        val gameType = playerData.value.gameType
        val parameters = "$battleMapEnum - $gameType"
        val roomsRef = database.reference.child("rooms")
        val playerId: String = auth.uid ?: return

        roomsRef
            .orderByChild("status").equalTo(RoomStatus.WAITING.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var availableRoomKey: String? = null

                    for (roomSnapshot in snapshot.children) {
                        val room = roomSnapshot.getValue(Room::class.java)
                        if (room != null && room.parameters == parameters) {
                            availableRoomKey = roomSnapshot.key
                            break
                        }
                    }

                    Log.d(TAG, "Listener availableRoomKey: $availableRoomKey")

                    if (availableRoomKey != null) {
                        val availableRoomRef = roomsRef.child(availableRoomKey)
                        availableRoomRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                Log.d(TAG, "Transaction starts. Data: $mutableData")
                                val room = mutableData.getValue(Room::class.java)
                                    ?: return Transaction.success(mutableData)
                                Log.d(TAG, "Room: $room")
                                if(room.player2Id.isNotEmpty()){
                                    Log.d(TAG, "Room if full")
                                    Transaction.success(mutableData)
                                } else if(room.player1Id == playerId){
                                    Log.d(TAG, "Player is already in the room")
                                    deleteRoom(availableRoomRef)
                                    Transaction.success(mutableData)
                                } else {
                                    room.player2Id = playerId
                                    room.status = RoomStatus.TO_CONFIRM.name
                                    mutableData.value = room
                                    return Transaction.success(mutableData)
                                }
                                Log.d(TAG, "Room is full")
                                return Transaction.success(mutableData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                if(currentData == null){
                                    Log.d(TAG, "currentData in onComplete are null")
                                }
                                if(!committed){
                                    Log.d(TAG, "Transaction not committed")
                                }
                                if (committed) {
                                    val room = currentData?.getValue(Room::class.java)
                                    if (room != null) {
                                        updateRoomRef(availableRoomRef)
                                        handleRoomState(playerId, context)
                                    }
                                } else {
                                    Log.d(TAG, "Unknown error")
                                }
                            }
                        })
                    } else {
                        // No matching room. Creating new one
                        Log.d(TAG, "No available room. Room is creating")
                        val newRoomKey = roomsRef.push().key ?: return
                        val newRoomRef = roomsRef.child(newRoomKey)
                        updateRoomRef(newRoomRef)
                        val newRoom = Room(
                            player1Id = playerId,
                            parameters = parameters,
                            status = RoomStatus.WAITING.name,
                            battleMap = battleMapEnum.name,
                            gameType = gameType.name
                        )

                        Log.d(TAG, "New room created: $newRoom")
                        roomsRef.child(newRoomKey).setValue(newRoom)
                            .addOnSuccessListener {
                                Log.d(TAG, "Call for handleRoomState()")
                                handleRoomState(playerId, context)
                            }
                            .addOnFailureListener { error ->
                                Log.e(TAG, "Failed to create a new room: ${error.message}")
                                startMatchmakingAfterFail(context)
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to query rooms: ${error.message}")
                    startMatchmakingAfterFail(context)
                }
            })
    }

    fun handleRoomStateAfterNotification(context: Context) {
        val roomRef = playerData.value.roomRef ?: return
        Log.d("Notification", "roomRef: $roomRef")
        roomRef.get().addOnSuccessListener { snapshot ->
            Log.d("Notification", "Data room successfully get.")
            val room = snapshot.getValue(Room::class.java)
            Log.d("Notification", "Room: $room")
            if (room?.player1Id != "" && room?.player2Id != "") {
                Log.d("Notification", "Second player joined. Match ready.")
                opponentFound(true)
            } else {
                deleteRoom(roomRef)
                findOrCreateRoom(context)
            }
        }
            .addOnFailureListener {
                findOrCreateRoom(context)
            }

    }

    private fun handleRoomState(
        playerId: String,
        context: Context
    ) {
        Log.d(TAG, "handleRoomState starts")
        Log.d(TAG, "RoomRef in playerData: ${playerData.value.roomRef}")
        val roomRef = playerData.value.roomRef ?: return
        Log.d(TAG, "HandleRoomState roomRef: $roomRef")
        roomRef.get().addOnSuccessListener { snapshot ->
            val room = snapshot.getValue(Room::class.java)
            if(room != null){
                Log.d(TAG, "Room data: $room")
                if (room.player1Id == playerId) {
                    Log.d(TAG, "Player is assigned as PLAYER1.")
                    updatePlayer(Players.PLAYER1)
                } else if (room.player2Id == playerId) {
                    Log.d(TAG, "Player is assigned as PLAYER2.")
                    updatePlayer(Players.PLAYER2)
                }

                val roomStatus = room.status.toRoomStatus()

                when (roomStatus) {
                    RoomStatus.WAITING -> {
                        Log.d(TAG, "Room is waiting for the second player.")

                        onWaitingForOpponent()

                        val listener = object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val updatedRoom = snapshot.getValue(Room::class.java)
                                if (updatedRoom?.player1Id != "" && updatedRoom?.player2Id != "") {
                                    Log.d(TAG, "Second player joined. Match ready.")
                                    onMatchFound(roomRef)
                                    roomRef.removeEventListener(this)
                                    updateListener(null)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Sentry.captureException(error.toException())
                                Log.d(TAG, "Failed to create listener: ${error.message}")
                                startMatchmakingAfterFail(context)
                            }
                        }
                        updateListener(listener)
                        roomRef.addValueEventListener(listener)
                    }

                    RoomStatus.TO_CONFIRM -> {
                        Log.d(TAG, "Match found.")
                        viewModelScope.launch(Dispatchers.IO) {
                            notification.sendNotificationToPlayer(
                                roomId = roomRef.key?: "",
                                playerId = room.player1Id,
                                title = context.getString(R.string.notificationTitleOpponentFound),
                                body = context.getString(R.string.notificationBodyOpponentFound),
                                context = context
                            )
                        }
                        onMatchFound(roomRef)
                    }

                    RoomStatus.FULL -> {
                        Log.d(TAG, "Room is full.")
                        startMatchmakingAfterFail(context)
                    }

                    RoomStatus.ENDED -> {
                        Log.d(TAG, "Room has ended. Cleaning up.")
                        deleteRoom(roomRef = roomRef)
                        startMatchmakingAfterFail(context)
                    }

                    else -> {
                        Log.e(TAG, "Unexpected room status: ${room.status}")
                        deleteRoom(roomRef)
                        updateListener(null)
                        updateRoomRef(null)
                        findOrCreateRoom(context)
                    }
                }
            } else {
                Log.d(TAG, "Room does not exist or data is null.")
                updateListener(null)
                updateRoomRef(null)
                findOrCreateRoom(context)
            }
        }.addOnFailureListener { error ->
            Log.e(TAG, "Failed to fetch room: ${error.message}")
            Sentry.captureException(error)
            updateListener(null)
            updateRoomRef(null)
            findOrCreateRoom(context)
        }
    }

    fun confirmPresence(
        onMatchConfirmed: () -> Unit,
        timerModel: TimerViewModel,
        context: Context
    ) {

        val playerId = auth.uid ?: return
        val roomRef = playerData.value.roomRef ?: return

        Log.d("Matchmaking", "Confirming presence for player: $playerId")

        roomRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val room = currentData.getValue(Room::class.java) ?: return Transaction.success(currentData)
                Log.d("Matchmaking", "Current room data: $room")

                val updatedRoom = when (playerId) {
                    room.player1Id -> room.copy(player1Ready = true)
                    room.player2Id -> room.copy(player2Ready = true)
                    else -> {
                        Log.w("Matchmaking", "Player ID $playerId does not match room players.")
                        return Transaction.abort()
                    }
                }
                Log.d("Matchmaking", "Updated room data: $updatedRoom")
                currentData.value = updatedRoom
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Log.e("Matchmaking", "Transaction error: ${error.message}")
                    Sentry.captureException(error.toException())
                    return
                }

                if (!committed) {
                    Log.d("Matchmaking", "Transaction not committed.")
                    return
                }

                val room = currentData?.getValue(Room::class.java)?: return
                Log.d("Matchmaking", "Room data after transaction: $room")

                if (room.player1Ready && room.player2Ready) {
                    Log.d("Matchmaking", "Both players confirmed presence.")
                    val statusUpdate = mapOf(
                        "status" to RoomStatus.FULL.name
                    )
                    roomRef.updateChildren(statusUpdate)
                        .addOnSuccessListener {
                            Log.d(TAG, "Room status updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to update room status: ${e.message}")
                        }
                    onMatchConfirmed()
                } else {
                    Log.d("Matchmaking", "Waiting for opponent to confirm presence.")
                    waitingForConfirmation(true)
                    val timer = CoroutineTimer(
                        timerModel = timerModel,
                        secondsForTurn = 60,
                        onFinishTimer = {
                            onFinishTimer(
                                roomRef = roomRef,
                                context = context,
                                timerModel = timerModel
                            )
                        }
                    )
                    timerModel.makeTimer(timer)

                    roomRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val updatedRoom = snapshot.getValue(Room::class.java)?: return
                            Log.d("Matchmaking", "Room data on opponent check: $updatedRoom")

                            if(updatedRoom.player1Ready && updatedRoom.player2Ready){
                                Log.d("Matchmaking", "Opponent confirmed presence. Stopping timer.")
                                room.status = RoomStatus.FULL.name
                                timerModel.stopTimer()
                                onMatchConfirmed()
                                waitingForConfirmation(false)
                                roomRef.removeEventListener(this)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Matchmaking", "Error during opponent check: ${error.message}")
                            Sentry.captureException(error.toException())
                            timerModel.stopTimer()
                            waitingForConfirmation(false)
                            roomRef.removeEventListener(this)
                            deleteRoom(roomRef)
                        }
                    })
                }
            }
        })
    }

    private fun deleteRoom(roomRef: DatabaseReference){
        roomRef.removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Room successfully deleted.")
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to delete room: ${error.message}")
                Sentry.captureException(error)
            }
    }

    private fun onFinishTimer(
        roomRef: DatabaseReference,
        context: Context,
        timerModel: TimerViewModel
    ){
        Log.d("Matchmaking", "Opponent did not confirm in time. Deleting room.")
        deleteRoom(roomRef)
        onOpponentDisconnected(context, timerModel)
    }

    private fun updatePlayer(player: Players){
        Log.d("Player", "View model method start. Player data: ${playerData.value.player}, player in method: $player")
        val opponent = when (player) {
            Players.PLAYER1 -> Players.PLAYER2
            Players.PLAYER2 -> Players.PLAYER1
            Players.NONE -> Players.NONE
        }
        sharedPlayerModel.updatePlayer(player = player, opponent = opponent)
        Log.d("Player", "View model method end: ${playerData.value.player}")
    }

    fun waitingForConfirmation(toShow: Boolean){
        _matchmakingUiStates.update { state ->
            state.copy(
                toShow = toShow
            )
        }
    }

    fun showProgressIndicator(toShow: Boolean){
        _matchmakingUiStates.update { state ->
            state.copy(toShow = toShow)
        }
    }

    fun opponentFound(isFound: Boolean){
        _matchmakingUiStates.update { state ->
            state.copy(opponentFound = isFound)
        }
        showProgressIndicator(false)
    }

    fun cancelMatchmaking(){
        updateListener(null)
        playerData.value.roomRef?.let { deleteRoom(it) }
    }

    private fun startMatchmakingAfterFail(context: Context){
        updateListener(null)
        updateRoomRef(null)
        findOrCreateRoom(context)
    }

    private fun onMatchFound(roomRef: DatabaseReference){
        sharedPlayerModel.updateRoomRef(roomRef)
        opponentFound(true)
    }

    private fun onWaitingForOpponent(){
        showProgressIndicator(toShow = true)
    }

    private fun onOpponentDisconnected(context: Context, timerModel: TimerViewModel){
        showProgressIndicator(toShow = true)
        opponentFound(isFound = false)
        timerModel.cancelTimer()
        startMatchmakingAfterFail(context)
    }

    private fun updateListener(listener: ValueEventListener?){
        _matchmakingUiStates.update { state ->
            state.copy(
                waitingListener = listener
            )
        }
    }

    private fun updateRoomRef(roomRef: DatabaseReference?){
        Log.d("UpdateRoomRef", "RoomRef: $roomRef")
        sharedPlayerModel.updateRoomRef(roomRef)
    }
}

enum class RoomStatus{
    WAITING,
    TO_CONFIRM,
    FULL,
    ENDED
}

fun String.toRoomStatus(): RoomStatus? {
    return try {
        RoomStatus.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}