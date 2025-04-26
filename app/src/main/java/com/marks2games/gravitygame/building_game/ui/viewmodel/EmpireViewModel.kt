package com.marks2games.gravitygame.building_game.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.domain.usecase.GetEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnUseClass
import com.marks2games.gravitygame.building_game.domain.usecase.transport.GetAllTransports
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddArmyProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddExpeditionProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddInfrastructureProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddProgressProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddResearchProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddRocketMaterialsProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTradeActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTransportActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.BuildDistrictActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.ChangeDistrictModeActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.DestroyDistrictActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.SaveEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.CreateNewEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteTransportUseCase
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
    private val addArmyProductionActionUseCase: AddArmyProductionActionUseCase,
    private val addExpeditionProductionActionUseCase: AddExpeditionProductionActionUseCase,
    private val addInfrastructureProductionActionUseCase: AddInfrastructureProductionActionUseCase,
    private val addProgressProductionActionUseCase: AddProgressProductionActionUseCase,
    private val addResearchProductionActionUseCase: AddResearchProductionActionUseCase,
    private val addRocketMaterialsProductionActionUseCase: AddRocketMaterialsProductionActionUseCase,
    private val addTradeActionUseCase: AddTradeActionUseCase,
    private val addTransportActionUseCase: AddTransportActionUseCase,
    private val buildDistrictActionUseCase: BuildDistrictActionUseCase,
    private val changeDistrictModeActionUseCase: ChangeDistrictModeActionUseCase,
    private val destroyDistrictActionUseCase: DestroyDistrictActionUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val deleteTransportUseCase: DeleteTransportUseCase,
    private val getAllTransports: GetAllTransports,
    createNewEmpireUseCase: CreateNewEmpireUseCase
) : ViewModel() {

    private val _empire = MutableStateFlow(createNewEmpireUseCase.invoke())
    val empire: StateFlow<Empire> = _empire.asStateFlow()
    private val _testEmpire = MutableStateFlow(createNewEmpireUseCase.invoke())
    val testEmpire: StateFlow<Empire> = _testEmpire.asStateFlow()
    private val _empireUiState = MutableStateFlow(EmpireUiState())
    val empireUiState: StateFlow<EmpireUiState> = _empireUiState.asStateFlow()

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
                isDistrictDialogShown = false,
                planetForTransport = planet
            )
        }
    }

    fun updateDistrictDialogShown(isShown: Boolean, district: District?) {
        _empireUiState.update { state ->
            state.copy(
                isDistrictDialogShown = isShown,
                districtForDialog = district

            )
        }
    }

    fun updateShowDistrictList(isShown: Boolean, planet: Planet?) {
        val planetId = planet?.id ?: return
        _empireUiState.update { state ->
            state.copy(
                isShownDistrictList = isShown,
                planetIdForDetails = planetId
            )
        }
    }

    fun launchEmpireScreen() {
        viewModelScope.launch {
            val newEmpire = getEmpireUseCase.invoke()
            _empire.update { newEmpire }
            testNewTurn(newEmpire)
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
        viewModelScope.launch {
            val newTurnResult = newTurnUseCase.invoke(empire.value, false)
            val updatedEmpire = newTurnResult.first
            _empire.update { updatedEmpire }
            _empireUiState.update { state ->
                state.copy(
                    errors = emptyList()
                )
            }
            saveEmpireUseCase.invoke(updatedEmpire)
            testNewTurn(updatedEmpire)
        }
    }

    fun updateErrorsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isErrorsShown = isShown
            )
        }
    }

    fun updateActionsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isActionsShown = isShown
            )
        }
    }

    fun updateTransportMenuShown(isShown: Boolean){
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

    fun updateRocketMaterialsSetting(setting: RocketMaterialsSetting) {
        _empireUiState.update { state ->
            state.copy(
                rocketMaterialsProductionSet = setting
            )
        }
    }

    fun updateDistrictToBuild(district: DistrictEnum) {
        _empireUiState.update { state ->
            state.copy(
                districtToBuild = district
            )
        }
    }

    fun openDistrictDetails(planetId: Int, district: District) {
        val planet = testEmpire.value.planets.find { it.id == planetId }
        if (planet == null) {
            return
        }
        val modeIsSelected = if(district is District.Industrial){
             when(district.mode){
                 IndustrialMode.ROCKET_MATERIALS -> IndustrialMode.ROCKET_MATERIALS
                 IndustrialMode.INFRASTRUCTURE -> IndustrialMode.INFRASTRUCTURE
                 IndustrialMode.METAL -> IndustrialMode.METAL
             }
        } else if (district is District.Prospectors) {
            when(district.mode){
                ProspectorsMode.METAL -> ProspectorsMode.METAL
                ProspectorsMode.ORGANIC_SEDIMENTS -> ProspectorsMode.ORGANIC_SEDIMENTS
            }
        } else if(district is District.UrbanCenter){
            when(district.mode){
                UrbanCenterMode.INFLUENCE -> UrbanCenterMode.INFLUENCE
                UrbanCenterMode.RESEARCH -> UrbanCenterMode.RESEARCH
            }
        } else null

        _empireUiState.update { state ->
            state.copy(
                isDistrictDialogShown = true,
                planetIdForDetails = planetId,
                districtForDialog = district,
                modeIsChecked = modeIsSelected,
                armyProductionSet = planet.armyConstructionSetting,
                expeditionsProductionSet = planet.expeditionsSetting,
                progressProductionSet = planet.progressSetting,
                researchProductionSet = planet.researchSetting,
                infrastructureProductionSet = planet.infrastructureSetting,
                rocketMaterialsProductionSet = planet.rocketMaterialsSetting
            )
        }
    }

    fun updateInfrastructureSetting(setting: InfrastructureSetting) {
        _empireUiState.update { state ->
            state.copy(
                infrastructureProductionSet = setting
            )
        }
    }

    fun updateIntProductionState(resource: Resource, value: Int) {
        _empireUiState.update { state ->
            when (resource) {
                Resource.ARMY -> state.copy(armyProductionSet = value)
                Resource.EXPEDITIONS -> state.copy(expeditionsProductionSet = value)
                Resource.RESEARCH -> state.copy(researchProductionSet = value)
                Resource.PROGRESS -> state.copy(progressProductionSet = value)
                else -> state
            }
        }
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

    fun addArmyProductionAction(context: Context, planetId: Int, value: Int) {
        _empire.update { state ->
            state.copy(
                actions = addArmyProductionActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    value
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

    fun addExpeditionProductionAction(context: Context, planetId: Int, value: Int) {
        _empire.update { state ->
            state.copy(
                actions = addExpeditionProductionActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    value
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

    fun addInfrastructureProductionAction(context: Context, planetId: Int, value: InfrastructureSetting) {
        _empire.update { state ->
            state.copy(
                actions = addInfrastructureProductionActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    value
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

    fun addProgressProductionAction(context: Context, planetId: Int, value: Int) {
        _empire.update { state ->
            state.copy(
                actions = addProgressProductionActionUseCase.invoke(
                    empire.value.actions,
                    planetId,
                    value
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

    fun addResearchProductionAction(context: Context, planetId: Int, value: Int) {
        _empire.update { state ->
            state.copy(
                actions = addResearchProductionActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    value
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

    fun addRocketMaterialsProductionAction(context: Context, planetId: Int, value: RocketMaterialsSetting) {
        _empire.update { state ->
            state.copy(
                actions = addRocketMaterialsProductionActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    value
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

    fun buildDistrictAction(context: Context, planetId: Int, district: DistrictEnum, districtId: Int) {
        _empire.update { state ->
            state.copy(
                actions = buildDistrictActionUseCase.invoke(
                    actions = empire.value.actions,
                    planetId = planetId,
                    districtId = districtId,
                    district = district
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

    fun updateModeIsChecked(mode: Enum<*>) {
        _empireUiState.update { state ->
            state.copy(
                modeIsChecked = mode
            )
        }
    }

    fun changeDistrictModeAction(
        district: DistrictEnum,
        districtId: Int,
        mode: Enum<*>?,
        planetId: Int,
        context: Context
    ) {
        _empire.update { state ->
            state.copy(
                actions = changeDistrictModeActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    districtId,
                    district,
                    mode
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

    fun destroyDistrictAction(context: Context, planetId: Int, districtId: Int) {
        _empire.update { state ->
            state.copy(
                actions = destroyDistrictActionUseCase.invoke(
                    testEmpire.value.actions,
                    planetId,
                    districtId
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
