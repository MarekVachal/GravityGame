package com.example.gravitygame.ui.screens.settingScreen

data class SettingUiState(
    val isEnglishChecked: Boolean = true,
    val isCzechChecked: Boolean = false,
    val isPolishChecked: Boolean = false,
    val showTutorial: Boolean = true,
    val keepScreenOn: Boolean = true
)

enum class Languages {
    ENGLISH,
    CZECH,
    POLISH
}
