package com.flysolo.etrikedriver.screens.main.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.screens.main.messages.MessagesEvents
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.messages.UserWithMessage
import com.flysolo.etrikedriver.screens.shared.Avatar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier,
    id : String,
    state: MessagesState,
    events: (MessagesEvents) -> Unit,
    navHostController: NavHostController
) {
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            events(MessagesEvents.OnGetMyMessages(id))
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {

                    Text(text = "Messages")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    ) }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
            items(state.userWithMessage) {
                UserWithMessagesCard(userWithMessage = it) { id ->
                    navHostController.navigate(AppRouter.CONVERSATION.navigate(id))
                }
            }
        }
    }
}

@Composable
fun UserWithMessagesCard(
    modifier: Modifier = Modifier,
    userWithMessage: UserWithMessage,
    onClick : (String) -> Unit,
) {
    val user = userWithMessage.user
    val messages = userWithMessage.messages.sortedByDescending { it.createdAt }
    ListItem(
        modifier = modifier.fillMaxWidth().clickable {
            onClick(user?.id ?: "")
        },
        leadingContent = {
            Avatar(
                url = user?.profile?: "",
                size = 42.dp
            ) { }
        },
        headlineContent = {
            Text("${user?.name}")
        },
        supportingContent = {
            val message = messages.getOrNull(0)
            Text(message?.message ?: "No mesage")
        }
    )
}