package com.marks2games.gravitygame.core.domain.usecases.genericMap

import javax.inject.Inject

class UpdateButtonSizeUseCase @Inject constructor() {
    operator fun invoke(scale: Float, defaultButtonSize: Float): Float {
        return defaultButtonSize * scale
    }
}