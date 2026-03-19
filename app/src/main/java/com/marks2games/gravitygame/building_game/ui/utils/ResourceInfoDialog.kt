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
import com.marks2games.gravitygame.building_game.data.model.Resource

@Composable
fun ResourceInfoDialog (
    resource: Resource,
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
                        when (resource) {
                            Resource.METAL -> R.drawable.metal_icon
                            Resource.BIOMASS -> R.drawable.biomass_icon
                            Resource.RESEARCH -> R.drawable.research_icon
                            Resource.TRADE_POWER -> R.drawable.tradepower_icon
                            Resource.ARMY -> R.drawable.warship_material_icon
                            Resource.CREDITS -> R.drawable.money_icon
                            Resource.EXPEDITIONS -> R.drawable.expedition_icon
                            Resource.ORGANIC_SEDIMENTS -> R.drawable.organic_sediments_icon
                            Resource.INFRASTRUCTURE -> R.drawable.infrastructure_icon
                            Resource.ROCKET_MATERIALS -> R.drawable.rocket_material_icon
                            Resource.PROGRESS -> R.drawable.progress_icon
                            Resource.DEVELOPMENT -> R.drawable.development_icon
                            Resource.INFLUENCE -> R.drawable.influence_icon
                        }
                    ),
                    contentDescription = "Resource icon",
                    modifier = modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = when (resource) {
                        Resource.RESEARCH -> stringResource(Resource.RESEARCH.nameResIdNominative)
                        Resource.TRADE_POWER -> stringResource(Resource.TRADE_POWER.nameResIdNominative)
                        Resource.ARMY -> stringResource(Resource.ARMY.nameResIdNominative)
                        Resource.CREDITS -> stringResource(Resource.CREDITS.nameResIdNominative)
                        Resource.EXPEDITIONS -> stringResource(Resource.EXPEDITIONS.nameResIdNominative)
                        Resource.BIOMASS -> stringResource(Resource.BIOMASS.nameResIdNominative)
                        Resource.METAL -> stringResource(Resource.METAL.nameResIdNominative)
                        Resource.ORGANIC_SEDIMENTS -> stringResource(Resource.ORGANIC_SEDIMENTS.nameResIdNominative)
                        Resource.INFRASTRUCTURE -> stringResource(Resource.INFRASTRUCTURE.nameResIdNominative)
                        Resource.ROCKET_MATERIALS -> stringResource(Resource.ROCKET_MATERIALS.nameResIdNominative)
                        Resource.PROGRESS -> stringResource(Resource.PROGRESS.nameResIdNominative)
                        Resource.DEVELOPMENT -> stringResource(Resource.DEVELOPMENT.nameResIdNominative)
                        Resource.INFLUENCE -> stringResource(Resource.INFLUENCE.nameResIdNominative)
                    }
                )
            },
            text = {
                Text(
                    text = when (resource) {
                        Resource.RESEARCH -> stringResource(Resource.RESEARCH.descriptionId)
                        Resource.TRADE_POWER -> stringResource(Resource.TRADE_POWER.descriptionId)
                        Resource.ARMY -> stringResource(Resource.ARMY.descriptionId)
                        Resource.CREDITS -> stringResource(Resource.CREDITS.descriptionId)
                        Resource.EXPEDITIONS -> stringResource(Resource.EXPEDITIONS.descriptionId)
                        Resource.BIOMASS -> stringResource(Resource.BIOMASS.descriptionId)
                        Resource.METAL -> stringResource(Resource.METAL.descriptionId)
                        Resource.ORGANIC_SEDIMENTS -> stringResource(Resource.ORGANIC_SEDIMENTS.descriptionId)
                        Resource.INFRASTRUCTURE -> stringResource(Resource.INFRASTRUCTURE.descriptionId)
                        Resource.ROCKET_MATERIALS -> stringResource(Resource.ROCKET_MATERIALS.descriptionId)
                        Resource.PROGRESS -> stringResource(Resource.PROGRESS.descriptionId)
                        Resource.DEVELOPMENT -> stringResource(Resource.DEVELOPMENT.descriptionId)
                        Resource.INFLUENCE -> stringResource(Resource.INFLUENCE.descriptionId)
                    },
                    textAlign = TextAlign.Justify,
                    modifier = modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
}