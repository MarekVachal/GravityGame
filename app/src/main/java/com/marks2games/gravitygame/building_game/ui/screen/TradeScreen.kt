package com.marks2games.gravitygame.building_game.ui.screen

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.ui.viewmodel.TradeViewModel

@Composable
fun TradeScreen(
    empire: Empire,
    tradeModel: TradeViewModel,
    updateEmpire: (Empire) -> Unit
){

    Button(
        onClick = {tradeModel.trade(empire, updateEmpire)}
    ) { }

}