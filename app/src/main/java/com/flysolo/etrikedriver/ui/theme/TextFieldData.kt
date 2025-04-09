package com.flysolo.etrikedriver.ui.theme

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class TextFieldData(
    val value : String = "",
    val hasError: Boolean = false,
    val errorMessage : String ? = null
)

data class Password(
    val value : String = "",
    val isError : Boolean = false,
    val errorMessage : String? = null
)




@Composable
fun TextFieldDefaults.defaultColors(): TextFieldColors {
    return this.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )
}