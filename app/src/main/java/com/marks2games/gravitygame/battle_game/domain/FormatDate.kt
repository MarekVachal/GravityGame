package com.marks2games.gravitygame.battle_game.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm", locale)
    return format.format(date)
}