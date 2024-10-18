package com.example.gravitygame.ui.screens.statisticScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.gravitygame.R
import com.example.gravitygame.ui.utils.BattleResultEnum

class StatisticViewModel: ViewModel() {

    fun callBattleResultForStatistic(result: BattleResultEnum, context: Context): String{
        return when(result){
            BattleResultEnum.WIN -> context.getString(R.string.winNominative)
            BattleResultEnum.LOSE -> context.getString(R.string.lostNominative)
            BattleResultEnum.DRAW -> context.getString(R.string.drawNominative)
        }
    }
}