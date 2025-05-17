package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.building_game.data.model.ResearchUiState
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.UpdateResearchingTechnologyUseCase
import com.marks2games.gravitygame.core.domain.usecases.UpdateButtonSizeUseCase
import com.marks2games.gravitygame.core.domain.usecases.UpdateOffsetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ResearchViewModel @Inject constructor(
    private val updateOffset: UpdateOffsetUseCase,
    private val updateButtonSize: UpdateButtonSizeUseCase,
    private val updateResearchingTechnology: UpdateResearchingTechnologyUseCase
) : ViewModel(){

    private val _researchUiState = MutableStateFlow(ResearchUiState())
    val researchUiState = _researchUiState.asStateFlow()

    fun updateScale(newScale: Float) {
        val minScale = researchUiState.value.minScale
        _researchUiState.update { state ->
            state.copy(
                scale = newScale.coerceIn(minScale, 2.5f),
                buttonSize = updateButtonSize.invoke(newScale, researchUiState.value.defaultButtonSize)
            )
        }
    }

    fun updateOffset(newOffset: Offset) {
        if (researchUiState.value.screenSize != IntSize.Zero) {
            _researchUiState.update{ state ->
                state.copy(
                    offset = updateOffset.invoke(
                        screenSize = researchUiState.value.screenSize,
                        newOffset = newOffset,
                        scale = researchUiState.value.scale,
                        mapSize = researchUiState.value.mapSize,
                        nodeSize = researchUiState.value.buttonSize
                    )
                )
            }
        }
    }

    fun updateScreenSize(newScreenSize: IntSize) {
        val mapWidth = newScreenSize.width * researchUiState.value.spaceBetweenNodes
        val mapHeight = newScreenSize.height * researchUiState.value.spaceBetweenNodes
        val mapSize = Size(mapWidth, mapHeight)

        val minScale = maxOf(
            newScreenSize.width / (mapWidth + researchUiState.value.buttonSize * researchUiState.value.nodesPadding),
            newScreenSize.height / (mapHeight + researchUiState.value.buttonSize * researchUiState.value.nodesPadding)
        )

        _researchUiState.update { state ->
            state.copy(
                screenSize = newScreenSize,
                mapSize = mapSize,
                minScale = minScale
            )
        }
    }

    fun setResearchingTechnology(technology: TechnologyEnum, empireModel: EmpireViewModel){
        val updatedTechnologies = updateResearchingTechnology.invoke(technology, researchUiState.value.technologies)

        empireModel.setResearchingTechnology(updatedTechnologies)

        _researchUiState.update { state ->
            state.copy(
                technologies = updatedTechnologies
            )
        }
    }



    fun updateTechnologiesUiState(technologies: List<Technology>){
        _researchUiState.update { state ->
            state.copy(
                technologies = technologies
            )
        }
    }

    fun getTechnologyFromEnum(technologyEnum: TechnologyEnum, technologies: List<Technology>): Technology?{
        return technologies.find { it.type == technologyEnum }
    }

    fun updateIsTechnologyInfoDialogShown(technologyToShow: TechnologyEnum?, isShown: Boolean){
        _researchUiState.update { state ->
            state.copy(
                technologyToShowInfo = technologyToShow,
                isTechnologyInfoDialogShown = isShown
            )
        }
    }

}