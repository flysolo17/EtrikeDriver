package com.flysolo.etrikedriver.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.flysolo.etrikedriver.R


@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    url : String,
    size : Dp = 150.dp,
    onClick : () -> Unit
) {
    AsyncImage(
        model = url,
        modifier = modifier
            .size(size)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentDescription = "Profile",
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.ic_profile_filled),
        placeholder = painterResource(R.drawable.ic_profile_filled)
    )
}