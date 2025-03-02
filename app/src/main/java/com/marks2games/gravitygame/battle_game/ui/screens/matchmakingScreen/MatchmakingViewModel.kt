package com.marks2games.gravitygame.battle_game.ui.screens.matchmakingScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.marks2games.gravitygame.core.domain.Notification
import com.marks2games.gravitygame.battle_game.data.model.realtime_database.Room
import com.marks2games.gravitygame.battle_game.data.model.PlayerData
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Players
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.battle_game.ui.utils.timer.CoroutineTimer
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
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
import com.marks2games.gravitygame.battle_game.data.model.enum_class.toBattleMap
import com.marks2games.gravitygame.battle_game.data.model.enum_class.RoomStatus
import com.marks2games.gravitygame.battle_game.data.model.enum_class.toGameType
import com.marks2games.gravitygame.battle_game.data.model.enum_class.toRoomStatus
import com.marks2games.gravitygame.core.domain.authentication.AnonymousSign
import com.marks2games.gravitygame.core.domain.authentication.GoogleSign

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
    private val anonymousSign = AnonymousSign(auth)

    fun restoreGameSession(roomId: String){
        val roomRef = database.reference.child("rooms").child(roomId)
        roomRef.get().addOnSuccessListener { snapshot ->
            val room = snapshot.getValue(Room::class.java)
            if(room != null){
                sharedPlayerModel.updateRoomRef(roomRef)
                room.gameType.toGameType()?.let { sharedPlayerModel.updateGameType(it) }
                room.battleMap.toBattleMap()?.let { sharedPlayerModel.updateBattleMap(it) }
                sharedPlayerModel.updateIsOnline(true)
            } else {
                return@addOnSuccessListener
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

                    if (availableRoomKey != null) {
                        val availableRoomRef = roomsRef.child(availableRoomKey)
                        availableRoomRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                val room = mutableData.getValue(Room::class.java)
                                    ?: return Transaction.success(mutableData)
                                if(room.player2Id.isNotEmpty()){
                                    Transaction.success(mutableData)
                                } else if(room.player1Id == playerId){
                                    deleteRoom(availableRoomRef)
                                    Transaction.success(mutableData)
                                } else {
                                    room.player2Id = playerId
                                    room.status = RoomStatus.TO_CONFIRM.name
                                    mutableData.value = room
                                    return Transaction.success(mutableData)
                                }
                                return Transaction.success(mutableData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                if(error != null){
                                    Sentry.captureException(error.toException())
                                    return
                                }
                                if(!committed){
                                    return
                                }
                                val room = currentData?.getValue(Room::class.java)
                                if (room != null) {
                                    updateRoomRef(availableRoomRef)
                                    handleRoomState(playerId, context)
                                }
                            }
                        })
                    } else {
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

                        roomsRef.child(newRoomKey).setValue(newRoom)
                            .addOnSuccessListener {
                                handleRoomState(playerId, context)
                            }
                            .addOnFailureListener { error ->
                                startMatchmakingAfterFail(context)
                                Sentry.captureException(error)
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Sentry.captureException(error.toException())
                    startMatchmakingAfterFail(context)
                }
            })
    }

    fun handleRoomStateAfterNotification(context: Context) {
        val roomRef = playerData.value.roomRef ?: return
        roomRef.get()
            .addOnSuccessListener { snapshot ->
                val room = snapshot.getValue(Room::class.java)
                if (room?.player1Id != "" && room?.player2Id != "") {
                    opponentFound(true)
                } else {
                    deleteRoom(roomRef)
                    findOrCreateRoom(context)
                }
            }
            .addOnFailureListener{ error ->
                findOrCreateRoom(context)
                Sentry.captureException(error)
            }
    }

    private fun handleRoomState(
        playerId: String,
        context: Context
    ) {
        val roomRef = playerData.value.roomRef ?: return
        roomRef.get()
            .addOnSuccessListener { snapshot ->
                val room = snapshot.getValue(Room::class.java)
                if(room != null){
                    if (room.player1Id == playerId) {
                        updatePlayer(Players.PLAYER1)
                    } else if (room.player2Id == playerId) {
                        updatePlayer(Players.PLAYER2)
                    }
                    val roomStatus = room.status.toRoomStatus()
                    when (roomStatus) {
                        RoomStatus.WAITING -> {
                            onWaitingForOpponent()

                            val listener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val updatedRoom = snapshot.getValue(Room::class.java)
                                    if (updatedRoom?.player1Id != "" && updatedRoom?.player2Id != "") {
                                        onMatchFound(roomRef)
                                        roomRef.removeEventListener(this)
                                        updateListener(null)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Sentry.captureException(error.toException())
                                    startMatchmakingAfterFail(context)
                                }
                            }
                            updateListener(listener)
                            roomRef.addValueEventListener(listener)
                        }

                        RoomStatus.TO_CONFIRM -> {
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
                            startMatchmakingAfterFail(context)
                        }

                        RoomStatus.ENDED -> {
                            deleteRoom(roomRef = roomRef)
                            startMatchmakingAfterFail(context)
                        }

                        else -> {
                            deleteRoom(roomRef)
                            updateListener(null)
                            updateRoomRef(null)
                            findOrCreateRoom(context)
                        }
                    }
                } else {
                    updateListener(null)
                    updateRoomRef(null)
                    findOrCreateRoom(context)
                }
            }.addOnFailureListener { error ->
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

        roomRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val room = currentData.getValue(Room::class.java) ?: return Transaction.success(currentData)

                val updatedRoom = when (playerId) {
                    room.player1Id -> room.copy(player1Ready = true)
                    room.player2Id -> room.copy(player2Ready = true)
                    else -> return Transaction.abort()
                }
                currentData.value = updatedRoom
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Sentry.captureException(error.toException())
                    return
                }

                if (!committed) {
                    return
                }

                val room = currentData?.getValue(Room::class.java)?: return

                if (room.player1Ready && room.player2Ready) {
                    roomRef.child("status").setValue(RoomStatus.FULL.name)
                        .addOnFailureListener { e ->
                            Sentry.captureException(e)
                        }
                    onMatchConfirmed()
                } else {
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
                            if(updatedRoom.player1Ready && updatedRoom.player2Ready){
                                room.status = RoomStatus.FULL.name
                                timerModel.stopTimer()
                                onMatchConfirmed()
                                waitingForConfirmation(false)
                                roomRef.removeEventListener(this)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
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
            .addOnFailureListener { error ->
                Sentry.captureException(error)
            }
    }

    private fun onFinishTimer(
        roomRef: DatabaseReference,
        context: Context,
        timerModel: TimerViewModel
    ){
        deleteRoom(roomRef)
        onOpponentDisconnected(context, timerModel)
    }

    private fun updatePlayer(player: Players){
        val opponent = when (player) {
            Players.PLAYER1 -> Players.PLAYER2
            Players.PLAYER2 -> Players.PLAYER1
            Players.NONE -> Players.NONE
        }
        sharedPlayerModel.updatePlayer(player = player, opponent = opponent)
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
        sharedPlayerModel.updateRoomRef(roomRef)
    }
}

