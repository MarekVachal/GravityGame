package com.marks2games.gravitygame.models

enum class GameType {
    FREE,
    SCORED
}

fun String.toGameType(): GameType? {
    return try {
        GameType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}