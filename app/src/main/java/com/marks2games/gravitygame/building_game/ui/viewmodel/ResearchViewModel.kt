package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.ResearchUiState
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.TechnologyResearchState
import com.marks2games.gravitygame.building_game.domain.usecase.technology.UpdateResearchingTechnologyUseCase
import com.marks2games.gravitygame.core.data.di.TechnologyMap
import com.marks2games.gravitygame.core.data.model.TechnologyNode
import com.marks2games.gravitygame.core.domain.model.MapConfig
import com.marks2games.gravitygame.core.domain.usecases.genericMap.CreateTechnologicalNodesUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.GetNodePositionUseCase
import com.marks2games.gravitygame.core.domain.usecases.genericMap.GetToroidalPositionsUseCase
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
class ResearchViewModel @Inject constructor(
    override val updateOffset: UpdateOffsetUseCase,
    override val updateButtonSize: UpdateButtonSizeUseCase,
    override val updateMinScale: UpdateMinScaleUseCase,
    override val updateMapSize: UpdateMapSizeUseCase,
    private val updateResearchingTechnology: UpdateResearchingTechnologyUseCase,
    private val createTechnologicalNodes: CreateTechnologicalNodesUseCase,
    @TechnologyMap override val mapConfig: MapConfig,
    getToroidalPositions: GetToroidalPositionsUseCase<TechnologyNode>,
    getNodePosition: GetNodePositionUseCase<TechnologyNode>
) : MapViewModel<TechnologyNode>(
    updateOffset = updateOffset,
    updateButtonSize = updateButtonSize,
    updateMinScale = updateMinScale,
    updateMapSize = updateMapSize,
    mapConfig = mapConfig,
    getToroidalPositions = getToroidalPositions,
    getNodePosition = getNodePosition
) {

    private val _researchUiState = MutableStateFlow(ResearchUiState())
    val researchUiState = _researchUiState.asStateFlow()

    fun createTechnologicalMapNodes(technologies: List<Technology>){
        val nodesMap = createTechnologicalNodes.invoke(
            technologies = technologies,
            setButtonColor = { setButtonBorderColor(it) }
        )
        updateNodes(nodesMap)
    }

    fun updateTechnologiesUiState(technologies: List<Technology>){
        _researchUiState.update { state ->
            state.copy(
                technologies = technologies
            )
        }
    }

    fun getTechnologyFromEnum(technologyEnum: TechnologyEnum): Technology?{
        return researchUiState.value.technologies.find { it.type == technologyEnum }
    }

    fun updateIsTechnologyInfoDialogShown(technologyToShow: TechnologyEnum?, isShown: Boolean){
        _researchUiState.update { state ->
            state.copy(
                technologyToShowInfo = technologyToShow,
                isTechnologyInfoDialogShown = isShown
            )
        }
    }

    fun setResearchingTechnology(technology: TechnologyEnum, empireModel: EmpireViewModel){
        val updatedTechnologies = updateResearchingTechnology.invoke(technology, researchUiState.value.technologies)
        updateBordersColor(updatedTechnologies)

        empireModel.setResearchingTechnology(updatedTechnologies)

        _researchUiState.update { state ->
            state.copy(
                technologies = updatedTechnologies
            )
        }
    }

    fun setButtonBorderColor(state: TechnologyResearchState?): Color{
        return when (state) {
            TechnologyResearchState.SELECTED -> Color.Yellow
            TechnologyResearchState.FINISHED -> Color.White
            TechnologyResearchState.LOCKED -> Color.Gray
            TechnologyResearchState.UNLOCKED -> Color.Green
            else -> Color.Gray
        }
    }

    fun updateBordersColor(technologies: List<Technology>) {
        val updatedNodes = mapUiState.value.nodes.map{ node ->
            val technology = technologies.find {node.type == it.type}
            val newColor = setButtonBorderColor(technology?.state)
            node.copy(buttonColor = newColor)
        }
        updateNodes(updatedNodes)
    }
}