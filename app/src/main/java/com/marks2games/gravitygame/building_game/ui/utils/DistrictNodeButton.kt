package com.marks2games.gravitygame.building_game.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.core.data.model.DistrictNode
import com.marks2games.gravitygame.core.data.model.MapUiState
import com.marks2games.gravitygame.R

@Composable
fun DistrictNodeButton(
    node: District?,
    mapUiState: MapUiState<DistrictNode>
){
  Card(
      modifier = Modifier.fillMaxSize(),
      colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
      )
  )  {
      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
      ){
          Text(
              text = stringResource(node?.type?.nameIdNominative?: R.string.unknown_district),
              fontSize = (12 * mapUiState.scale.coerceIn(0.7f, 1.5f)).sp,
              maxLines = 2,
              textAlign = TextAlign.Center
          )
      }
  }
}