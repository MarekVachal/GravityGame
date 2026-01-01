package com.marks2games.gravitygame.building_game.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.data.util.ActionDescriptionData
import com.marks2games.gravitygame.building_game.domain.usecase.GetEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnUseClass
import com.marks2games.gravitygame.building_game.domain.usecase.technology.GetTechnologyPriceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.CreateTransportListUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTradeActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTransportActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.SaveEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.CreateNewEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteTransportUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetActionDescriptionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.UpdateActionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpireViewModel @Inject constructor(
    private val getEmpireUseCase: GetEmpireUseCase,
    private val newTurnUseCase: NewTurnUseClass,
    private val saveEmpireUseCase: SaveEmpireUseCase,
    private val addTradeActionUseCase: AddTradeActionUseCase,
    private val addTransportActionUseCase: AddTransportActionUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val deleteTransportUseCase: DeleteTransportUseCase,
    private val getAllTransports: CreateTransportListUseCase,
    private val getActionDescription: GetActionDescriptionUseCase,
    private val getTechnologyPrice: GetTechnologyPriceUseCase,
    private val updateEmpireList: UpdateActionsUseCase,
    createNewEmpireUseCase: CreateNewEmpireUseCase
) : ViewModel() {

    private val _empire = MutableStateFlow(createNewEmpireUseCase.invoke())
    val empire: StateFlow<Empire> = _empire.asStateFlow()
    private val _testEmpire = MutableStateFlow(createNewEmpireUseCase.invoke())
    val testEmpire: StateFlow<Empire> = _testEmpire.asStateFlow()
    private val _empireUiState = MutableStateFlow(EmpireUiState())
    val empireUiState: StateFlow<EmpireUiState> = _empireUiState.asStateFlow()

    fun updateActionsAfterBackToEmpireScreen(planetId: Int?, actions: List<Action>){
        _empire.update { state ->
            state.copy(
                actions = updateEmpireList.invoke(
                    actions,
                    empire.value.actions,
                    planetId
                )
            )
        }
        val result = newTurnUseCase.invoke(empire.value, true)
        _testEmpire.update { result.first }
        _empireUiState.update { state ->
            state.copy(
                errors = result.second
            )
        }
    }

    fun isErrorListEmpty(): Boolean {
        return empireUiState.value.errors.isEmpty()
    }

    fun onErrorMenuClick(){
        if(empireUiState.value.isErrorsShown){
            updateErrorsShown(false)
        } else {
            updateErrorsShown(true)
            updateActionsShown(false)
            updateTransportMenuShown(false)
        }
    }

    fun onActionMenuClick(){
        if(empireUiState.value.isActionsShown){
            updateActionsShown(false)
        } else {
            updateActionsShown(true)
            updateErrorsShown(false)
            updateTransportMenuShown(false)
        }
    }

    fun onTransportMenuClick(){
        if(empireUiState.value.isTransportMenuShown){
            updateTransportMenuShown(false)
        } else {
            updateTransportMenuShown(true)
            updateErrorsShown(false)
            updateActionsShown(false)
        }
    }

    fun getTechnologyPrice(): Int{
        return getTechnologyPrice.invoke(
            empire.value.technologies
        )
    }

    fun setResearchingTechnology(newTechnologies: List<Technology>){
        _empire.update { it.copy(technologies = newTechnologies) }
    }

    fun getActionDescription(action: Action): ActionDescriptionData {
        return getActionDescription.invoke(action, empire.value)
    }

    fun deleteAction(action: Action){
        _empire.update { state ->
            state.copy(
                actions = deleteActionUseCase.invoke(
                    action = action,
                    actions = empire.value.actions
                )
            )
        }
        val updatedEmpire = _empire.value
        testEmpireAfterActionChange(updatedEmpire)
    }

    fun deleteTransport(transport: Transport){
        _empire.update { state ->
            state.copy(
                transports = deleteTransportUseCase.invoke(
                    transport = transport,
                    transports = empire.value.transports
                )
            )
        }
        val updatedEmpire = _empire.value
        testEmpireAfterActionChange(updatedEmpire)
    }

    fun deleteAllActions(){
        _empire.update { state ->
            state.copy(
                actions = emptyList()
            )
        }
        val updatedEmpire = _empire.value
        testEmpireAfterActionChange(updatedEmpire)
    }

    fun closeTransportDialog(){
        _empireUiState.update { state ->
            state.copy(
                isTransportDialogShown = false,
                planetForTransport = null
            )
        }
    }

    fun openTransportDialog(planet: Planet){
        _empireUiState.update { state ->
            state.copy(
                isTransportDialogShown = true,
                planetForTransport = planet
            )
        }
    }

    fun launchEmpireScreen() {
        viewModelScope.launch {
            val newEmpire = getEmpireUseCase.invoke()
            _empire.update { newEmpire }
            testNewTurn(newEmpire)

            updateHasLaunchedEmpireScreen(true)
        }
    }

    fun updateHasLaunchedEmpireScreen(hasLaunched: Boolean){
        _empire.update { state ->
            state.copy(
                hasLaunched = hasLaunched
            )
        }
    }


    private fun testNewTurn(empire: Empire) {
        val newTurnTestResult = newTurnUseCase.invoke(empire, true)
        _testEmpire.update { newTurnTestResult.first }
        _empireUiState.update { state ->
            state.copy(
                errors = newTurnTestResult.second
            )
        }
    }

    fun newTurn() {
        val newTurnResult = newTurnUseCase.invoke(empire.value, false)
        val updatedEmpire = newTurnResult.first
        _empire.update { updatedEmpire }
        _empireUiState.update { state ->
            state.copy(
                errors = emptyList()
            )
        }
        testNewTurn(updatedEmpire)
        viewModelScope.launch {
            saveEmpireUseCase.invoke(updatedEmpire)
        }
    }

    private fun updateErrorsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isErrorsShown = isShown
            )
        }
    }

    private fun updateActionsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isActionsShown = isShown
            )
        }
    }

    private fun updateTransportMenuShown(isShown: Boolean){
        _empireUiState.update{ state ->
            state.copy(
                isTransportMenuShown = isShown
            )
        }
    }

    fun deleteAllTransports(){
        _empire.update { state ->
            state.copy(
                transports = emptyList()
            )
        }
        val updatedEmpire = _empire.value
        testEmpireAfterActionChange(updatedEmpire)
    }

    fun getAllTransports(): List<Transport> {
        return getAllTransports.invoke(empire.value)
    }

    private fun testEmpireAfterActionChange(empire: Empire){
        val result =
            newTurnUseCase.invoke(empire, true)
        _empireUiState.update { state ->
            state.copy(
                errors = result.second
            )
        }
        _testEmpire.update { result.first }
    }

    fun addTradeAction(context: Context, planetId: Int, trade: Trade) {
        _empire.update { state ->
            state.copy(
                actions = addTradeActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    trade
                )

            )
        }
        val updatedEmpire = _empire.value
        Toast.makeText(
            context,
            context.getString(R.string.actionAdded),
            Toast.LENGTH_SHORT
        ).show()
        testEmpireAfterActionChange(updatedEmpire)
    }

    fun addTransportAction(context: Context ,planetId: Int, transport: Transport) {
        _empire.update { state ->
            state.copy(
                actions = addTransportActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    transport
                )
            )
        }
        val updatedEmpire = _empire.value
        Toast.makeText(
            context,
            context.getString(R.string.actionAdded),
            Toast.LENGTH_SHORT
        ).show()
        testEmpireAfterActionChange(updatedEmpire)
    }
}
