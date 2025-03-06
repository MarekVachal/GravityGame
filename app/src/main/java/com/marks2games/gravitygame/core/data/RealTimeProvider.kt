package com.marks2games.gravitygame.core.data

import com.marks2games.gravitygame.core.domain.TimeProvider
import javax.inject.Inject

class RealTimeProvider @Inject constructor() : TimeProvider {

    private val timeUnitMillis = 5000L //60 * 60 * 1000L

    override fun getTimeUnitMillis(): Long = timeUnitMillis

    override fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

    override fun getHoursPassed(lastUpdated: Long): Long {
        val currentTime = getCurrentTimeMillis()
        return (currentTime - lastUpdated) / timeUnitMillis
    }

    override fun getSecondsToNextUpdate(lastUpdated: Long): Int{
        val elapsedTime = getCurrentTimeMillis() - lastUpdated
        return (timeUnitMillis - (elapsedTime % timeUnitMillis)).toInt() /1000
    }
}