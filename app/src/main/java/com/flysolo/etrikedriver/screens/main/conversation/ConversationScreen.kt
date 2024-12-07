package com.flysolo.etrikedriver.screens.main.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.models.messages.Message
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.screens.shared.BackButton
import com.flysolo.etrikedriver.utils.display
import com.flysolo.etrikedriver.utils.shortToast
import kotlinx.coroutines.SupervisorJob


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    passengerID : String,
    state: ConversationState,
    events : (ConversationEvents) -> Unit,

    navHostController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope { SupervisorJob() }
    LaunchedEffect(
        passengerID
    ) {
        if (passengerID.isNotEmpty()) {
            events.invoke(ConversationEvents.OnGetPassenger(passengerID))
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Avatar(
                            url = state.passenger?.profile ?: "",
                            size = 40.dp
                        ) { }
                        Text("${state.passenger?.name}")
                    }
                },
                navigationIcon = {
                    BackButton {
                      navHostController.popBackStack()
                    }
                }
            )
        }

    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .weight(1f),
                reverseLayout = true
            ){
                if (state.messages.isEmpty()) {
                    item {
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No conversation yet!")
                        }
                    }
                }
                items(state.messages) {
                    val isMyMessage = it.senderID == state.user?.id
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .align(
                                if (isMyMessage) Alignment.End else Alignment.Start
                            ),
                        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                    ){
                        MessageBubble(
                            message = it,
                            sender = state.user,
                            receiver = state.passenger
                        )
                    }
                }
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = state.message,
                    onValueChange = {events(ConversationEvents.OnMessageChange(it))},
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text("Enter your message") },
                    trailingIcon = {
                        IconButton(
                            enabled = !state.isSendingMessage,
                            onClick = {
                                if (state.message.isEmpty()) {
                                    context.shortToast("Please add message")
                                    return@IconButton
                                }
                                events.invoke(ConversationEvents.OnSendMessage(state.message))
                            }
                        ) {
                            if (state.isSendingMessage) {
                                CircularProgressIndicator(
                                    modifier = modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send"
                                )
                            }

                        }
                    }
                )
            }
        }

    }
}


@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    message: Message,
    sender : com.flysolo.etrikedriver.models.users.User ? ,
    receiver :  com.flysolo.etrikedriver.models.users.User ? )
{
    val isMyMessage = message.senderID == sender?.id
    val containerColor =  if (isMyMessage) MaterialTheme.colorScheme.primary else Color.Unspecified
    Card(
        modifier = modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColorFor(containerColor)
        )
    ) {
        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = if (isMyMessage) Alignment.End  else Alignment.Start
        ) {
            Text("${message.message}", style = MaterialTheme.typography.titleSmall)
            Text(message.createdAt.display(), style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            ))

        }


    }

}
