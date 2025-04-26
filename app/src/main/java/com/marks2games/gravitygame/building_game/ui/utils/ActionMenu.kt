package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.core.ui.utils.SwipeUtil

@Composable
fun ActionMenu(
    modifier: Modifier,
    empire: Empire,
    empireUiState: EmpireUiState,
    empireModel: EmpireViewModel
) {
    Box(
        modifier = modifier.clickable {
            if(empireUiState.isActionsShown){
                empireModel.updateActionsShown(false)
            } else {
                empireModel.updateActionsShown(true)
            }
        }
    ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu icon",
                tint = Color.White,
                modifier = modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )

        Card(
            modifier = modifier
                .align(Alignment.TopEnd),
            shape = CircleShape
        ) {
            Text(
                modifier = modifier.padding(4.dp),
                text = "${empire.actions.size}"
            )
        }
    }
}

@Composable
fun ActionList(
    modifier: Modifier,
    actions: List<Action>,
    empire: Empire,
    empireModel: EmpireViewModel
){
    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)
    ){
        Column (
            modifier = modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ){
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ){
                Text(
                    text = "Delete all actions",
                    modifier = modifier.clickable { empireModel.deleteAllActions() }
                )
            }
            HorizontalDivider(
                modifier = modifier.padding(bottom = 4.dp)
            )
            LazyColumn {
                items (
                    count = actions.size,
                    key = {actions[it].id})
                { ActionRow(
                        modifier = modifier,
                        action = actions[it],
                        empire = empire,
                        empireModel = empireModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionRow(
    modifier: Modifier,
    action: Action,
    empire: Empire,
    empireModel: EmpireViewModel
){
    SwipeUtil(
        delete = {empireModel.deleteAction(action)},
        edit = { },
        enableEdit = false
    ) {
        Column {
            Row {
                Text("${action.type.name} on planet ${empire.planets.find { it.id == action.planetId }?.name ?: ""}")
            }
            HorizontalDivider(
                modifier = modifier.padding(bottom = 4.dp),
                thickness = 1.dp
            )
        }

    }
}