package com.flysolo.etrikedriver.screens.main.no_active_franchise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flysolo.etrikedriver.R


@Composable
fun NoActiveFranchise(modifier: Modifier = Modifier) {
    Scaffold {
        Column(
            modifier = modifier.fillMaxSize().padding(it).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = modifier.size(120.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = Color.White)
            )
            Spacer(
                modifier = modifier.height(16.dp)
            )

            Text("You have no active franchise. Please contact Etrike Admin.", style = MaterialTheme.typography.titleMedium.copy(
                textAlign = TextAlign.Center
            ))
        }
    }

}