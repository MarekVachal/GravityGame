package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.util.ActionDescriptionData
import com.marks2games.gravitygame.core.ui.utils.SwipeUtil

@Composable
fun ActionMenu(
    modifier: Modifier = Modifier,
    actionsCount: Int,
    onActionMenuClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onActionMenuClick() }
    ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu icon",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd),
            shape = CircleShape
        ) {
            Text(
                modifier = modifier.padding(4.dp),
                text = "$actionsCount"
            )
        }
    }
}

@Composable
fun ActionListPopup(
    modifier: Modifier = Modifier,
    actions: List<Action>,
    deleteAllActions: () -> Unit,
    getActionDescription: (Action) -> ActionDescriptionData,
    deleteAction: (Action) -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true
        )
    ) {
        ActionList(
            modifier = modifier,
            actions = actions,
            deleteAllActions = deleteAllActions,
            getActionDescription = getActionDescription,
            deleteAction = deleteAction
        )
    }
}

@Composable
fun ActionList(
    modifier: Modifier,
    actions: List<Action>,
    deleteAllActions: () -> Unit,
    getActionDescription: (Action) -> ActionDescriptionData,
    deleteAction: (Action) -> Unit
){
    Card(
        modifier = modifier
            .padding(8.dp)
    ){
        Column (
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ){
                Text(
                    text = stringResource(R.string.deleteAllActions),
                    modifier = Modifier.clickable { deleteAllActions() }
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyColumn {
                items (
                    count = actions.size,
                    key = {actions[it].id})
                {
                    ActionRow(
                        modifier = modifier,
                        action = actions[it],
                        getActionDescription = getActionDescription,
                        deleteAction = deleteAction
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
    getActionDescription: (Action) -> ActionDescriptionData,
    deleteAction: (Action) -> Unit
){
    val descriptionData = remember { getActionDescription(action) }

    val description = when (descriptionData) {
        is ActionDescriptionData.GenericDescription -> {
            val actionName = stringResource(descriptionData.actionNameRes)
            stringResource(R.string.action_description_generic, actionName, descriptionData.planetName)
        }

        is ActionDescriptionData.DistrictDescription -> {
            val actionName = stringResource(descriptionData.actionNameRes)
            val districtName = stringResource(descriptionData.districtNameRes)
            stringResource(R.string.action_description_district, actionName, districtName, descriptionData.planetName)
        }
    }

    SwipeUtil(
        delete = { deleteAction(action) },
        edit = { },
        enableEdit = false
    ) {
        Column {
            Row {
                Text(description)
            }
            HorizontalDivider(
                modifier = modifier.padding(bottom = 4.dp),
                thickness = 1.dp
            )
        }

    }
}