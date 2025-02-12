package com.marks2games.gravitygame.ui.screens.accountScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.signIn.GoogleSign
import com.marks2games.gravitygame.ui.screens.infoDialogsScreens.DeleteAccountDialog

@Composable
fun AccountScreen (
    modifier: Modifier = Modifier,
    onStatisticButtonClick: () -> Unit,
    //onAchievementsButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    accountModel: AccountViewModel,
    context: Context,
    googleSign: GoogleSign
){
    val accountUiState by accountModel.accountUiState.collectAsState()

    LaunchedEffect (Unit){
        accountModel.updateUserName(context)
        accountModel.updateUserEmail(context)
    }

    Image(
        painter = painterResource(id = R.drawable.main_menu_back),
        contentDescription = "Background for main menu",
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize()
    )

    DeleteAccountDialog(
        modifier = modifier,
        accountModel = accountModel,
        toShow = accountUiState.showDeleteAccountDialog,
        googleSign = googleSign,
        context = context
    )

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Card (
            modifier = modifier.wrapContentSize()
        ) {
            Column (
                modifier = modifier.padding(12.dp),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    text = "${stringResource(id = R.string.userEmail)}: ${accountUiState.userEmail}"
                )
                Spacer(modifier.size(4.dp))
                Text(
                    text = "${stringResource(R.string.displayName)}: ${accountUiState.userName}"
                )
                Spacer(modifier.size(4.dp))
                Button(
                    onClick = {
                        accountModel.setClickOnButton(context = context, googleSign = googleSign)
                    }
                ) {
                    Text(
                        text = accountModel.setTextOnClickButton(context = context)
                    )
                }
                Spacer(modifier.size(4.dp))
                Button(
                    onClick = { accountModel.updateShowDeleteAccountDialog(true) }
                ) {
                    Text(
                        text = stringResource(R.string.deleteAccount)
                    )
                }
            }
        }

        Button(
            onClick = { onStatisticButtonClick() }
        ) {
            Text(text = stringResource(id = R.string.statisticTitle))
        }
        /*
        Button(
            onClick = {onAchievementsButtonClick()},
            enabled = false
        ) {
            Text(text = stringResource(id = R.string.achievementsTitle))
        }

         */
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 24.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ){
        IconButton(
            onClick = { onBackButtonClick() }
        ) {
            Icon(
                painterResource(R.drawable.check),
                contentDescription = "Check icon",
                tint = Color.Unspecified,
                modifier = modifier.size(48.dp)
            )
        }
    }
}