package com.marks2games.gravitygame.ui.screens.settingScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.models.SharedPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferencesRepository
): ViewModel() {

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

    fun changeKeepScreenOn(enabled: Boolean){
        _settingUiState.update { state ->
            state.copy(keepScreenOn = enabled)
        }
        saveTutorialSettings()
    }

    fun changeShowTutorial(toShow: Boolean){
        _settingUiState.update { state ->
            state.copy(showTutorial = toShow)
        }
        saveTutorialSettings()
    }

    fun saveTutorialSettings(){
        sharedPreferences.setShowTutorial(settingUiState.value.showTutorial)
        sharedPreferences.setKeepScreenOn(settingUiState.value.keepScreenOn)
    }

    @Suppress("DEPRECATION")
    fun setLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        val resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)
        sharedPreferences.setLanguage(language)
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