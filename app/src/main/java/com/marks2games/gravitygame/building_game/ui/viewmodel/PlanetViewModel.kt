package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetDistrictBorderColorUseCase
import com.marks2games.gravitygame.core.data.di.DistrictMap
import com.marks2games.gravitygame.core.data.model.DistrictNode
import com.marks2games.gravitygame.core.domain.model.MapConfig
import com.marks2games.gravitygame.core.domain.usecases.genericMap.CreateDistrictNodesUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateButtonSizeUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateMapSizeUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateMinScaleUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.UpdateOffsetUseCase
import com.marks2games.gravitygame.ui.viewModel.MapViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlanetViewModel @Inject constructor(
    override val updateOffset: UpdateOffsetUseCase,
    override val updateButtonSize: UpdateButtonSizeUseCase,
    override val updateMinScale: UpdateMinScaleUseCase,
    override val updateMapSize: UpdateMapSizeUseCase,
    private val createDistrictNodes: CreateDistrictNodesUseCase,
    private val getDistrictBorder: GetDistrictBorderColorUseCase,
    @DistrictMap override val mapConfig: MapConfig
) : MapViewModel<DistrictNode>(
    updateOffset = updateOffset,
    updateButtonSize = updateButtonSize,
    updateMinScale = updateMinScale,
    updateMapSize = updateMapSize,
    mapConfig = mapConfig
) {

    private val _planetUiState = MutableStateFlow(Planet())
    val planetUiState = _planetUiState.asStateFlow()

    fun updatePlanetUiState(planet: Planet?){
        planet?.let {
            _planetUiState.update { planet }
        }
    }

    fun updateDistrictNodes(planet: Planet?){
        planet?.let{
            val nodes = createDistrictNodes.invoke(planet.type, planet.districts)
            val updatedNodes = nodes.map { node ->
                val newColor = getDistrictBorder(node.district)
                node.copy(buttonColor = newColor)
            }
            updateNodes(updatedNodes)
        }

    }

    private fun getDistrictBorder(district: District): Color{
        return getDistrictBorder.invoke(
            district,
            planetUiState.value.planetGrowthBorder,
            planetUiState.value.progress
        )
    }
}