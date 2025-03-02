package com.marks2games.gravitygame.battle_game.data.model.enum_class

enum class RoomStatus{
    WAITING, TO_CONFIRM, FULL, ENDED
}

fun String.toRoomStatus(): RoomStatus? {
    return try {
        RoomStatus.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}