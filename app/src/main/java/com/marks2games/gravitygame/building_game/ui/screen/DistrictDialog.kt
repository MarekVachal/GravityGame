package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.District.ExpeditionPlatform
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetUiState
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.ui.viewmodel.PlanetViewModel
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.ui.utils.NumberInputField
import kotlin.reflect.KClass

@Composable
fun DistrictDialog(
    modifier: Modifier = Modifier,
    toShow: Boolean,
    planet: Planet?,
    planets: List<Planet>,
    district: District?,
    planetModel: PlanetViewModel,
    planetUiState: PlanetUiState
) {
    val context = LocalContext.current
    if (toShow && planet != null) {
        Dialog(
            onDismissRequest = { planetModel.updateDistrictDialogShown(false, null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentWidth()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)
                        .wrapContentWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row{
                        Text(stringResource(district?.type?.nameIdNominative?: R.string.unknown_district))
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                    )
                    if (district?.type == DistrictEnum.PROSPECTORS || district?.type == DistrictEnum.INDUSTRIAL || district?.type == DistrictEnum.URBAN_CENTER) {
                        Row(
                            modifier = modifier.wrapContentWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    planetModel.changeDistrictModeAction(
                                        district = district.type,
                                        districtId = district.districtId,
                                        mode = planetUiState.modeIsChecked,
                                        context = context
                                    )

                                }
                            ) {
                                Text(text = stringResource(R.string.changeDistrictModeName))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            ModeSelector(
                                district = district,
                                selectedMode = planetUiState.modeIsChecked,
                                onCheckedChange = { checked ->
                                    planetModel.updateModeIsChecked(checked)
                                },
                                planetModel = planetModel,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    }
                    when (district) {
                        is District.Industrial -> {
                            if (district.mode == IndustrialMode.INFRASTRUCTURE) {
                                SpinnerProductionRow(
                                    actionFunction = {
                                        planetModel.addInfrastructureProductionAction(
                                            context = context,
                                            planetUiState.infrastructureProductionSet
                                        )
                                    },
                                    enumClass = InfrastructureSetting::class,
                                    onItemSelected = { selected ->
                                        planetModel.updateInfrastructureSetting(
                                            selected
                                        )
                                    },
                                    label = stringResource(planetUiState.infrastructureProductionSet.nameId)
                                )
                            } else {
                                SpinnerProductionRow(
                                    actionFunction = {
                                        planetModel.addRocketMaterialsProductionAction(
                                            context = context,
                                            planetUiState.rocketMaterialsProductionSet
                                        )
                                    },
                                    enumClass = RocketMaterialsSetting::class,
                                    onItemSelected = { selected ->
                                        planetModel.updateRocketMaterialsSetting(
                                            selected
                                        )
                                    },
                                    label = stringResource(planetUiState.rocketMaterialsProductionSet.nameId)
                                )
                            }
                        }

                        is District.Capitol -> {
                            ProductionSetRow(
                                modifier = Modifier.wrapContentWidth(),
                                actionFunction = {
                                    planetModel.addProgressProductionAction(
                                        context = context,
                                        planetUiState.progressProductionSet.toInt()
                                    )
                                },
                                value = planetUiState.progressProductionSet,
                                onValueChange = { newValue ->
                                    planetModel.updateIntProductionState(
                                        Resource.PROGRESS,
                                        newValue
                                    )
                                },
                                producedResource = Resource.PROGRESS,
                                planetModel = planetModel,
                                maxValue = planetModel.calculateMaxProgressProduction(planet = planet)
                            )
                        }

                        is ExpeditionPlatform -> {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                ProductionSetRow(
                                    modifier = Modifier.wrapContentWidth(),
                                    actionFunction = {
                                        planetModel.addArmyProductionAction(
                                            context = context,
                                            planetUiState.armyProductionSet.toInt()
                                        )
                                    },
                                    value = planetUiState.armyProductionSet,
                                    onValueChange = { newValue ->
                                        planetModel.updateIntProductionState(
                                            Resource.ARMY,
                                            newValue
                                        )
                                    },
                                    producedResource = Resource.ARMY,
                                    planetModel = planetModel,
                                    maxValue = 10
                                )
                                DropdownSelector(
                                    modifier = modifier,
                                    enumClass = ShipType::class,
                                    onItemSelected = { selected ->
                                        planetModel.updateBuildingShip(selected)
                                        planetModel.addShipTypeBuildAction(context, selected)
                                    },
                                    disabledItems = planetModel.getLockedShips(),
                                    label = stringResource(planetUiState.buildingShip?.nameNominative
                                        ?: R.string.noShip)
                                )
                            }

                            ProductionSetRow(
                                modifier = Modifier.wrapContentWidth(),
                                actionFunction = {
                                    planetModel.addExpeditionProductionAction(
                                        context = context,
                                        planetUiState.expeditionsProductionSet.toInt()
                                    )
                                },
                                value = planetUiState.expeditionsProductionSet,
                                onValueChange = { newValue ->
                                    planetModel.updateIntProductionState(
                                        Resource.EXPEDITIONS,
                                        newValue
                                    )
                                },
                                producedResource = Resource.EXPEDITIONS,
                                planetModel = planetModel,
                                maxValue = 10
                            )
                            Row {
                                Button(
                                    onClick = { planetModel.updateTransportDialogShown(true) },
                                    enabled = planets.size > 1 && planetModel.isTechnologyResearched(
                                        TechnologyEnum.TRANSPORT_TECHNOLOGY)
                                ) { Text(stringResource(R.string.transport)) }
                            }
                        }

                        is District.UrbanCenter -> {
                            if (district.mode == UrbanCenterMode.RESEARCH) {
                                ProductionSetRow(
                                    modifier = Modifier.wrapContentWidth(),
                                    actionFunction = {
                                        planetModel.addResearchProductionAction(
                                            context = context,
                                            planetUiState.researchProductionSet.toInt()
                                        )
                                    },
                                    value = planetUiState.researchProductionSet,
                                    onValueChange = { newValue ->
                                        planetModel.updateIntProductionState(
                                            Resource.RESEARCH,
                                            newValue
                                        )
                                    },
                                    producedResource = Resource.RESEARCH,
                                    planetModel = planetModel,
                                    maxValue = planetModel.calculateMaxResearchProduction(planet)
                                )
                            }
                        }

                        is District.Empty -> {
                            val isExpeditionPlatformResearched = planetModel.isTechnologyResearched(TechnologyEnum.SPACE_TRAVELLING)

                            val excludedItems = if (planet.districts.any { it is ExpeditionPlatform }) {
                                setOf(
                                    DistrictEnum.EMPTY,
                                    DistrictEnum.CAPITOL,
                                    DistrictEnum.EXPEDITION_PLATFORM,
                                    DistrictEnum.IN_CONSTRUCTION
                                )
                            } else {
                                setOf(
                                    DistrictEnum.EMPTY,
                                    DistrictEnum.CAPITOL,
                                    DistrictEnum.IN_CONSTRUCTION
                                )
                            }

                            val disabledItems = if (!isExpeditionPlatformResearched) {
                                setOf(DistrictEnum.EXPEDITION_PLATFORM)
                            } else {
                                emptySet()
                            }
                            SpinnerProductionRow(
                                actionFunction = {
                                    planetModel.buildDistrictAction(
                                        context = context,
                                        planetUiState.districtToBuild,
                                        district.districtId
                                    )
                                },
                                textOnButton = stringResource(R.string.buildDistrict),
                                enumClass = DistrictEnum::class,
                                onItemSelected = { selected ->
                                    planetModel.updateDistrictToBuild(
                                        selected
                                    )
                                },
                                excludedItems = excludedItems,
                                disabledItems = disabledItems,
                                label = stringResource(planetUiState.districtToBuild.nameIdNominative)
                            )
                        }
                        is District.InConstruction -> {
                            Row{
                                Text(
                                    stringResource(
                                        R.string.inConstruction,
                                        stringResource(R.string.infrastructure),
                                        district.infra,
                                        DISTRICT_BUILD_COST
                                    )
                                )
                            }
                            Row{
                                Text(
                                    stringResource(
                                        R.string.districtToBuildDescription,
                                        stringResource(planetUiState.districtToBuild.nameIdNominative)
                                    )
                                )
                            }
                        }
                        else -> println("Do nothing")
                    }
                    if(district !is District.Capitol && district !is District.Empty && district !is District.Unnocupated){
                        Button(
                            onClick = {
                                planetModel.destroyDistrictAction(
                                    context = context,
                                    districtId = district?.districtId ?: 0
                                )
                            }
                        ) { Text(stringResource(R.string.destroyDistrict)) }
                    }
                    if(planetModel.canSettleDistrict(district)){
                        Button(
                            onClick = {
                                planetModel.settleDistrictAction(context, district?.districtId)
                                planetModel.updateDistrictDialogShown(false, null)
                            }
                        ) { Text(stringResource(R.string.settleNewDistrict))}
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductionSetRow(
    modifier: Modifier = Modifier,
    actionFunction: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    producedResource: Resource,
    planetModel: PlanetViewModel,
    maxValue: Int
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                actionFunction()
            }
        ) { Text(stringResource(R.string.setProduction)) }

        Spacer(modifier = Modifier.width(8.dp))

        NumberInputField(
            value = value,
            onValueChange = onValueChange,
            label = {
                ProductionLabelRow(
                    producedResource = producedResource,
                    planetModel = planetModel,
                    isForProspectors = false,
                    modifier = Modifier.wrapContentWidth()
                )
            },
            maxValue = maxValue,
            modifier = Modifier.wrapContentWidth()
        )
    }
}

@Composable
private fun ProductionLabelRow(
    modifier: Modifier = Modifier,
    producedResource: Resource,
    isForProspectors: Boolean,
    planetModel: PlanetViewModel
) {
    val (consumedResource1Enum, consumedResource2Enum) = planetModel.getConsumedResource(producedResource, isForProspectors)
    val (producedResourceValue, consumedResource1, consumedResource2) = planetModel.getResourceValue(producedResource, isForProspectors)
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        consumedResource1Enum?.let{ resource ->
            consumedResource1?.let{ value ->
                DisplayResourceBox(resource = resource, value = value)
            }
        }
        consumedResource2Enum?.let{ resource ->
            consumedResource2?.let{ value ->
                Text(" + ")
                DisplayResourceBox(resource = resource, value = value)
            }
        }
        if(consumedResource1Enum != null ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow back",
                tint = Color.Unspecified
            )
        }
        producedResourceValue?.let{
            DisplayResourceBox(resource = producedResource, value = it)
        }
    }
}

@Composable
private fun DisplayResourceBox(resource: Resource, value: Int){
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .wrapContentWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(value.toString())
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(resource.icon),
                contentDescription = "Resource icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }

}

@Composable
private fun <T : Enum<T>> SpinnerProductionRow(
    modifier: Modifier = Modifier,
    actionFunction: () -> Unit,
    textOnButton: String = stringResource(R.string.setProduction),
    enumClass: KClass<T>,
    onItemSelected: (T) -> Unit,
    excludedItems: Set<T> = emptySet(),
    disabledItems: Set<T> = emptySet(),
    label: String = ""
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { actionFunction() }
        ) { Text(textOnButton) }

        Spacer(modifier = Modifier.width(8.dp))

        DropdownSelector(
            enumClass = enumClass,
            onItemSelected = onItemSelected,
            excludedItems = excludedItems,
            disabledItems = disabledItems,
            label = label
        )
    }
}

@Composable
private fun ModeSelector(
    modifier: Modifier = Modifier,
    district: District,
    selectedMode: Enum<*>?,
    onCheckedChange: (Enum<*>) -> Unit,
    planetModel: PlanetViewModel
) {
    val openModes = planetModel.getUnlockedProductionModes(district)
    val modes = when (district.type) {
        DistrictEnum.PROSPECTORS -> listOf(
            ProspectorsMode.METAL,
            ProspectorsMode.ORGANIC_SEDIMENTS
        )

        DistrictEnum.INDUSTRIAL -> listOf(
            IndustrialMode.INFRASTRUCTURE,
            IndustrialMode.ROCKET_MATERIALS,
            IndustrialMode.METAL
        )

        DistrictEnum.URBAN_CENTER -> listOf(
            UrbanCenterMode.INFLUENCE,
            UrbanCenterMode.RESEARCH
        )

        else -> return
    }

    Column (
        modifier = modifier
            .wrapContentWidth()
            .width(IntrinsicSize.Max)
    ){
        modes.forEach { mode ->
            val (label, resource) = when (mode) {
                ProspectorsMode.METAL -> stringResource(R.string.metal) to Resource.METAL
                IndustrialMode.METAL -> stringResource(R.string.metal) to Resource.METAL
                ProspectorsMode.ORGANIC_SEDIMENTS -> stringResource(R.string.organic_sediments) to Resource.ORGANIC_SEDIMENTS
                IndustrialMode.INFRASTRUCTURE -> stringResource(R.string.infrastructure) to Resource.INFRASTRUCTURE
                IndustrialMode.ROCKET_MATERIALS -> stringResource(R.string.rocket_materials) to Resource.ROCKET_MATERIALS
                UrbanCenterMode.INFLUENCE -> stringResource(R.string.influence) to Resource.INFLUENCE
                UrbanCenterMode.RESEARCH -> stringResource(R.string.research) to Resource.RESEARCH
                else -> return
            }
            val isForProspectors = mode is ProspectorsMode
            val isEnabled = openModes.contains(mode)

            ProductionLabelRow(
                modifier = Modifier.wrapContentWidth(),
                producedResource = resource,
                planetModel = planetModel,
                isForProspectors = isForProspectors
            )

            OptionRow(
                modifier = Modifier.wrapContentWidth(),
                text = label,
                selected = selectedMode == mode,
                onClick = { if (isEnabled) onCheckedChange(mode) },
                enabled = isEnabled
            )
            HorizontalDivider(modifier = modifier.wrapContentWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OptionRow(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .wrapContentWidth()
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled

        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> DropdownSelector(
    modifier: Modifier = Modifier,
    enumClass: KClass<T>,
    onItemSelected: (T) -> Unit,
    excludedItems: Set<T> = emptySet(),
    disabledItems: Set<T> = emptySet(),
    label: String = ""
) {
    val items = remember { enumClass.java.enumConstants?.filter { it !in excludedItems } }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { }
    ) {
        Text(
            text = label,
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, expanded)
                .clickable { expanded = true }
                .padding(16.dp)
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .width(IntrinsicSize.Min),
            expanded = expanded,
            onDismissRequest = { }
        ) {
            items?.forEach { item ->
                val itemName = stringResource(
                    when(item) {
                        is DistrictEnum -> item.nameIdNominative
                        is InfrastructureSetting -> item.nameId
                        is RocketMaterialsSetting -> item.nameId
                        is ShipType -> item.nameNominative
                        else -> R.string.unknown
                    }
                )
                val isDisabled = item in disabledItems
                DropdownMenuItem(
                    text = { Text(itemName, color = if (isDisabled) Color.Gray else Color.Unspecified) },
                    onClick = {
                        if (!isDisabled) {
                            onItemSelected(item)
                        }
                    },
                    enabled = !isDisabled
                )
            }
        }
    }
}
