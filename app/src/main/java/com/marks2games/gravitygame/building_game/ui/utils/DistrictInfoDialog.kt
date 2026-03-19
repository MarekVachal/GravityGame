package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.District

@Composable
fun DistrictInfoDialog (
    district: District?,
    toShow: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    if (toShow){
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onConfirm) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Check icon"
                    )
                }
            },
            icon = {
                Image(
                    painter = painterResource(
                        id = when (district) {
                            is District.Capitol -> R.drawable.capitol_icon
                            is District.Empty -> R.drawable.wilderness_icon
                            is District.ExpeditionPlatform -> R.drawable.expedition_platform_icon
                            is District.InConstruction -> R.drawable.in_construction_icon
                            is District.Industrial -> R.drawable.industrials_icon
                            is District.Prospectors -> R.drawable.prospectors_icon
                            is District.Unnocupated -> R.drawable.empty_icon
                            is District.UrbanCenter -> R.drawable.urban_icon
                            else -> R.drawable.empty_icon
                        }
                    ),
                    contentDescription = "Resource icon",
                    modifier = modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = when (district) {
                        is District.Capitol -> stringResource(R.string.capitolDistrictName)
                        is District.Empty -> stringResource(R.string.emptyDistrictName)
                        is District.ExpeditionPlatform -> stringResource(R.string.expeditionPlatformDistrictName)
                        is District.InConstruction -> stringResource(R.string.inConstructionDistrictName)
                        is District.Industrial -> stringResource(R.string.industrialDistrictName)
                        is District.Prospectors -> stringResource(R.string.prospectorsDistrictName)
                        is District.Unnocupated -> stringResource(R.string.unnocupatedNominative)
                        is District.UrbanCenter -> stringResource(R.string.urbanCenterDistrictName)
                        else -> stringResource(R.string.unknown)
                    }
                )
            },
            text = {
                Text(
                    text = when (district) {
                        is District.Capitol -> stringResource(R.string.capitolDistrictDescription)
                        is District.Empty -> stringResource(R.string.emptyDistrictDescription)
                        is District.ExpeditionPlatform -> stringResource(R.string.expeditionPlatformDistrictDescription)
                        is District.InConstruction -> stringResource(R.string.inConstructionDistrictDescription)
                        is District.Industrial -> stringResource(R.string.IndustrialDistrictDescription)
                        is District.Prospectors -> stringResource(R.string.prospectorsDistrictDescription)
                        is District.Unnocupated -> stringResource(R.string.unnocupatedDistrictDescription)
                        is District.UrbanCenter -> stringResource(R.string.urbanCenterDistrictDescription)
                        else -> stringResource(R.string.unknown)
                    },
                    textAlign = TextAlign.Justify,
                    modifier = modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
}