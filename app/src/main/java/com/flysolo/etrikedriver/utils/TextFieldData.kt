package com.flysolo.etrikedriver.utils

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


data class TextFieldData(
    val value : String = "",
    val hasError: Boolean = false,
    val errorMessage : String ? = null
)


@Composable
fun TextFieldDefaults.EtrikeColors(): TextFieldColors {
    return this.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,

    )
}