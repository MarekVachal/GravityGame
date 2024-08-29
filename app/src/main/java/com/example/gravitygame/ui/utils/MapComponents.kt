package com.example.gravitygame.ui.utils

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    locationList: List<Location>,
    location: Int,
    boxSize: Dp,
    flagSize: Dp,
    planetSize: Dp,
    planetImage: Int
) {
    var lastTouchPosition: Offset? by remember { mutableStateOf(null) }
    Box(
        modifier = modifier
            .size(boxSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        Log.d("MapBox", "Drag started at offset: $offset")
                        battleModel.setStartLocationMovementOrder(location)
                        lastTouchPosition = offset
                    },
                    onDragEnd = {
                        lastTouchPosition?.let { touchPosition ->
                            val touchPositionInWindow = Offset(
                                x = touchPosition.x + battleModel.movementUiState.value.mapBoxCoordinates[location]?.left!!,
                                y = touchPosition.y + battleModel.movementUiState.value.mapBoxCoordinates[location]?.top!!
                            )
                            val endLocation = determineLocationFromOffset(
                                touchPositionInWindow,
                                battleModel.movementUiState.value.mapBoxCoordinates
                            )
                            endLocation?.let { battleModel.setEndLocationMovementOrder(it) }
                                ?: battleModel.cleanAfterUnsuccessfulMovement()
                        }
                    },
                    onDrag = { change, _ ->
                        lastTouchPosition = change.position
                        change.consume()
                    }
                )
            }
            .onGloballyPositioned { coordinates ->
                battleModel.createMapBoxPositions(location, coordinates.boundsInWindow())
            }
            .clickable {
                battleModel.showLocationInfoDialog(true)
                battleModel.setLocationForInfo(location = location)
            }
            .border(
                BorderStroke(
                    2.dp,
                    if (locationList[location].accessible) {
                        Color.Green
                    } else if (location == (battleModel.movementUiState.value.startPosition
                            ?: 100)
                    ) {
                        Color.Red
                    } else {
                        Color.Unspecified
                    }
                ), shape = CircleShape
            )
    ) {
        PlanetImage(
            image = planetImage,
            myContentDescription = "location$location",
            size = planetSize,
            modifier = Modifier.align(Alignment.Center)
        )
        AcceptableLost(
            location = location,
            locationList = locationList,
            modifier = Modifier
                .clip(shape = CircleShape)
                .align(Alignment.BottomCenter)
        )
        if (locationList[location].owner.value == Players.PLAYER1) {
            PlanetFlag(
                image = R.drawable.flag_player1,
                myContentDescription = "Location$location flag",
                size = flagSize,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        if (locationList[location].owner.value == Players.PLAYER2) {
            PlanetFlag(
                image = R.drawable.flag_player2,
                myContentDescription = "Location$location flag",
                size = flagSize,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        //Enemy Ships
        ArmyInfo(
            modifier = Modifier.align(Alignment.CenterEnd) /*if (battleModel.player1) Alignment.CenterEnd else Alignment.CenterStart*/,
            numberCruisers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.CRUISER,
                isForEnemy = true
            ),
            numberDestroyers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.DESTROYER,
                isForEnemy = true
            ),
            numberGhosts = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.GHOST,
                isForEnemy = true
            ),
            numberWarpers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.WARPER,
                isForEnemy = true
            ),
            locationList = locationList,
            location = location,
            isForEnemy = true
        )
        //My Ships
        ArmyInfo(
            modifier = Modifier.align(Alignment.CenterStart),//if (battleModel.player1) Alignment.CenterStart else Alignment.CenterEnd
            numberCruisers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.CRUISER,
                isForEnemy = false
            ),
            numberDestroyers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.DESTROYER,
                isForEnemy = false
            ),
            numberGhosts = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.GHOST,
                isForEnemy = false
            ),
            numberWarpers = battleModel.getNumberOfShip(
                locationList = locationList,
                location = location,
                shipType = ShipType.WARPER,
                isForEnemy = false
            ),
            locationList = locationList,
            location = location,
            isForEnemy = false
        )
    }
}

@Composable
fun MovementRecordOnLine(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    location1: Int,
    location2: Int,
    record: List<Map<Ship, Int>>
){

    val cruisers = battleModel.getNumberShipsForRecord(shipType = ShipType.CRUISER, location1 = location1, location2 = location2)
    val destroyers = battleModel.getNumberShipsForRecord(shipType = ShipType.DESTROYER, location1 = location1, location2 = location2)
    val ghost = battleModel.getNumberShipsForRecord(shipType = ShipType.GHOST, location1 = location1, location2 = location2)
    val warper = battleModel.getNumberShipsForRecord(shipType = ShipType.WARPER, location1 = location1, location2 = location2)
    if (record.any { map -> map.any { (it.key.startingPosition == location1 && it.key.currentPosition == location2) || (it.key.startingPosition == location2 && it.key.currentPosition == location1) } }){
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ){
            Column (
                modifier = modifier.padding(4.dp)
            ){
                if(cruisers != 0) ArmyInfoRow(shipNumber = cruisers)
                if(destroyers != 0) ArmyInfoRow(shipNumber = destroyers)
                if(ghost != 0) ArmyInfoRow(shipNumber = ghost)
                if(warper != 0) ArmyInfoRow(shipNumber = warper)
            }
        }
    }
}


@Composable
private fun AcceptableLost(
    location: Int,
    locationList: List<Location>,
    modifier: Modifier = Modifier
) {
    if (locationList[location].myShipList.isNotEmpty()) {
        Card(
            modifier = modifier
        ) {
            Text(
                text = locationList[location].myAcceptableLost.intValue.toString(),
                modifier = modifier.padding(2.dp)
            )
        }
    }

}


@Composable
private fun PlanetFlag(
    image: Int,
    myContentDescription: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painterResource(image),
        contentDescription = myContentDescription,
        modifier = modifier
            .size(size)
    )
}

@Composable
private fun PlanetImage(
    image: Int,
    myContentDescription: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(image),
        contentDescription = myContentDescription,
        modifier = modifier
            .size(size)
    )
}

@Composable
private fun ArmyInfoRow(
    modifier: Modifier = Modifier,
    shipNumber: Int
) {
    Row {
        Image(
            painter = painterResource(R.drawable.ship_icon),
            contentDescription = "Cruiser icon",
            modifier = modifier
                .size(18.dp)
        )
        Text(
            text = shipNumber.toString()
        )
    }
}

@Composable
private fun ArmyInfo(
    modifier: Modifier = Modifier,
    numberCruisers: Int,
    numberDestroyers: Int,
    numberGhosts: Int,
    numberWarpers: Int,
    locationList: List<Location>,
    location: Int,
    isForEnemy: Boolean
) {

    if (!isForEnemy && locationList[location].myShipList.isNotEmpty()) {
        Card(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {

                if (locationList[location].myShipList.any { it.type == ShipType.CRUISER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberCruisers
                )
                if (locationList[location].myShipList.any { it.type == ShipType.DESTROYER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberDestroyers
                )
                if (locationList[location].myShipList.any { it.type == ShipType.GHOST }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberGhosts
                )
                if (locationList[location].myShipList.any { it.type == ShipType.WARPER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberWarpers
                )
            }
        }

    } else if (isForEnemy && locationList[location].enemyShipList.isNotEmpty()) {
        Card(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                if (locationList[location].enemyShipList.any { it.type == ShipType.CRUISER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberCruisers
                )
                if (locationList[location].enemyShipList.any { it.type == ShipType.DESTROYER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberDestroyers
                )
                if (locationList[location].enemyShipList.any { it.type == ShipType.GHOST }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberGhosts
                )
                if (locationList[location].enemyShipList.any { it.type == ShipType.WARPER }) ArmyInfoRow(
                    modifier = modifier,
                    shipNumber = numberWarpers
                )
            }
        }
    }


}