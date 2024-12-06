package com.flysolo.etrikedriver.screens.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    icon : ImageVector,
    label : String,
    onClick : () -> Unit
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}