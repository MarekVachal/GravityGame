package com.example.gravitygame.ui.screen

import androidx.compose.runtime.Composable
import com.example.gravitygame.maps.TinyMap
import com.example.gravitygame.viewModels.BattleViewModel

@Composable
fun SelectMapScreen(
    battleModel: BattleViewModel,
    onNextButtonClicked: () -> Unit
){
    battleModel.createBattleMap(TinyMap())
    onNextButtonClicked()
}