package com.example.gravitygame.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.models.Location
import com.example.gravitygame.models.Players
import com.example.gravitygame.models.Ship
import com.example.gravitygame.models.ShipType
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    locationList: List<Location>,
    location: Int,
    boxSize: Dp,
    flagSize: Dp,
    planetSize: Dp,
    planetImage: Int,
    explosionSize: Dp
) {
    var lastTouchPosition: Offset? by remember { mutableStateOf(null) }
    val draggingIconVisible = remember { mutableStateOf(false) }
    val iconPositionX = remember { Animatable(0f) }
    val iconPositionY = remember { Animatable(0f) }
    val iconSize = boxSize.value/2
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .size(boxSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        draggingIconVisible.value = true
                        coroutineScope.launch {
                            iconPositionX.snapTo(offset.x - iconSize)
                            iconPositionY.snapTo(offset.y - iconSize)
                        }
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
                            if(endLocation != null){
                                battleModel.setEndLocationMovementOrder(endLocation)
                                draggingIconVisible.value = false
                            } else {
                                draggingIconVisible.value = false
                                coroutineScope.launch {
                                    iconPositionX.snapTo(0f)
                                    iconPositionY.snapTo(0f)
                                }
                                battleModel.cleanAfterUnsuccessfulMovement()
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        lastTouchPosition = change.position
                        coroutineScope.launch {
                            iconPositionX.animateTo(
                                targetValue = change.position.x - iconSize,
                                animationSpec = tween(100)
                            )
                            iconPositionY.animateTo(
                                targetValue = change.position.y - iconSize,
                                animationSpec = tween(100)
                            )
                        }
                        change.consume()
                    }
                )
            }
            .onGloballyPositioned { coordinates ->
                battleModel.createMapBoxPositions(location, coordinates.boundsInWindow())
            }
            .combinedClickable(
                onClick = {
                    battleModel.showLocationInfoDialog(true)
                    battleModel.setLocationForInfo(location = location)
                },
                onLongClick = {
                    battleModel.checkToShowBattleInfo(location = location)
                }

            )
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

        BattleIconShow(
            location = location,
            locationList = locationList,
            size = explosionSize,
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
                location = location,
                shipType = ShipType.CRUISER,
                isForEnemy = true
            ),
            numberDestroyers = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.DESTROYER,
                isForEnemy = true
            ),
            numberGhosts = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.GHOST,
                isForEnemy = true
            ),
            numberWarpers = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.WARPER,
                isForEnemy = true
            ),
            locationList = locationList,
            location = location,
            isForEnemy = true,
            battleModel = battleModel
        )
        //My Ships
        ArmyInfo(
            modifier = Modifier.align(Alignment.CenterStart),//if (battleModel.player1) Alignment.CenterStart else Alignment.CenterEnd
            numberCruisers = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.CRUISER,
                isForEnemy = false
            ),
            numberDestroyers = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.DESTROYER,
                isForEnemy = false
            ),
            numberGhosts = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.GHOST,
                isForEnemy = false
            ),
            numberWarpers = battleModel.getNumberOfShip(
                location = location,
                shipType = ShipType.WARPER,
                isForEnemy = false
            ),
            locationList = locationList,
            location = location,
            isForEnemy = false,
            battleModel = battleModel
        )

        if (draggingIconVisible.value) {
            Image(
                painter = painterResource(id = R.drawable.fleet_icon),
                contentDescription = null,
                modifier = Modifier
                    .size((boxSize.value/1.5).dp)
                    .offset {
                        IntOffset(iconPositionX.value.toInt(), iconPositionY.value.toInt())
                    }
            )
        }
    }
}

@Composable
fun MovementRecordOnLine(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    location1: Int,
    location2: Int,
    myRecord: List<Map<Ship, Int>>,
    enemyRecord: List<Ship>
){
    val enemyCruisers = battleModel.getNumberShipsForRecord(shipType = ShipType.CRUISER, location1 = location1, location2 = location2, isMyRecord = false)
    val enemyDestroyers = battleModel.getNumberShipsForRecord(shipType = ShipType.DESTROYER, location1 = location1, location2 = location2, isMyRecord = false)
    val enemyGhost = battleModel.getNumberShipsForRecord(shipType = ShipType.GHOST, location1 = location1, location2 = location2, isMyRecord = false)
    val enemyWarper = battleModel.getNumberShipsForRecord(shipType = ShipType.WARPER, location1 = location1, location2 = location2, isMyRecord = false)

    val cruisers = battleModel.getNumberShipsForRecord(shipType = ShipType.CRUISER, location1 = location1, location2 = location2, isMyRecord = true)
    val destroyers = battleModel.getNumberShipsForRecord(shipType = ShipType.DESTROYER, location1 = location1, location2 = location2, isMyRecord = true)
    val ghost = battleModel.getNumberShipsForRecord(shipType = ShipType.GHOST, location1 = location1, location2 = location2, isMyRecord = true)
    val warper = battleModel.getNumberShipsForRecord(shipType = ShipType.WARPER, location1 = location1, location2 = location2, isMyRecord = true)
    val myRecordBoolean = myRecord.any { map -> map.any { (it.key.startingPosition == location1 && it.key.currentPosition == location2) || (it.key.startingPosition == location2 && it.key.currentPosition == location1) } }
    val enemyRecordBoolean = enemyRecord.any { (it.startingPosition == location1 && it.currentPosition == location2) || (it.startingPosition == location2 && it.currentPosition == location1)}
    if(myRecordBoolean || enemyRecordBoolean){
        Row(
            modifier = modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if (myRecordBoolean){
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
                        if(cruisers != 0) ArmyInfoRow(shipNumber = cruisers, shipType = ShipType.CRUISER, battleModel = battleModel)
                        if(destroyers != 0) ArmyInfoRow(shipNumber = destroyers, shipType = ShipType.DESTROYER, battleModel = battleModel)
                        if(ghost != 0) ArmyInfoRow(shipNumber = ghost, shipType = ShipType.GHOST, battleModel = battleModel)
                        if(warper != 0) ArmyInfoRow(shipNumber = warper, shipType = ShipType.WARPER, battleModel = battleModel)
                    }
                }
            }
            if(myRecordBoolean && enemyRecordBoolean){
                Spacer(modifier = Modifier.size(4.dp))
            }
            if (enemyRecordBoolean){
                Card(
                    modifier = modifier,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ){
                    Column (
                        modifier = modifier.padding(4.dp)
                    ){
                        if(enemyCruisers != 0) ArmyInfoRow(shipNumber = enemyCruisers, shipType = ShipType.CRUISER, battleModel = battleModel)
                        if(enemyDestroyers != 0) ArmyInfoRow(shipNumber = enemyDestroyers, shipType = ShipType.DESTROYER, battleModel = battleModel)
                        if(enemyGhost != 0) ArmyInfoRow(shipNumber = enemyGhost, shipType = ShipType.GHOST, battleModel = battleModel)
                        if(enemyWarper != 0) ArmyInfoRow(shipNumber = enemyWarper, shipType = ShipType.WARPER, battleModel = battleModel)
                    }
                }
            }
        }

    }
}

@Composable
private fun BattleIconShow(
    location: Int,
    locationList: List<Location>,
    size: Dp,
    modifier: Modifier = Modifier
){
    if (locationList[location].wasBattleHere.value){
        Box (
            modifier = modifier
        ){
            Image(
                painter = painterResource(id = R.drawable.explosion),
                contentDescription = "Explosion icon",
                modifier = Modifier.size(size))
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
    shipNumber: Int,
    shipType: ShipType,
    battleModel: BattleViewModel
) {
    Row {
        Image(
            painter = painterResource(
                battleModel.getShipImage(shipType = shipType)
            ),
            contentDescription = "Ship icon",
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
    isForEnemy: Boolean,
    battleModel: BattleViewModel
) {

    val shipList = if (isForEnemy) locationList[location].enemyShipList else locationList[location].myShipList

    if (shipList.isNotEmpty()) {
        Card(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                listOf(
                    ShipType.CRUISER to numberCruisers,
                    ShipType.DESTROYER to numberDestroyers,
                    ShipType.GHOST to numberGhosts,
                    ShipType.WARPER to numberWarpers
                ).forEach { (shipType, shipNumber) ->
                    if (shipList.any { it.type == shipType }) {
                        ArmyInfoRow(
                            modifier = modifier,
                            shipNumber = shipNumber,
                            shipType = shipType,
                            battleModel = battleModel
                        )
                    }
                }
            }
        }
    }
}