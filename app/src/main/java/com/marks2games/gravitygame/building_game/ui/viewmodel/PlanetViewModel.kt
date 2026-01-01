package com.marks2games.gravitygame.building_game.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetUiState
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.data.util.ActionDescriptionData
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnUseClass
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.UpdateDistrictsForSettleUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.GetTechnologyPriceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.GetUnlockedProductionModesUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.IsTechnologyResearchedUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.transport.CreateTransportListUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddArmyProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddBuildingShipTypeActionUseCase
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
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.SettleDistrictActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.DeleteTransportUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetActionDescriptionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetConsumedResourceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetDistrictBorderColorUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetLockedShipsToBuildUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.GetResourceValueUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.MaxProgressProductionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.MaxResearchProductionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.utils.UpdateActionsUseCase
import com.marks2games.gravitygame.core.data.di.DistrictMap
import com.marks2games.gravitygame.core.data.model.DistrictNode
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.domain.model.MapConfig
import com.marks2games.gravitygame.core.domain.usecases.genericMap.CreateDistrictNodesUseCase
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
import kotlin.math.min
import javax.inject.Inject

@HiltViewModel
class PlanetViewModel @Inject constructor(
    private val newTurnUseCase: NewTurnUseClass,
    override val updateOffset: UpdateOffsetUseCase,
    override val updateButtonSize: UpdateButtonSizeUseCase,
    override val updateMinScale: UpdateMinScaleUseCase,
    override val updateMapSize: UpdateMapSizeUseCase,
    private val createDistrictNodes: CreateDistrictNodesUseCase,
    private val getDistrictBorder: GetDistrictBorderColorUseCase,
    private val getTechnologyPrice: GetTechnologyPriceUseCase,
    private val getActionDescription: GetActionDescriptionUseCase,
    private val getAllTransports: CreateTransportListUseCase,
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
    private val settleDistrictAction: SettleDistrictActionUseCase,
    private val addShipTypeBuildAction: AddBuildingShipTypeActionUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val deleteTransportUseCase: DeleteTransportUseCase,
    private val updateEmpireList: UpdateActionsUseCase,
    private val getResourceValue: GetResourceValueUseCase,
    private val getConsumedResource: GetConsumedResourceUseCase,
    private val isTechnologyResearched: IsTechnologyResearchedUseCase,
    private val getUnlockedProductionModes: GetUnlockedProductionModesUseCase,
    private val getLockedShipsUseCase: GetLockedShipsToBuildUseCase,
    private val maxResearchProduction: MaxResearchProductionUseCase,
    private val maxProgressProduction: MaxProgressProductionUseCase,
    private val createTransports: CreateTransportListUseCase,
    private val updateDistrictsForSettle: UpdateDistrictsForSettleUseCase,
    @DistrictMap override val mapConfig: MapConfig,
    getToroidalPositions: GetToroidalPositionsUseCase<DistrictNode>,
    getNodePosition: GetNodePositionUseCase<DistrictNode>
) : MapViewModel<DistrictNode>(
    updateOffset = updateOffset,
    updateButtonSize = updateButtonSize,
    updateMinScale = updateMinScale,
    updateMapSize = updateMapSize,
    mapConfig = mapConfig,
    getToroidalPositions = getToroidalPositions,
    getNodePosition = getNodePosition
) {

    private val _planetUiState = MutableStateFlow(PlanetUiState())
    val planetUiState = _planetUiState.asStateFlow()

    fun launchPlanetScreen(planetId: Int?, empire: Empire, testEmpire: Empire){
        _planetUiState.update { state ->
            state.copy(
                planetId = planetId,
                empire = empire,
                testEmpire = testEmpire,
                errors = newTurnUseCase.invoke(empire, true).second,
                transports = createTransports.invoke(empire, planetId),
                actions = empire.actions.filter{it.planetId == planetId}
            )
        }
        val planet = empire.planets.find { it.id == planetId }
        updateDistrictNodes(planet)
    }
    fun updateDistrictNodes(planet: Planet?){
        val districts = planet?.let{
            updateDistrictsForSettle.invoke(planet.districts, planet)
        }?: emptyList()

        Log.d("PlanetViewModel", "updateDistrictNodes: $planet")
        planet?.let{
            val nodes = createDistrictNodes.invoke(planet.type, districts)
            val updatedNodes = nodes.map { node ->
                val newColor = getDistrictBorder(node.district)
                node.copy(buttonColor = newColor)
            }
            updateNodes(updatedNodes)
            Log.d("PlanetViewModel", "UpdatedDistrictNodes success.")
        }
    }

    fun canSettleDistrict(district: District?): Boolean{
        return district?.let{
            getDistrictBorder(it) == Color.Green
        } ?: false
    }

    fun updateNodesColor(planet: Planet?){
        val districts = planet?.let {
            updateDistrictsForSettle.invoke(it.districts, it)
        }?: emptyList()
        val updatedDistricts = mapUiState.value.nodes.map{ node ->
            node.copy(district = districts.first{ it.districtId == node.district.districtId })
        }
        val updatedNodes = planet?.let{ _ ->
            updatedDistricts.map { node ->
                val newColor = getDistrictBorder(node.district)
                node.copy(buttonColor = newColor)
            }
        }
        updatedNodes?.let{updateNodes(it)}
    }

    private fun getDistrictBorder(district: District): Color{
        val testPlanet = planetUiState.value.testEmpire?.planets?.find { it.id == planetUiState.value.planetId }
        val planet = planetUiState.value.empire?.planets?.find { it.id == planetUiState.value.planetId }
        val progress =
            min(testPlanet?.progress ?: 0, planet?.progress ?: 0) >= (testPlanet?.planetGrowthBorder
                ?: 0)
        return getDistrictBorder.invoke(district, progress)
    }

    fun isTechnologyResearched(technologyEnum: TechnologyEnum): Boolean {
        return isTechnologyResearched.invoke(technologyEnum, planetUiState.value.empire?.technologies)
    }

    fun getLockedShips(): Set<ShipType> {
        return getLockedShipsUseCase.invoke(planetUiState.value.empire?.technologies)
    }

    fun updateBuildingShip(selectedShip: ShipType){
        _planetUiState.update { it.copy(buildingShip = selectedShip) }
    }

    fun isErrorListEmpty(): Boolean {
        return planetUiState.value.errors.isEmpty()
    }

    fun onErrorMenuClick(){
        if(planetUiState.value.isErrorsShown){
            updateErrorsShown(false)
        } else {
            updateErrorsShown(true)
            updateActionsShown(false)
            updateTransportMenuShown(false)
        }
    }

    fun onActionMenuClick(){
        if(planetUiState.value.isActionsShown){
            updateActionsShown(false)
        } else {
            updateActionsShown(true)
            updateErrorsShown(false)
            updateTransportMenuShown(false)
        }
    }

    fun onTransportMenuClick(){
        if(planetUiState.value.isTransportMenuShown){
            updateTransportMenuShown(false)
        } else {
            updateTransportMenuShown(true)
            updateErrorsShown(false)
            updateActionsShown(false)
        }
    }

    private fun updateErrorsShown(isShown: Boolean) {
        _planetUiState.update { state ->
            state.copy(
                isErrorsShown = isShown
            )
        }
    }

    private fun updateActionsShown(isShown: Boolean) {
        _planetUiState.update { state ->
            state.copy(
                isActionsShown = isShown
            )
        }
    }

    private fun updateTransportMenuShown(isShown: Boolean){
        _planetUiState.update{ state ->
            state.copy(
                isTransportMenuShown = isShown
            )
        }
    }

    fun getAllTransports(): List<Transport> {
        return getAllTransports.invoke(planetUiState.value.empire, planetUiState.value.planetId)
    }

    fun deleteAllTransports(){
        planetUiState.value.empire?.let{ empire ->
            _planetUiState.update { state ->
                state.copy(
                    transports = emptyList()
                )
            }
            val newTransports = empire.transports.filterNot {
                it.planet1Id == planetUiState.value.planetId || it.planet2Id == planetUiState.value.planetId
            }
            val updatedEmpire = empire.copy(
                transports = newTransports
            )
            testTurn(updatedEmpire)
        }
    }

    fun getTechnologyPrice(): Int{
        return getTechnologyPrice.invoke(
            planetUiState.value.empire?.technologies
        )
    }

    fun getActionDescription(action: Action): ActionDescriptionData {
        return getActionDescription.invoke(action, planetUiState.value.empire)
    }

    fun deleteAction(action: Action){
        _planetUiState.update { state ->
            state.copy(
                actions = deleteActionUseCase.invoke(
                    action = action,
                    actions = planetUiState.value.actions
                )
            )
        }
        var empire = _planetUiState.value.empire
        empire = empire?.copy(
            actions = updateEmpireList.invoke(
                _planetUiState.value.actions,
                planetUiState.value.empire?.actions ?: emptyList(),
                planetUiState.value.planetId
            )
        )
        empire?.let { testTurn(it) }
        if(action is Action.DistrictAction.SettleDistrict){
            updateDistrictNodes(planetUiState.value.testEmpire?.planets?.find { it.id == planetUiState.value.planetId })
        }
    }

    fun deleteTransport(transport: Transport){
        _planetUiState.update { state ->
            state.copy(
                transports = deleteTransportUseCase.invoke(
                    transport = transport,
                    transports = planetUiState.value.transports
                )
            )
        }
        var empire = _planetUiState.value.empire
        empire = empire?.copy(
            transports = deleteTransportUseCase.invoke(
                transport = transport,
                transports = planetUiState.value.empire?.transports ?: emptyList()
            )
        )
        empire?.let { testTurn(it) }
    }

    fun deleteAllActions(){
        _planetUiState.update { state ->
            state.copy(
                actions = emptyList()
            )
        }
        var empire = _planetUiState.value.empire
        empire = empire?.copy(
            actions = updateEmpireList.invoke(
                emptyList(),
                planetUiState.value.empire?.actions ?: emptyList(),
                planetUiState.value.planetId
            )
        )

        empire?.let {
            testTurn(it)
        }
        val planet = planetUiState.value.testEmpire?.planets?.find { planet -> planet.id == planetUiState.value.planetId }
        updateNodesColor(planet)
    }

    fun closeTransportDialog(){
        _planetUiState.update { state ->
            state.copy(
                isTransportDialogShown = false,
            )
        }
    }

    fun updateDistrictDialogShown(isShown: Boolean, district: District?) {
        _planetUiState.update { state ->
            state.copy(
                isDistrictDialogShown = isShown,
                districtForDialog = district
            )
        }
    }

    fun openDistrictDetails(district: District) {
        val planet = planetUiState.value.empire?.planets?.find { it.id == planetUiState.value.planetId }
        if (planet == null) {
            return
        }
        val modeIsSelected = when (district) {
            is District.Industrial -> {
                when(district.mode){
                    IndustrialMode.ROCKET_MATERIALS -> IndustrialMode.ROCKET_MATERIALS
                    IndustrialMode.INFRASTRUCTURE -> IndustrialMode.INFRASTRUCTURE
                    IndustrialMode.METAL -> IndustrialMode.METAL
                }
            }

            is District.Prospectors -> {
                when(district.mode){
                    ProspectorsMode.METAL -> ProspectorsMode.METAL
                    ProspectorsMode.ORGANIC_SEDIMENTS -> ProspectorsMode.ORGANIC_SEDIMENTS
                }
            }

            is District.UrbanCenter -> {
                when(district.mode){
                    UrbanCenterMode.INFLUENCE -> UrbanCenterMode.INFLUENCE
                    UrbanCenterMode.RESEARCH -> UrbanCenterMode.RESEARCH
                }
            }

            else -> null
        }

        _planetUiState.update { state ->
            state.copy(
                isDistrictDialogShown = true,
                districtForDialog = district,
                modeIsChecked = modeIsSelected,
                armyProductionSet = planet.armyConstructionSetting.toString(),
                expeditionsProductionSet = planet.expeditionsSetting.toString(),
                progressProductionSet = planet.progressSetting.toString(),
                researchProductionSet = planet.researchSetting.toString(),
                infrastructureProductionSet = planet.infrastructureSetting,
                rocketMaterialsProductionSet = planet.rocketMaterialsSetting,
                buildingShip = planet.buildingShip
            )
        }
    }

    fun testTurn(empire: Empire?){
        empire?.let{ empire ->
            planetUiState.value.planetId?.let{
                val result =
                    newTurnUseCase.invoke(empire, true, listOf(it))
                _planetUiState.update { state ->
                    state.copy(
                        testEmpire = result.first,
                        errors = result.second
                    )
                }
            }
        }
    }

    private fun callToast(context: Context){
        Toast.makeText(
            context,
            context.getString(R.string.actionAdded),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateStatesAfterActionAdded(newActions: List<Action>, context: Context){
        _planetUiState.update { state ->
            state.copy(
                actions = newActions
            )
        }
        val updatedEmpire = planetUiState.value.empire?.copy(
            actions = updateEmpireList.invoke(
                newActions,
                planetUiState.value.empire?.actions ?: emptyList(),
                planetUiState.value.planetId
            )
        )
        callToast(context)
        updatedEmpire?.let {
            testTurn(it)
        }
    }

    fun addShipTypeBuildAction(context: Context, value: ShipType) {
        val newActions = addShipTypeBuildAction.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)

    }

    fun addArmyProductionAction(context: Context, value: Int) {
        val newActions = addArmyProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addExpeditionProductionAction(context: Context, value: Int) {
        val newActions = addExpeditionProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addInfrastructureProductionAction(context: Context, value: InfrastructureSetting) {
        val newActions = addInfrastructureProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addProgressProductionAction(context: Context, value: Int) {
        val newActions = addProgressProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addResearchProductionAction(context: Context, value: Int) {
        val newActions = addResearchProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addRocketMaterialsProductionAction(context: Context, value: RocketMaterialsSetting) {
        val newActions = addRocketMaterialsProductionActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            value
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addTradeAction(context: Context, trade: Trade) {
        val newActions = addTradeActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            trade
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun addTransportAction(context: Context, transport: Transport) {
        val newActions = addTransportActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            transport
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun buildDistrictAction(context: Context, district: DistrictEnum, districtId: Int) {
        val newActions = buildDistrictActionUseCase.invoke(
                    actions = planetUiState.value.actions,
                    planetId = planetUiState.value.planetId,
                    districtId = districtId,
                    district = district
                )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun updateModeIsChecked(mode: Enum<*>) {
        _planetUiState.update { state ->
            state.copy(
                modeIsChecked = mode
            )
        }
    }

    fun changeDistrictModeAction(
        district: DistrictEnum,
        districtId: Int,
        mode: Enum<*>?,
        context: Context
    ) {
        val newActions = changeDistrictModeActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            districtId,
            district,
            mode
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun settleDistrictAction(context: Context, districtId: Int?){
        val newActions = districtId?.let{
            settleDistrictAction.invoke(
                planetUiState.value.actions,
                planetUiState.value.planetId,
                it
            )
        }?: emptyList()
        updateStatesAfterActionAdded(newActions, context)
        val planet = planetUiState.value.testEmpire?.planets?.first { it.id == planetUiState.value.planetId }
        updateNodesColor(planet)
    }

    fun destroyDistrictAction(context: Context, districtId: Int) {
        val newActions = destroyDistrictActionUseCase.invoke(
            planetUiState.value.actions,
            planetUiState.value.planetId,
            districtId
        )
        updateStatesAfterActionAdded(newActions, context)
    }

    fun updateRocketMaterialsSetting(setting: RocketMaterialsSetting) {
        _planetUiState.update { state ->
            state.copy(
                rocketMaterialsProductionSet = setting
            )
        }
    }

    fun updateDistrictToBuild(district: DistrictEnum) {
        _planetUiState.update { state ->
            state.copy(
                districtToBuild = district
            )
        }
    }

    fun updateInfrastructureSetting(setting: InfrastructureSetting) {
        _planetUiState.update { state ->
            state.copy(
                infrastructureProductionSet = setting
            )
        }
    }

    fun updateIntProductionState(resource: Resource, value: String) {
        _planetUiState.update { state ->
            when (resource) {
                Resource.ARMY -> state.copy(armyProductionSet = value)
                Resource.EXPEDITIONS -> state.copy(expeditionsProductionSet = value)
                Resource.RESEARCH -> state.copy(researchProductionSet = value)
                Resource.PROGRESS -> state.copy(progressProductionSet = value)
                else -> state
            }
        }
    }

    fun getConsumedResource(resource: Resource, isForProspectors: Boolean): Pair<Resource?, Resource?>{
        return getConsumedResource.invoke(resource, isForProspectors)
    }

    fun getResourceValue(resource: Resource, isForProspectors: Boolean): Triple<Int?, Int?, Int?>{
        return getResourceValue.invoke(resource, isForProspectors)
    }

    fun calculateMaxProgressProduction(planet: Planet): Int{
        return maxProgressProduction(planet, planetUiState.value.actions, planetUiState.value.empire?.technologies)
    }

    fun calculateMaxResearchProduction(planet: Planet): Int{
        return maxResearchProduction.invoke(planet)
    }

    fun updateTransportDialogShown(toShow: Boolean){
        _planetUiState.update { state ->
            state.copy(
                isTransportDialogShown = toShow
            )
        }
    }

    fun getUnlockedProductionModes(district: District): List<Enum<*>>{
        return getUnlockedProductionModes.invoke(planetUiState.value.empire?.technologies, district)
    }
}
