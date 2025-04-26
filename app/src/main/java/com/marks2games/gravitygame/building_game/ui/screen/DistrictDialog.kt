package com.marks2games.gravitygame.building_game.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.core.ui.utils.NumberInputField
import kotlin.reflect.KClass

@Composable
fun DistrictDialog(
    modifier: Modifier = Modifier,
    toShow: Boolean,
    planet: Planet,
    planets: List<Planet>,
    district: District?,
    empireModel: EmpireViewModel,
    empireUiState: EmpireUiState
) {
    val context = LocalContext.current
    if (toShow) {
        Dialog(
            onDismissRequest = { empireModel.updateDistrictDialogShown(false, null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row{
                        Text(district?.type?.name ?: "Unknown type")
                    }
                    if (district?.type == DistrictEnum.PROSPECTORS || district?.type == DistrictEnum.INDUSTRIAL || district?.type == DistrictEnum.URBAN_CENTER) {
                        Row(
                            modifier = modifier,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    empireModel.changeDistrictModeAction(
                                        district = district.type,
                                        districtId = district.districtId,
                                        mode = empireUiState.modeIsChecked,
                                        planetId = planet.id,
                                        context = context
                                    )

                                }
                            ) {
                                Text(text = "Change mode")
                            }
                            ModeSelector(
                                districtType = district.type,
                                selectedMode = empireUiState.modeIsChecked,
                                onCheckedChange = { checked ->
                                    empireModel.updateModeIsChecked(checked)
                                }
                            )
                        }
                    }
                    when (district) {
                        is District.Industrial -> {
                            if (district.mode == IndustrialMode.INFRASTRUCTURE) {
                                SpinnerProductionRow(
                                    actionFunction = {
                                        empireModel.addInfrastructureProductionAction(
                                            context = context,
                                            planet.id,
                                            empireUiState.infrastructureProductionSet
                                        )
                                    },
                                    enumClass = InfrastructureSetting::class,
                                    onItemSelected = { selected ->
                                        empireModel.updateInfrastructureSetting(
                                            selected
                                        )
                                    },
                                    label = empireUiState.infrastructureProductionSet.name
                                )
                            } else {
                                SpinnerProductionRow(
                                    actionFunction = {
                                        empireModel.addRocketMaterialsProductionAction(
                                            context = context,
                                            planet.id, empireUiState.rocketMaterialsProductionSet
                                        )
                                    },
                                    enumClass = RocketMaterialsSetting::class,
                                    onItemSelected = { selected ->
                                        empireModel.updateRocketMaterialsSetting(
                                            selected
                                        )
                                    },
                                    label = empireUiState.rocketMaterialsProductionSet.name
                                )
                            }
                        }

                        is District.Capitol -> {
                            ProductionSetRow(
                                actionFunction = {
                                    empireModel.addProgressProductionAction(
                                        context = context,
                                        planet.id, empireUiState.progressProductionSet
                                    )
                                },
                                value = empireUiState.progressProductionSet.toString(),
                                onValueChange = { newValue ->
                                    Log.d(
                                        "DistrictDialog",
                                        "NumberInputField changed to: $newValue"
                                    )
                                    empireModel.updateIntProductionState(
                                        Resource.PROGRESS,
                                        newValue.toIntOrNull() ?: 0
                                    )
                                },
                                icon = R.drawable.progress_icon,
                                resourceName = R.string.progress,
                                maxValue = 10
                            )
                        }

                        is ExpeditionPlatform -> {
                            ProductionSetRow(
                                actionFunction = {
                                    empireModel.addArmyProductionAction(
                                        context = context,
                                        planet.id, empireUiState.armyProductionSet
                                    )
                                },
                                value = empireUiState.armyProductionSet.toString(),
                                onValueChange = { newValue ->
                                    empireModel.updateIntProductionState(
                                        Resource.ARMY,
                                        newValue.toIntOrNull() ?: 0
                                    )
                                },
                                icon = R.drawable.army_icon,
                                resourceName = R.string.army,
                                maxValue = 10
                            )
                            ProductionSetRow(
                                actionFunction = {
                                    empireModel.addExpeditionProductionAction(
                                        context = context,
                                        planet.id, empireUiState.expeditionsProductionSet
                                    )
                                },
                                value = empireUiState.expeditionsProductionSet.toString(),
                                onValueChange = { newValue ->
                                    empireModel.updateIntProductionState(
                                        Resource.EXPEDITIONS,
                                        newValue.toIntOrNull() ?: 0
                                    )
                                },
                                icon = R.drawable.biomass_icon,
                                resourceName = R.string.expeditions,
                                maxValue = 10
                            )
                            Row {
                                Button(
                                    onClick = {
                                        empireModel.openTransportDialog(
                                            planet = planet
                                        )
                                    },
                                    enabled = planets.size > 1
                                ) { Text(stringResource(R.string.transport)) }
                            }
                        }

                        is District.UrbanCenter -> {
                            if (district.mode == UrbanCenterMode.RESEARCH) {
                                ProductionSetRow(
                                    actionFunction = {
                                        empireModel.addResearchProductionAction(
                                            context = context,
                                            planet.id, empireUiState.researchProductionSet
                                        )
                                    },
                                    value = empireUiState.researchProductionSet.toString(),
                                    onValueChange = { newValue ->
                                        empireModel.updateIntProductionState(
                                            Resource.RESEARCH,
                                            newValue.toIntOrNull() ?: 0
                                        )
                                    },
                                    icon = R.drawable.influence_icon,
                                    resourceName = R.string.research,
                                    maxValue = 10
                                )
                            }
                        }

                        is District.Empty -> {
                            SpinnerProductionRow(
                                actionFunction = {
                                    empireModel.buildDistrictAction(
                                        context = context,
                                        planet.id,
                                        empireUiState.districtToBuild,
                                        district.districtId
                                    )
                                },
                                textOnButton = stringResource(R.string.buildDistrict),
                                enumClass = DistrictEnum::class,
                                onItemSelected = { selected ->
                                    empireModel.updateDistrictToBuild(
                                        selected
                                    )
                                },
                                excludedItems = if(planet.districts.any{it is ExpeditionPlatform}){
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
                                },
                                label = empireUiState.districtToBuild.name
                            )
                        }
                        is District.InConstruction -> Text("Infrastructure in construction: ${district.infra}/$DISTRICT_BUILD_COST")
                        else -> println("Do nothing")
                    }
                    if(district !is District.Capitol && district !is District.Empty){
                        Button(
                            onClick = {
                                empireModel.destroyDistrictAction(
                                    context = context,
                                    planetId = planet.id,
                                    districtId = district?.districtId ?: 0
                                )
                            }
                        ) { Text(stringResource(R.string.destroyDistrict)) }
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
    icon: Int,
    resourceName: Int,
    maxValue: Int
) {
    Log.d("ProductionSetRow", "Recomposed with value: $value")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                Log.d("ProductionSetRow", "Button clicked")
                actionFunction()
            }
        ) { Text(stringResource(R.string.setProduction)) }

        NumberInputField(
            value = value,
            onValueChange = onValueChange,
            label = {
                ProductionLabelRow(
                    icon = icon,
                    resourceName = resourceName
                )
            },
            maxValue = maxValue
        )
    }
}

@Composable
private fun ProductionLabelRow(
    modifier: Modifier = Modifier,
    icon: Int,
    resourceName: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "Resource icon",
            modifier = modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(resourceName))
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

        DropdownSelector(
            enumClass = enumClass,
            onItemSelected = onItemSelected,
            excludedItems = excludedItems,
            label = label
        )
    }
}

@Composable
private fun ModeSelector(
    districtType: DistrictEnum,
    selectedMode: Enum<*>?,
    onCheckedChange: (Enum<*>) -> Unit
) {
    val modes = when (districtType) {
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

    Column {
        modes.forEach { mode ->
            val label = when (mode) {
                ProspectorsMode.METAL, IndustrialMode.METAL -> stringResource(R.string.metal)
                ProspectorsMode.ORGANIC_SEDIMENTS -> stringResource(R.string.organic_sediments)
                IndustrialMode.INFRASTRUCTURE -> stringResource(R.string.infrastructure)
                IndustrialMode.ROCKET_MATERIALS -> stringResource(R.string.rocket_materials)
                UrbanCenterMode.INFLUENCE -> stringResource(R.string.influence)
                UrbanCenterMode.RESEARCH -> stringResource(R.string.research)
                else -> return
            }

            OptionRow(
                text = label,
                selected = selectedMode == mode,
                onClick = { onCheckedChange(mode) }
            )
        }
    }
}

@Composable
private fun OptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick

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
    label: String = ""
) {
    val items = remember { enumClass.java.enumConstants?.filter { it !in excludedItems } }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
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
            onDismissRequest = { expanded = false }
        ) {
            items?.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
