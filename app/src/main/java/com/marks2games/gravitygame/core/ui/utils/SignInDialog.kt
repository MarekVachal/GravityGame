package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.marks2games.gravitygame.R

@Composable
fun SignInDialog(
    modifier: Modifier,
    toShow: Boolean,
    backToMainMenu: () -> Unit,
    signInAnonymously: () -> Unit,
    signInWithGoogle: () -> Unit
) {
    if (toShow){
        Dialog(
            onDismissRequest = backToMainMenu
        ) {
            Card(
                modifier = modifier
                    .wrapContentSize()
                    ,
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

                        Row (
                            modifier = modifier,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Text(
                                text = stringResource(R.string.signInText),
                                textAlign = TextAlign.Justify
                            )
                        }

                        Row (
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Button(
                                    onClick = signInAnonymously
                                ) {
                                    Text(
                                        text = stringResource(R.string.signInAnonymously)
                                    )
                                }
                                Spacer(modifier = modifier.size(4.dp))
                                Button(
                                    onClick = signInWithGoogle,
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