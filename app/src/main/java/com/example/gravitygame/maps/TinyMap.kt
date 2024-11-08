package com.example.gravitygame.maps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.models.Location
import com.example.gravitygame.ui.utils.MapBox
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.gravitygame.R
import com.example.gravitygame.models.Ship
import com.example.gravitygame.ui.utils.MovementRecordOnLine

class TinyMap : BattleMap() {
    override val mapName = R.string.tinyMapName
    override val boxSize = 100.dp
    override val planetSize = 54.dp
    override val explosionSize = 24.dp
    override val flagSize = 24.dp
    override val secondsForTurn = 301
    override val shipLimitOnPosition = 6
    override val shipLimitOnMap = 11
    private val location0 = Location(0, listOf(1, 2, 3))
    private val location1 = Location(1, listOf(0, 5, 4))
    private val location2 = Location(2, listOf(0, 5, 6))
    private val location3 = Location(3, listOf(0, 4, 6))
    private val location4 = Location(4, listOf(1, 3, 7))
    private val location5 = Location(5, listOf(1, 2, 7))
    private val location6 = Location(6, listOf(2, 3, 7))
    private val location7 = Location(7, listOf(4, 5, 6))
    override val locationList = listOf(
        location0,
        location1,
        location2,
        location3,
        location4,
        location5,
        location6,
        location7
    )
    override val player1Base = locationList.first().id
    override val player2Base = locationList.last().id

    @Composable
    override fun MapLayout(
        modifier: Modifier,
        battleModel: BattleViewModel,
        record: List<Map<Ship, Int>>,
        enemyRecord: List<Ship>,
        locationList: List<Location>
    ) {

        ConstraintLayout {
            val startGuideline = createGuidelineFromStart(0.1f)
            val endGuideline = createGuidelineFromEnd(0.1f)
            val topGuideline = createGuidelineFromTop(0.25f)
            val bottomGuideline = createGuidelineFromBottom(0.25f)
            val centerLeftVerticalGuideline = createGuidelineFromStart(0.4f)
            val middleLeftVerticalGuideline = createGuidelineFromStart(0.3f)
            val centerHorizontalGuideline = createGuidelineFromTop(0.5f)
            val centerRightVerticalGuideline = createGuidelineFromEnd(0.4f)
            val middleRightVerticalGuideline = createGuidelineFromEnd(0.3f)

            val (field0, field1, field2, field3, field4, field5, field6, field7) = createRefs()
            val (route0, route1, route2, route3, route4, route5, route6, route7, route8, route9, route10, route11) = createRefs()
            val (rec0, rec1, rec2, rec3, rec4, rec5, rec6, rec7, rec8, rec9, rec10, rec11) = createRefs()



            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 0 and 1",
                modifier = modifier
                    .rotate(-40f)
                    .constrainAs(route0) {
                        start.linkTo(field0.end)
                        top.linkTo(field0.top)
                        end.linkTo(field1.start)
                        bottom.linkTo(field1.bottom)
                    }
                    .width(150.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 0,
                location2 = 1,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec0){
                        centerVerticallyTo(route0)
                        centerHorizontallyTo(route0)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 0 and 3",
                modifier = modifier
                    .constrainAs(route1) {
                        start.linkTo(field0.end)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(field3.start)
                        bottom.linkTo(centerHorizontalGuideline)
                    }
                    .width(200.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 0,
                location2 = 3,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec1){
                        centerVerticallyTo(route1)
                        centerHorizontallyTo(route1)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 0 and 2",
                modifier = modifier
                    .rotate(40f)
                    .constrainAs(route2) {
                        start.linkTo(field0.end)
                        top.linkTo(field0.top)
                        end.linkTo(field2.start)
                        bottom.linkTo(field2.bottom)
                    }
                    .width(150.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 0,
                location2 = 2,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec2){
                        centerVerticallyTo(route2)
                        centerHorizontallyTo(route2)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 1 and 4",
                modifier = modifier
                    .constrainAs(route3) {
                        start.linkTo(middleLeftVerticalGuideline)
                        top.linkTo(topGuideline)
                        end.linkTo(middleRightVerticalGuideline)
                        bottom.linkTo(topGuideline)
                    }
                    .width(300.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 1,
                location2 = 4,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec3){
                        centerVerticallyTo(route3)
                        centerHorizontallyTo(route3)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 1 and 5",
                modifier = modifier
                    .rotate(25f)
                    .constrainAs(route4) {
                        start.linkTo(field1.end)
                        top.linkTo(topGuideline)
                        end.linkTo(field5.start)
                        bottom.linkTo(centerHorizontalGuideline)
                    }
                    .width(250.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 1,
                location2 = 5,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec4){
                        centerVerticallyTo(route4)
                        centerHorizontallyTo(route4)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 3 and 4",
                modifier = modifier
                    .rotate(-25f)
                    .constrainAs(route5) {
                        start.linkTo(field3.end)
                        top.linkTo(field4.top)
                        end.linkTo(field4.start)
                        bottom.linkTo(field3.bottom)
                    }
                    .width(250.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 3,
                location2 = 4,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec5){
                        centerVerticallyTo(route5)
                        centerHorizontallyTo(route5)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 3 and 6",
                modifier = modifier
                    .rotate(25f)
                    .constrainAs(route6) {
                        start.linkTo(field3.end)
                        top.linkTo(field3.top)
                        end.linkTo(field6.start)
                        bottom.linkTo(field6.bottom)
                    }
                    .width(250.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 3,
                location2 = 6,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec6){
                        centerVerticallyTo(route6)
                        centerHorizontallyTo(route6)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 2 and 6",
                modifier = modifier
                    .constrainAs(route7) {
                        start.linkTo(field2.end)
                        top.linkTo(bottomGuideline)
                        end.linkTo(field6.start)
                        bottom.linkTo(bottomGuideline)
                    }
                    .width(300.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 2,
                location2 = 6,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec7){
                        centerVerticallyTo(route7)
                        centerHorizontallyTo(route7)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 2 and 5",
                modifier = modifier
                    .rotate(-25f)
                    .constrainAs(route8) {
                        start.linkTo(field2.end)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(field5.start)
                        bottom.linkTo(bottomGuideline)
                    }
                    .width(250.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 2,
                location2 = 5,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec8){
                        centerVerticallyTo(route8)
                        centerHorizontallyTo(route8)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 7 and 4",
                modifier = modifier
                    .rotate(40f)
                    .constrainAs(route9) {
                        start.linkTo(field4.end)
                        top.linkTo(field4.top)
                        end.linkTo(field7.start)
                        bottom.linkTo(field7.bottom)
                    }
                    .width(150.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 7,
                location2 = 4,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec9){
                        centerVerticallyTo(route9)
                        centerHorizontallyTo(route9)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 7 and 5",
                modifier = modifier
                    .constrainAs(route10) {
                        start.linkTo(field5.end)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(field7.start)
                        bottom.linkTo(centerHorizontalGuideline)
                    }
                    .width(200.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 7,
                location2 = 5,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec10){
                        centerVerticallyTo(route10)
                        centerHorizontallyTo(route10)

                    }
            )

            Image(
                painter = painterResource(id = R.drawable.route),
                contentDescription = "Route between location 7 and 6",
                modifier = modifier
                    .rotate(-40f)
                    .constrainAs(route11) {
                        start.linkTo(field6.end)
                        top.linkTo(field7.top)
                        end.linkTo(field7.start)
                        bottom.linkTo(field6.bottom)
                    }
                    .width(150.dp)
            )

            MovementRecordOnLine(
                battleModel = battleModel,
                location1 = 7,
                location2 = 6,
                myRecord = record,
                enemyRecord = enemyRecord,
                modifier = modifier
                    .constrainAs(rec11){
                        centerVerticallyTo(route11)
                        centerHorizontallyTo(route11)

                    }
            )

            MapBox(
                battleModel = battleModel,
                location = 0,
                modifier = Modifier
                    .constrainAs(field0) {
                        start.linkTo(startGuideline)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(startGuideline)
                        bottom.linkTo(centerHorizontalGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.blue_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 1,
                modifier = modifier
                    .constrainAs(field1) {
                        start.linkTo(middleLeftVerticalGuideline)
                        top.linkTo(topGuideline)
                        end.linkTo(middleLeftVerticalGuideline)
                        bottom.linkTo(topGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 2,
                modifier = modifier
                    .constrainAs(field2) {
                        start.linkTo(middleLeftVerticalGuideline)
                        top.linkTo(bottomGuideline)
                        end.linkTo(middleLeftVerticalGuideline)
                        bottom.linkTo(bottomGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 3,
                modifier = modifier
                    .constrainAs(field3) {
                        start.linkTo(centerLeftVerticalGuideline)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(centerLeftVerticalGuideline)
                        bottom.linkTo(centerHorizontalGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 4,
                modifier = modifier
                    .constrainAs(field4) {
                        start.linkTo(middleRightVerticalGuideline)
                        top.linkTo(topGuideline)
                        end.linkTo(middleRightVerticalGuideline)
                        bottom.linkTo(topGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 5,
                modifier = modifier
                    .constrainAs(field5) {
                        start.linkTo(centerRightVerticalGuideline)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(centerRightVerticalGuideline)
                        bottom.linkTo(centerHorizontalGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 6,
                modifier = modifier
                    .constrainAs(field6) {
                        start.linkTo(middleRightVerticalGuideline)
                        top.linkTo(bottomGuideline)
                        end.linkTo(middleRightVerticalGuideline)
                        bottom.linkTo(bottomGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.yellow_planet,
                explosionSize = explosionSize
            )

            MapBox(
                battleModel = battleModel,
                location = 7,
                modifier = modifier
                    .constrainAs(field7) {
                        start.linkTo(endGuideline)
                        top.linkTo(centerHorizontalGuideline)
                        end.linkTo(endGuideline)
                        bottom.linkTo(centerHorizontalGuideline)
                    },
                boxSize = boxSize,
                planetSize = planetSize,
                flagSize = flagSize,
                locationList = locationList,
                planetImage = R.drawable.red_planet,
                explosionSize = explosionSize
            )
        }
    }
}


