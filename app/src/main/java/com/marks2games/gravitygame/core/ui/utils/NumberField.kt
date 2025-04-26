package com.marks2games.gravitygame.core.ui.utils

import android.util.Log
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    maxValue: Int
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Log.d("NumberInputField", "Recomposed with value: $value")
    TextField(
        value = value,
        onValueChange = { newValue ->
            Log.d("NumberInputField", "onValueChange called with: $newValue")
            if (newValue.all { it.isDigit() }) {
                val intValue = newValue.toIntOrNull()
                if(intValue != null && intValue > maxValue){
                    onValueChange(maxValue.toString())
                } else {
                    onValueChange(newValue)
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions (
            onDone = {
                keyboardController?.hide()
            }
        ),
        label = label
    )
}