package com.marks2games.gravitygame.battle_game.ui.utils

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
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.core.data.model.mapOfShips

@Composable
fun ShipInfoDialog(
    shipType: ShipType,
    toShow: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
) {
    if (toShow) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = confirmButton) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Check icon"
                    )
                }
            },
            icon = {
                Image(
                    painter = painterResource(
                        when(shipType){
                            ShipType.CRUISER -> R.drawable.cruiser
                            ShipType.DESTROYER -> R.drawable.destroyer
                            ShipType.GHOST -> R.drawable.ghost
                            ShipType.WARPER -> R.drawable.warper
                        }
                    ),
                    contentDescription = "Image of the ship",
                    modifier = modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = mapOfShips[shipType]?.let { stringResource(id = it.nameId) } ?: "Unknown"
                )
            },
            text = {
                Text(
                    text = mapOfShips[shipType]?.let { stringResource(id = it.descriptionId) }
                        ?: "Unknown",
                    textAlign = TextAlign.Justify,
                    modifier = modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
}