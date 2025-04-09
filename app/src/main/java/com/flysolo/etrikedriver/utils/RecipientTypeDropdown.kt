package com.flysolo.etrikedriver.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flysolo.etrikedriver.screens.cashout.RecipientType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientTypeDropdown(
    recipients : List<RecipientType>,
    selected: RecipientType,
    onDoctorSelected: (RecipientType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownLabel = selected.name

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            shape = MaterialTheme.shapes.medium,
            value = dropdownLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Recipient Type") },
            supportingText = {
                Text("")
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors =  TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            recipients.forEach { recipient ->
                DropdownMenuItem(
                    onClick = {
                        onDoctorSelected(recipient)
                        expanded = false
                    },
                    text = { Text(text = recipient.name) }
                )
            }
        }
    }
}
