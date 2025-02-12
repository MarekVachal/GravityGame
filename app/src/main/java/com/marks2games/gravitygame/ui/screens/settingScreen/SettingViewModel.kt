package com.marks2games.gravitygame.ui.screens.settingScreen

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class SettingViewModel : ViewModel() {

    private val _settingUiState = MutableStateFlow(SettingUiState())
    val settingUiState: StateFlow<SettingUiState> = _settingUiState.asStateFlow()

    fun changeLanguage(language: Languages){
        when (language){
            Languages.ENGLISH -> _settingUiState.update { state ->
                state.copy(
                    isEnglishChecked = true,
                    isCzechChecked = false,
                    //isPolishChecked = false
                )
            }
            Languages.CZECH -> _settingUiState.update { state ->
                state.copy(
                    isCzechChecked = true,
                    isEnglishChecked = false,
                    //isPolishChecked = false
                )
            }
            /*
            Languages.POLISH -> _settingUiState.update { state ->
                state.copy(
                    isPolishChecked = true,
                    isEnglishChecked = false,
                    isCzechChecked = false
                )
            }
            */
        }
    }

    fun changeKeepScreenOn(enabled: Boolean, context: Context){
        _settingUiState.update { state ->
            state.copy(keepScreenOn = enabled)
        }
        saveTutorialSettings(context = context)
    }

    fun changeShowTutorial(toShow: Boolean, context: Context){
        _settingUiState.update { state ->
            state.copy(showTutorial = toShow)
        }
        saveTutorialSettings(context = context)
    }

    fun saveTutorialSettings(context: Context){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(
                "AppSettings", Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        editor.putBoolean("ShowTutorial", settingUiState.value.showTutorial)
        editor.putBoolean("keepScreenOn", settingUiState.value.keepScreenOn)
        editor.apply()
    }

    @Suppress("DEPRECATION")
    fun setLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        val resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)
        val prefs: SharedPreferences =
            context.getSharedPreferences(
                "AppSettings", Context.MODE_PRIVATE
            )
        with(prefs.edit()) {
            putString("language", language)
            apply()
        }
    }

    fun setChosenLanguage(language: String){
        changeLanguage(language = when(language){
            "en" -> Languages.ENGLISH
            "cs" -> Languages.CZECH
            //"pl" -> Languages.POLISH
            else -> return
        })
    }
}