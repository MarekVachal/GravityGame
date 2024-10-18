package com.example.gravitygame.ui.screens.selectMapScreen

import androidx.compose.runtime.Composable
import com.example.gravitygame.maps.BattleMapEnum
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun SelectMapScreen(
    battleModel: BattleViewModel,
    onNextButtonClicked: () -> Unit
){
    battleModel.createBattleMap(BattleMapEnum.TINY)
    onNextButtonClicked()
}