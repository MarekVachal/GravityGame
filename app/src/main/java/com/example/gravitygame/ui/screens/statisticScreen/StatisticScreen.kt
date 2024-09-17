package com.example.gravitygame.ui.screens.statisticScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.database.BattleResult
import com.example.gravitygame.database.DatabaseViewModel
import com.example.gravitygame.ui.utils.formatDate

@Composable
fun StatisticScreen(
    modifier: Modifier = Modifier,
    databaseModel: DatabaseViewModel,
    onBackButtonClick: () -> Unit
) {
    
    val statisticUiState by databaseModel.statisticUiState.collectAsState()

    if(!statisticUiState.initialize) databaseModel.loadStatistic()

    Image(
        painter = painterResource(id = R.drawable.main_menu_back),
        contentDescription = "Background for main menu",
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize()
    )

    Row(
        modifier = modifier.padding(bottom = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ){
        IconButton(
            onClick = { onBackButtonClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout icon",
                tint = Color.Unspecified)
        }
    }

    Row (
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Card{
            Column {
                LazyColumn (
                    modifier = modifier
                        .padding(12.dp)
                        .fillMaxHeight(0.9f),
                    contentPadding = PaddingValues(8.dp)
                ){
                    items(statisticUiState.listBattleResult.reversed()) { item ->
                        BattleResultItem(battleResult = item, onClick = { databaseModel.onItemClick(battleResult = item) })
                    }
                }
            }
        }

        Card{
            Column (
                modifier = modifier.padding(12.dp)
            ){
                if(statisticUiState.battleResult == null){
                    GeneralStatistics(statisticUiState = statisticUiState)
                } else {
                    statisticUiState.battleResult?.let {
                        DetailStatistics(
                            battleResult = it,
                            onDetailStatisticButtonClick = {databaseModel.nullBattleResult()})}
                }
            }
        }
    }
}

@Composable
private fun BattleResultItem(
    battleResult: BattleResult,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Column (
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .width(IntrinsicSize.Max)
    ){
        Text(text = stringResource(id = R.string.date, formatDate(battleResult.timestamp)))
        Text(text = stringResource(id = R.string.battleResult, battleResult.result))
        HorizontalDivider()
    }
}

@Composable
private fun DetailStatistics(
    modifier: Modifier = Modifier,
    battleResult: BattleResult,
    onDetailStatisticButtonClick: () -> Unit
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.width(IntrinsicSize.Max)
    ){
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(id = R.string.detailOfBattle),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = {onDetailStatisticButtonClick()}
            ){
                Icon(painter = painterResource(id = R.drawable.close), contentDescription = "Close icon", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        HorizontalDivider()
        Text(text = stringResource(id = R.string.battleResult, battleResult.result))
        Text(text = stringResource(id = R.string.turns, battleResult.turn))
        Text(text = stringResource(id = R.string.myShipLost, battleResult.myShipLost))
        Text(text = stringResource(id = R.string.enemyShipDestroyed, battleResult.enemyShipDestroyed))
    }
}

@Composable
private fun GeneralStatistics(
    modifier: Modifier = Modifier,
    statisticUiState: StatisticUiState
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.width(IntrinsicSize.Max)
    ){
        Text(
            text = stringResource(id = R.string.generalStatistics),
            style = MaterialTheme.typography.titleLarge)
        HorizontalDivider()
        Text(text = stringResource(id = R.string.totalBattle, statisticUiState.totalBattle))
        Text(text = stringResource(id = R.string.averageTurns, statisticUiState.averageTurn))
        Text(text = stringResource(id = R.string.countOfWins, statisticUiState.countOfWins))
        Text(text = stringResource(id = R.string.countOfLost, statisticUiState.countOfLost))
        Text(text = stringResource(id = R.string.countOfDraw, statisticUiState.countOfDraw))
        Text(text = stringResource(id = R.string.myShipLost, statisticUiState.totalMyShipLost))
        Text(text = stringResource(id = R.string.enemyShipDestroyed, statisticUiState.totalEnemyShipDestroyed))
    }
}