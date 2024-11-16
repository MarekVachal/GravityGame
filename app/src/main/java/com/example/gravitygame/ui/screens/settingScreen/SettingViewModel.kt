package com.example.gravitygame.ui.screens.settingScreen

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SettingViewModel : ViewModel() {

    private val _settingUiState = MutableStateFlow(SettingUiState())
    val settingUiState: StateFlow<SettingUiState> = _settingUiState.asStateFlow()

    fun changeLanguage(language: Languages){
        when (language){
            Languages.ENGLISH -> _settingUiState.value = _settingUiState.value.copy(isEnglishChecked = true, isCzechChecked = false, isPolishChecked = false)
            Languages.CZECH -> _settingUiState.value = _settingUiState.value.copy(isCzechChecked = true, isEnglishChecked = false, isPolishChecked = false)
            Languages.POLISH -> _settingUiState.value = _settingUiState.value.copy(isPolishChecked = true, isEnglishChecked = false, isCzechChecked = false)
        }
    }

    fun changeKeepScreenOn(enabled: Boolean, context: Context){
        _settingUiState.value = _settingUiState.value.copy(keepScreenOn = enabled)
        saveTutorialSettings(context = context)
    }

    fun changeShowTutorial(toShow: Boolean, context: Context){
        _settingUiState.value = _settingUiState.value.copy(showTutorial = toShow)
        saveTutorialSettings(context = context)
    }

    fun saveTutorialSettings(context: Context){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
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
        val prefs: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("language", language)
            apply()
        }
    }

    fun setChosenLanguage(language: String){
        changeLanguage(language = when(language){
            "en" -> Languages.ENGLISH
            "cs" -> Languages.CZECH
            "pl" -> Languages.POLISH
            else -> return
        })
    }
}