package com.marks2games.gravitygame.battle_game.data.model.enum_class

enum class BattleMapEnum{
    TINY
}

fun String.toBattleMap(): BattleMapEnum? {
    return try {
        BattleMapEnum.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}