package com.marks2games.gravitygame.ui.screens.infoDialogsScreens

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.signIn.GoogleSign
import com.marks2games.gravitygame.ui.screens.accountScreen.AccountViewModel

@Composable
fun DeleteAccountDialog(
    modifier: Modifier,
    accountModel: AccountViewModel,
    toShow: Boolean,
    googleSign: GoogleSign,
    context: Context
) {
    if (toShow) {
        AlertDialog(
            onDismissRequest = { accountModel.updateShowDeleteAccountDialog(false) },
            confirmButton = {
                Button(
                    onClick = { accountModel.deleteUserAccount(googleSign, context) }
                ) {
                    Text(
                        text = stringResource(R.string.deleteAccount)
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { accountModel.updateShowDeleteAccountDialog(false) }
                ) {
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.deleteAccount)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.deleteAccountText)
                )
            },
            modifier = modifier
        )
    }
}