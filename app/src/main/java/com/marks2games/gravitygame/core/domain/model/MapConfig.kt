package com.marks2games.gravitygame.core.domain.model

sealed interface MapConfig {
    val spaceBetweenNodes: Float
    val nodesPadding: Float
    val defaultButtonSize: Float
    val nodeCenterXCorrection: Float
    val nodeCenterYCorrection: Float
    val buttonShapeCoefficientY: Float
    val isMapRotating: Boolean
    val isBackgroundRotating: Boolean
}

object TechnologyMapConfig : MapConfig {
    override val spaceBetweenNodes: Float = 1f
    override val nodesPadding: Float = 5f
    override val defaultButtonSize: Float = 100f
    override val nodeCenterXCorrection: Float = 1.5f
    override val nodeCenterYCorrection: Float = 1f
    override val buttonShapeCoefficientY: Float = 0.75f
    override val isMapRotating: Boolean = false
    override val isBackgroundRotating: Boolean = false

}

object DistrictMapConfig : MapConfig {
    override val spaceBetweenNodes: Float = 1f
    override val nodesPadding: Float = 5f
    override val defaultButtonSize: Float = 75f
    override val nodeCenterXCorrection: Float = 1.5f
    override val nodeCenterYCorrection: Float = 1f
    override val buttonShapeCoefficientY: Float = 1f
    override val isMapRotating: Boolean = true
    override val isBackgroundRotating: Boolean = true
}
