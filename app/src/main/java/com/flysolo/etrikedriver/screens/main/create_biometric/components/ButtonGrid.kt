package com.flysolo.etrikedriver.screens.main.create_biometric.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ButtonGrid(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onDelete: () -> Unit,
    isBiometricEnabled : Boolean =false,
    onBiometricClick : () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "","0", "<"
    )

    // Chunking the list into groups of 3
    val buttonChunks = buttons.chunked(3)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonChunks.forEach { chunk ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chunk.forEach { value ->

                    when (value) {
                        "<" -> {
                            PinButton(
                                modifier = Modifier.size(56.dp),
                                value = value,
                                onClick = { onDelete() }
                            )
                        }
                        "" -> {
                            if (isBiometricEnabled) {
                                OutlinedIconButton(
                                    onClick = onBiometricClick,
                                    shape = CircleShape,
                                    modifier = modifier.size(56.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "FingerPrint"
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.size(56.dp),
                                ) {

                                }
                            }

                        }
                        else -> {
                            PinButton(
                                modifier = Modifier.size(56.dp),
                                value = value,
                                onClick = { onClick(value) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinButton(
    modifier: Modifier = Modifier,
    value: String,
    onClick: (String) -> Unit
) {
    OutlinedButton(
        onClick = { onClick(value) },
        shape = CircleShape,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
