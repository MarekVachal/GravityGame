package com.example.gravitygame.ui.screens.infoDialogsScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
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
import com.example.gravitygame.R
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.models.mapOfShips

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
                    painter = painterResource(id = R.drawable.ship_icon),
                    contentDescription = "Image of the ship",
                    modifier = modifier.size(40.dp)
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
                    textAlign = TextAlign.Justify
                )
            },
            modifier = modifier
        )
    }
}