package com.marks2games.gravitygame.models

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    fun setHasSignIn(hasSignIn: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("hasSignIn", hasSignIn)
        editor.apply()
    }

    fun setLanguage(language: String){
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()
    }

    fun setShowTutorial(showTutorial: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("showTutorial", showTutorial)
        editor.apply()
    }

    fun setKeepScreenOn(keepScreenOn: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("keepScreenOn", keepScreenOn)
        editor.apply()
    }

    fun getShowTutorial(): Boolean{
        return sharedPreferences.getBoolean("showTutorial", true)
    }

    fun getLanguage(): String{
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    fun getKeepScreenOn(): Boolean{
        return sharedPreferences.getBoolean("keepScreenOn", false)
    }

    fun getHasSignIn(): Boolean{
        return sharedPreferences.getBoolean("hasSignIn", false)
    }
}