package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.ui.viewmodel.EmpireViewModel
import com.marks2games.gravitygame.core.ui.utils.SwipeUtil

@Composable
fun TransportMenu(
    modifier: Modifier = Modifier,
    empire: Empire,
    empireModel: EmpireViewModel,
    empireUiState: EmpireUiState
){
    Box(
        modifier = modifier.clickable{
            if(empireUiState.isTransportMenuShown){
                empireModel.updateTransportMenuShown(false)
            } else {
                empireModel.updateTransportMenuShown(true)
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Transport icon",
            tint = Color.White,
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.Center)
        )
        Card(
            modifier = modifier.align(Alignment.TopEnd),
            shape = CircleShape
        ) {
            Text(
                modifier = modifier.padding(4.dp),
                text = "${empire.transports.size}"
            )
        }
    }
}

@Composable
fun TransportsList(
    modifier: Modifier = Modifier,
    transports: List<Transport>,
    empire: Empire,
    empireModel: EmpireViewModel,
    onTransportClick: (Transport) -> Unit
){
    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)

    ){
        Column (
            modifier = modifier.wrapContentSize()
        ){
            Row(
                horizontalArrangement = Arrangement.End
            ){
                Text(
                    text = "Delete all transports",
                    modifier = modifier.clickable {empireModel.deleteAllTransports()}
                )
            }
            HorizontalDivider(
                modifier = modifier.padding(bottom = 4.dp)
            )
            LazyColumn {
                items(
                    count = transports.size,
                    key = { transports[it].transportId }
                ) {
                    TransportRow(
                        modifier = modifier,
                        transport = transports[it],
                        empire = empire,
                        empireModel = empireModel,
                        onTransportClick = onTransportClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportRow(
    modifier: Modifier = Modifier,
    transport: Transport,
    empire: Empire,
    empireModel: EmpireViewModel,
    onTransportClick: (Transport) -> Unit
){
    SwipeUtil(
        delete = { empireModel.deleteTransport(transport)},
        edit = {},
        enableEdit = false
    ) {
        Row(
            modifier = modifier.clickable{ onTransportClick(transport) }
        ){
            val planet1Name = empire.planets.find { it.id == transport.planet1Id }?.name ?: ""
            val planet2Name = empire.planets.find { it.id == transport.planet2Id }?.name ?: ""
            val icon = Icons.Default.Share
            Text(
                text = "$planet1Name $icon $planet2Name"
            )
            HorizontalDivider(
                modifier = modifier.padding(bottom = 4.dp)
            )
        }
    }
}