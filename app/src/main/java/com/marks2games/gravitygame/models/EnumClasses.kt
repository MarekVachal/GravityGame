package com.marks2games.gravitygame.models

enum class Players{
    PLAYER1, PLAYER2, NONE
}

enum class EndOfGameType{
    REGULAR, DISCONNECTED, CAPITULATION
}

enum class BattleResultEnum {
    WIN, LOSE, DRAW
}

enum class GameType {
    FREE, SCORED
}

enum class BattleMapEnum{
    TINY
}

enum class ShipType{
    CRUISER, DESTROYER, GHOST, WARPER
}

enum class RoomStatus{
    WAITING, TO_CONFIRM, FULL, ENDED
}
enum class ProgressIndicatorType{
    AI_CALCULATE, NEW_TURN, WAITING_FOR_MOVE, WAITING_FOR_OPPONENT
}

enum class Tasks{
    INFO_SHIP,
    NUMBER_SHIPS,
    TIMER,
    MOVEMENT,
    LOCATION_INFO,
    LOCATION_OWNER,
    SEND_SHIPS,
    ACCEPTABLE_LOST,
    BATTLE_OVERVIEW,
    BATTLE_INFO
}

fun String.toRoomStatus(): RoomStatus? {
    return try {
        RoomStatus.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String.toBattleMap(): BattleMapEnum? {
    return try {
        BattleMapEnum.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String.toGameType(): GameType? {
    return try {
        GameType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String.toShipType(): ShipType? {
    return try {
        ShipType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

enum class PlayerState{
    ACTIVE,
    BACKGROUND,
    DEAD
}