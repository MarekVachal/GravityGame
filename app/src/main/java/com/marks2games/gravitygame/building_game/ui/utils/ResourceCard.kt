package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ResourceCard(
    modifier: Modifier,
    resourceCount: Int,
    icon: Int,
    isStoredResource: Boolean,
    possibleIncome: Int = 0,
    isBordered: Boolean,
    border: Int = 0
) {
val possibleIncomeString = if(possibleIncome < 0) "$possibleIncome" else "+$possibleIncome"
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = "Resource icon in Resource card"
        )
        Text(
            if (isBordered){
                " $resourceCount$possibleIncomeString/$border"
            } else if (isStoredResource) {
                " $resourceCount$possibleIncomeString"
            } else {
                " $resourceCount"
            }
        )
    }

}