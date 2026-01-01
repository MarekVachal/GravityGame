package com.marks2games.gravitygame.core.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.getValue
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ui.screens.mainMenuScreen.MainMenuViewModel

@Composable
fun SignInDialog(
    modifier: Modifier,
    toShow: Boolean,
    backToMainMenu: () -> Unit,
    mainMenuModel: MainMenuViewModel,
    context: Context
) {

    val mainMenuUiStates by mainMenuModel.mainMenuUiStates.collectAsState()


    if (toShow){
        Dialog(
            onDismissRequest = backToMainMenu
        ) {
            Card(
                modifier = modifier
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp)
            ){
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = modifier.padding(16.dp)
                ){
                    Column {
                        Row (
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.signInTitle),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Box(
                            modifier = modifier
                                .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                                )
                                .fillMaxWidth()
                                .padding (8.dp)
                        ) {
                            Column(
                                modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                OutlinedTextField(
                                    value = mainMenuUiStates.email,
                                    onValueChange = { mainMenuModel.updateEmailState(it) },
                                    label = { Text(stringResource(R.string.email)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = mainMenuUiStates.password,
                                    onValueChange = { mainMenuModel.updatePasswordState(it) },
                                    label = { Text(stringResource(R.string.password)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(16.dp))

                                if (mainMenuUiStates.errorMessage != null) {
                                    Toast.makeText(
                                        context,
                                        mainMenuUiStates.errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    LaunchedEffect(Unit) {
                                        mainMenuModel.updateErrorMessage(null)
                                    }
                                }
                                if (mainMenuUiStates.isLoading) {
                                    MyProgressIndicator()
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(onClick = { mainMenuModel.signInWithEmail() }) {
                                            Text(stringResource(R.string.login))
                                        }

                                        Button(onClick = { mainMenuModel.registerWithEmail() }) {
                                            Text(stringResource(R.string.register))
                                        }
                                    }
                                }
                            }
                        }

                        Row (
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Button(
                                    onClick = { mainMenuModel.anonymousSignIn() }
                                ) {
                                    Text(
                                        text = stringResource(R.string.signInAnonymously)
                                    )
                                }
                                Spacer(modifier = modifier.size(4.dp))
                                Button(
                                    onClick = { mainMenuModel.signInWithGoogle() },
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = modifier.size(175.dp, 40.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.google_signin_button),
                                        contentDescription = "Google sign in button",
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}