package com.marks2games.gravitygame.core.domain.repository

interface SharedPreferencesRepository{
    suspend fun setHasSignIn(hasSignIn: Boolean)
    suspend fun setLanguage(language: String)
    suspend fun setShowTutorial(showTutorial: Boolean)
    suspend fun setKeepScreenOn(keepScreenOn: Boolean)
    suspend fun getShowTutorial(): Boolean
    suspend fun getLanguage(): String
    suspend fun getKeepScreenOn(): Boolean
    suspend fun getHasSignIn(): Boolean
}