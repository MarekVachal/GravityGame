package com.marks2games.gravitygame.core.data.model

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum

interface MapNode{
    val id: String
    val posX: Float
    val posY: Float
    val connections: List<String>
    val buttonColor: Color
}

data class TechnologyNode(
    val type: TechnologyEnum,
    override val posX: Float,
    override val posY: Float,
    override val connections: List<String>,
    override val buttonColor: Color
) : MapNode {
    override val id: String get() = type.name
}

data class DistrictNode(
    val district: District,
    override val id: String,
    override val posX: Float,
    override val posY: Float,
    override val connections: List<String>,
    override val buttonColor: Color
): MapNode