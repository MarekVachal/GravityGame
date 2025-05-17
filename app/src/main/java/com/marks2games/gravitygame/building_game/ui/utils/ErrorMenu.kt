package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.core.domain.error.NewTurnError
import com.marks2games.gravitygame.core.domain.error.displayError

@Composable
fun ErrorMenu(
    modifier: Modifier,
    empireUiState: EmpireUiState,
    empireModel: EmpireViewModel
) {
    Box(
        modifier = modifier.clickable {
            if (empireUiState.isErrorsShown) {
                empireModel.updateErrorsShown(false)
            } else {
                empireModel.updateErrorsShown(true)
                empireModel.updateActionsShown(false)
                empireModel.updateTransportMenuShown(false)
            }
        }
    ) {

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Menu icon",
            tint = if(empireUiState.errors.isEmpty()) Color.White else Color.Red,
            modifier = modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )


        Card(
            modifier = modifier
                .align(Alignment.TopEnd),
            shape = CircleShape
        ) {
            Text(
                modifier = modifier.padding(4.dp),
                text = "${empireUiState.errors.size}"
            )
        }
    }
}

@Composable
fun ErrorList(
    modifier: Modifier,
    errors: List<NewTurnError>,
    empire: Empire,
    empireModel: EmpireViewModel
) {
    Card (
        modifier = modifier.padding(8.dp)
    ){
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row (
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                IconButton(
                    onClick = { empireModel.updateErrorsShown(false) }
                ){
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Close icon",
                        tint = Color.Black
                    )
                }
            }
            LazyColumn {
                items(errors.size) {
                    ErrorRow(error = errors[it], empire = empire)
                }
            }
        }
    }
}

@Composable
private fun ErrorRow(modifier: Modifier = Modifier, error: NewTurnError, empire: Empire) {
    Column {
        Text(
            text = error.displayError(empire),
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Visible
        )
        HorizontalDivider(
            modifier = modifier.padding(bottom = 4.dp),
            thickness = 1.dp
        )
    }
}