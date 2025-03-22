package com.marks2games.gravitygame.core.domain.error

import com.marks2games.gravitygame.building_game.data.model.Resource

sealed class TransportOutResult {
    data class Success(
        val planet1Metal: Int,
        val planet1OrganicSediments: Float,
        val planet1RocketMaterials: Int
    ) : TransportOutResult()

    data class Error(
        val type: String = "TransportOutError",
        val transportId: Int,
        val missingResources: Map<Resource, Int>
    ) : TransportOutResult()
}