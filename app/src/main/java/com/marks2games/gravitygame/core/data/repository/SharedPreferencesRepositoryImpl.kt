package com.marks2games.gravitygame.core.data.repository

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): SharedPreferencesRepository{

    override suspend fun setHasSignIn(hasSignIn: Boolean){
        withContext(Dispatchers.IO){
            sharedPreferences.edit {
                putBoolean("hasSignIn", hasSignIn)
            }
        }
    }

    override suspend fun setLanguage(language: String){
        withContext(Dispatchers.IO){
            sharedPreferences.edit {
                putString("language", language)
            }
        }
    }

    override suspend fun setShowTutorial(showTutorial: Boolean){
        sharedPreferences.edit {
            putBoolean("showTutorial", showTutorial)
        }
    }

    override suspend fun setKeepScreenOn(keepScreenOn: Boolean){
        sharedPreferences.edit {
            putBoolean("keepScreenOn", keepScreenOn)
        }
    }

    override suspend fun getShowTutorial(): Boolean{
        return sharedPreferences.getBoolean("showTutorial", true)
    }

    override suspend fun getLanguage(): String{
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    override suspend fun getKeepScreenOn(): Boolean{
        return sharedPreferences.getBoolean("keepScreenOn", false)
    }

    override suspend fun getHasSignIn(): Boolean{
        return sharedPreferences.getBoolean("hasSignIn", false)
    }
}