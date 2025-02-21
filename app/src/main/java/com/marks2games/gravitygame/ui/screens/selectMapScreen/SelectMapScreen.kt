package com.marks2games.gravitygame.ui.screens.selectMapScreen

import androidx.compose.runtime.Composable
import com.marks2games.gravitygame.models.BattleMapEnum
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun SelectMapScreen(
    battleModel: BattleViewModel,
    onNextButtonClicked: () -> Unit
){
    battleModel.createBattleMap(BattleMapEnum.TINY)
    onNextButtonClicked()
}