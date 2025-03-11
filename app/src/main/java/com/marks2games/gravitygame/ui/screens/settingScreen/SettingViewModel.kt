package com.marks2games.gravitygame.ui.screens.settingScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetKeepScreenOnUseCase
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetLanguageUseCase
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetShowTutorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferencesRepository,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setShowTutorialUseCase: SetShowTutorialUseCase,
    private val setKeepScreenOnUseCase: SetKeepScreenOnUseCase
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
        viewModelScope.launch (Dispatchers.IO){
            setShowTutorialUseCase.invoke(settingUiState.value.showTutorial)
            setKeepScreenOnUseCase.invoke(settingUiState.value.keepScreenOn)
        }
    }

    @Suppress("DEPRECATION")
    fun setLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        val resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)
        viewModelScope.launch(Dispatchers.IO) {
            setLanguageUseCase.invoke(language)
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