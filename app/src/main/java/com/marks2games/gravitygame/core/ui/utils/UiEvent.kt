package com.marks2games.gravitygame.core.ui.utils

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowSnackbar(@StringRes val messageResId: Int) : UiEvent()
}