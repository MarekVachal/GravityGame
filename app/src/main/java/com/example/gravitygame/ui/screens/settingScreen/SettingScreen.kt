package com.example.gravitygame.ui.screens.settingScreen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onBackButtonClick: () -> Unit,
    settingModel: SettingViewModel
){
    val settingUiState by settingModel.settingUiState.collectAsState()

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LanguageSelection(
                context = context,
                settingUiState = settingUiState,
                settingModel = settingModel
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = settingUiState.showTutorial,
                    onCheckedChange = { isChecked ->
                        settingModel.changeShowTutorial(toShow = isChecked, context = context)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.showTutorial))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = settingUiState.keepScreenOn,
                    onCheckedChange = { isChecked ->
                        settingModel.changeKeepScreenOn(enabled = isChecked, context = context)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.keepScreenOn))
            }
        }

        Button(
            onClick = {
                settingModel.saveTutorialSettings(
                    context = context
                )
                onBackButtonClick()
            },
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Check icon"
            )
        }
    }
}

@Composable
fun LanguageSelection(
    modifier: Modifier = Modifier,
    context: Context,
    settingUiState: SettingUiState,
    settingModel: SettingViewModel
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.selectLanguage),
            modifier = modifier.padding(bottom = 16.dp)
        )
        LanguageOptionRow(
            text = stringResource(id = R.string.english),
            checked = settingUiState.isEnglishChecked,
            onCheckedChange = {
                if (it) {
                    settingModel.changeLanguage(Languages.ENGLISH)
                    settingModel.setLanguage(context = context, language = "en")
                }
            }
        )
        LanguageOptionRow(
            text = stringResource(id = R.string.czech),
            checked = settingUiState.isCzechChecked,
            onCheckedChange = {
                if (it) {
                    settingModel.changeLanguage(Languages.CZECH)
                    settingModel.setLanguage(context = context, language = "cs")
                }
            }
        )
        /*
        LanguageOptionRow(
            text = stringResource(id = R.string.polish),
            checked = settingUiState.isPolishChecked,
            onCheckedChange = {
                if (it) {
                    settingModel.changeLanguage(Languages.POLISH)
                    settingModel.setLanguage(context = context, language = "pl")
                }
            }
        )

         */
    }
}

@Composable
fun LanguageOptionRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}