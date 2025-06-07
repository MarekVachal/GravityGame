package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteEmpireDialog(
    modifier: Modifier = Modifier,
    showDeleteEmpireDialog: Boolean,
    mainMenuViewModel: MainMenuViewModel,
    updateHasLaunchedEmpireScreen: (Boolean) -> Unit
){
    if(showDeleteEmpireDialog){
        BasicAlertDialog(
            onDismissRequest = { mainMenuViewModel.updateShowDeleteEmpireDialog(false)},
        ) {
            Card{
                Column (
                    modifier = modifier.padding(16.dp)
                ){
                    Text(
                        text = stringResource(R.string.deleteEmpireTitle),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.deleteEmpireText),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {mainMenuViewModel.updateShowDeleteEmpireDialog(false)}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close"
                            )
                        }
                        IconButton(
                            onClick = {mainMenuViewModel.deleteEmpire(updateHasLaunchedEmpireScreen)}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }
}