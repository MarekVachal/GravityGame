package com.marks2games.gravitygame.battle_game.ui.screens.selectMapScreen

import androidx.compose.runtime.Composable
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleMapEnum
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun SelectMapScreen(
    battleModel: BattleViewModel,
    onNextButtonClicked: () -> Unit
){
    battleModel.createBattleMap(BattleMapEnum.TINY)
    onNextButtonClicked()
}