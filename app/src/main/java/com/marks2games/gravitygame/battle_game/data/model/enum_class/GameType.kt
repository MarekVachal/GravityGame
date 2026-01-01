package com.marks2games.gravitygame.battle_game.data.model.enum_class

enum class GameType {
    FREE, SCORED
}

fun String.toGameType(): GameType? {
    return try {
        GameType.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}