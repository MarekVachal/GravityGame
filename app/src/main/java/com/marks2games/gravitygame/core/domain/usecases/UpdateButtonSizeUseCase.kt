package com.marks2games.gravitygame.core.domain.usecases

import javax.inject.Inject

class UpdateButtonSizeUseCase @Inject constructor() {
    operator fun invoke(scale: Float, buttonSize: Float): Float {
        return buttonSize * scale
    }
}