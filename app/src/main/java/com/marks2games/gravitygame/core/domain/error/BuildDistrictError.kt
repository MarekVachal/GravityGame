package com.marks2games.gravitygame.core.domain.error

import com.marks2games.gravitygame.building_game.data.model.District

sealed class BuildDistrictResult {
    data class Success(val districts: List<District>) : BuildDistrictResult()
    sealed class Error : BuildDistrictResult() {
        object DistrictNotFound : Error()
        object CapitolNotAllowed : Error()
        object ExpeditionPlatformExists : Error()
    }
}