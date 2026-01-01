package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Technology

@Composable
fun TechnologyInfoDialog(
    technology: Technology?,
    isShown: Boolean,
    closeDialog: () -> Unit
) {
    Dialog(
        onDismissRequest = { closeDialog() }
    ) {
        if (isShown) {
            Card (
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ){
                Column (
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { closeDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                    Text(
                        text = stringResource(id = technology?.nameId ?: R.string.unknownTechnology),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row {
                        Text(text = "Cost: ${technology?.cost}")
                        Spacer(Modifier.size(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.research_icon),
                            contentDescription = "researchIcon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(text = stringResource(technology?.descriptionId ?: R.string.getTechnologyDescriptionError))
                }
            }
        }
    }
}