package com.marks2games.gravitygame.core.domain

interface TimeProvider {
    fun getCurrentTimeMillis(): Long
    fun getHoursPassed(lastUpdated: Long): Long
    fun getSecondsToNextUpdate(lastUpdated: Long): Int
    fun getTimeUnitMillis(): Long
}