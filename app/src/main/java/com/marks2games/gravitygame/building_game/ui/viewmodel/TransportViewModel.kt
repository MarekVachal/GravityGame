package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.data.model.TransportUiState
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnUseClass
import com.marks2games.gravitygame.building_game.domain.usecase.transport.CanPlanet2ExportUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.GenerateTransportId
import com.marks2games.gravitygame.building_game.domain.usecase.transport.GetPlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.IsAddButtonEnabledUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.IsRemoveButtonEnabledUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.UpdateModifiedEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.UpdateResourceInTransportUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.UpdateTransportUiUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.CreateNewEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetResourceIconUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor(
    private val testNewTurnUseClass: NewTurnUseClass,
    private val updateResource: UpdateResourceInTransportUseCase,
    private val generateTransportId: GenerateTransportId,
    private val getResourceIcon: GetResourceIconUseCase,
    private val isRemoveButtonEnabled: IsRemoveButtonEnabledUseCase,
    private val isAddButtonEnabled: IsAddButtonEnabledUseCase,
    private val getPlanet: GetPlanetUseCase,
    private val canPlanet2Export: CanPlanet2ExportUseCase,
    private val updateModifiedEmpire: UpdateModifiedEmpireUseCase,
    private val updateTransportUi: UpdateTransportUiUseCase,
    createNewEmpireUseCase: CreateNewEmpireUseCase
) : ViewModel() {

    private val _transportUiState = MutableStateFlow(TransportUiState())
    val transportUiState: StateFlow<TransportUiState> = _transportUiState
    private val _modifiedEmpire = MutableStateFlow(createNewEmpireUseCase.invoke())
    val modifiedEmpire: StateFlow<Empire> = _modifiedEmpire
    private lateinit var realEmpire: Empire

    private fun updateTransportUi(isCostChange: Boolean, resource: Resource, isAdding: Boolean, isForPlanet1: Boolean){
        _modifiedEmpire.update { updateTransportUi.invoke(
            resource = resource,
            transport = transportUiState.value.transport,
            modifiedEmpire = modifiedEmpire.value,
            isAdding = isAdding,
            isForPlanet1 = isForPlanet1,
            isCostChange = isCostChange
        ) }
    }

    fun updateTransportDialogOnTransportClick(transport: Transport) {
        _transportUiState.update { state ->
            state.copy(
                transport = transport,
                isTransportReady = true
            )
        }
    }

    fun isAddButtonEnabled(planet: Planet?, resource: Resource, isForPlanet1: Boolean): Boolean {
        val exportedPlanet = if (isForPlanet1) {
            planet
        } else {
            realEmpire.planets.firstOrNull{ it.id == transportUiState.value.transport.planet1Id }
        }
        return isAddButtonEnabled.invoke(
            exportedPlanet = exportedPlanet,
            planet = planet,
            resource = resource,
            isPlanet1 = isForPlanet1,
            transport = transportUiState.value.transport
        )
    }

    fun isRemoveButtonEnabled(planet: Planet?, resource: Resource, isForPlanet1: Boolean): Boolean {
        val map = if (isForPlanet1) {
            transportUiState.value.transport.exportFromPlanet1
        } else {
            transportUiState.value.transport.exportFromPlanet2
        }
        return isRemoveButtonEnabled.invoke(planet, resource, map)
    }

    fun getPlanet(planetId: Int, empire: Empire): Planet? {
        return getPlanet.invoke(planetId, empire)
    }

    fun launchTransportDialog(empire: Empire, planet: Planet) {
        val testEmpire = testNewTurnUseClass.invoke(empire, true).first
        val modifiedEmpire = updateModifiedEmpire.invoke(testEmpire, planet.id, null)

        _modifiedEmpire.update { modifiedEmpire
        }
        val newTransport = Transport(
            planet1Id = planet.id,
            transportId = generateTransportId.invoke(empire)
        )

        _transportUiState.update { state ->
            state.copy(
                isTransportReady = false,
                transport = newTransport
            )
        }

        realEmpire = empire
    }

    fun getResourceIcon(resource: Resource): Int {
        return getResourceIcon.invoke(resource)
    }

    fun updateIsLongTermChosen(isLongTermChosen: Boolean) {
        val newTransport = transportUiState.value.transport.copy(
            isLongTime = isLongTermChosen
        )
        _transportUiState.update { state ->
            state.copy(
                transport = newTransport
            )
        }
    }

    fun canPlanet2Export(planet: Planet?): Boolean {
        return canPlanet2Export.invoke(planet)
    }

    fun updateChosen2Planet(planet: Planet?, onPlanetNotFound: () -> Unit) {
        val testPlanet = planet?.let { getPlanet.invoke(it.id, modifiedEmpire.value) }
        _modifiedEmpire.update { state ->
            updateModifiedEmpire.invoke(state, null, testPlanet?.id)
        }

        return if (planet != null && testPlanet == null) {
            onPlanetNotFound()
        } else {
            val newTransport = transportUiState.value.transport.copy(
                planet2Id = planet?.id
            )

            _transportUiState.update { state ->
                state.copy(
                    transport = newTransport,
                    isTransportReady = true
                )
            }
        }
    }

    fun addResource(resource: Resource, isPlanet1: Boolean) {
        val oldTransport = transportUiState.value.transport
        val newTransport = updateResource.invoke(
            resource = resource,
            isPlanet1 = isPlanet1,
            isAdding = true,
            transport = transportUiState.value.transport
        )
        _transportUiState.update { state ->
            state.copy(
                transport = newTransport
            )
        }
        if(oldTransport.cost != newTransport.cost){
            updateTransportUi(
                resource = Resource.ORGANIC_SEDIMENTS,
                isForPlanet1 = true,
                isAdding = true,
                isCostChange = true

            )
        }

        updateTransportUi(
            resource = resource,
            isForPlanet1 = isPlanet1,
            isAdding = true,
            isCostChange = false
        )

    }

    fun removeResource(resource: Resource, isPlanet1: Boolean) {
        val oldTransport = transportUiState.value.transport
        val newTransport = updateResource.invoke(
            resource = resource,
            isPlanet1 = isPlanet1,
            isAdding = false,
            transport = transportUiState.value.transport
        )
        _transportUiState.update { state ->
            state.copy(
                transport = newTransport
            )
        }
        if(oldTransport.cost != newTransport.cost){
            updateTransportUi(
                resource = Resource.ORGANIC_SEDIMENTS,
                isForPlanet1 = true,
                isAdding = false,
                isCostChange = true

            )
        }
        updateTransportUi(
            resource = resource,
            isForPlanet1 = isPlanet1,
            isAdding = false,
            isCostChange = false
        )
    }
}