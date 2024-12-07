package com.flysolo.etrikedriver.screens.main.bottom_nav.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.flysolo.etrikedriver.ui.theme.EtrikeDriverTheme

@Composable
fun InformationCard(
    modifier: Modifier = Modifier,
    label : String,
    icon : ImageVector,
    value : String ? = null,
    desc : String ? = null,
    ) {
    OutlinedCard(
        modifier =  modifier.fillMaxWidth()
    ) {
        ListItem(
            modifier = modifier.fillMaxWidth(),
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = "icon"
                )
            },
            overlineContent = {
                Text(
                    label,

                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
            },
            headlineContent =  {
                Text(
                    value ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall.copy(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                desc?.let {
                    Badge(
                        content ={
                            Text("${desc}", style = MaterialTheme.typography.labelSmall.copy())
                        }
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationCardPrev() {
    EtrikeDriverTheme {
        InformationCard(
            label = "Pickup",
            icon = Icons.Default.Place,
            value = "hahahahahahahahhshshashasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdadasdadasdasdasdasdasdaasdas"
        )
    }
}
