package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatStudioScreen(
    projectId: String,
    viewModel: ShortsForgeViewModel
) {
    val messages by viewModel.repository.observeChat(projectId).collectAsState(initial = emptyList())
    val inputText by viewModel.chatInputText.collectAsState()
    val isSending by viewModel.isSendingChatMessage.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val promptSuggestions = listOf(
        "Suggest 3 explosive hooks for this clip",
        "How to optimize for 85% retention on Shorts?",
        "Rewrite this transcript with dramatic pacing",
        "Recommend high-energy B-roll sound effects"
    )

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "AI Director Copilot",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearChat(projectId) },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = ForgeTextTertiary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = ForgeSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .border(0.5.dp, ForgeBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Prompt chips row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(promptSuggestions) { prompt ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.chatInputText.value = prompt
                                    viewModel.sendChatMessage(projectId)
                                },
                                label = { Text(prompt, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = ForgeSurfaceVariant,
                                    labelColor = ForgeTextSecondary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    borderColor = ForgeBorder,
                                    enabled = true
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    // Input bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { viewModel.chatInputText.value = it },
                            placeholder = { Text("Ask your AI video director...", color = ForgeTextTertiary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgePrimary,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = ForgeTextPrimary,
                                unfocusedTextColor = ForgeTextPrimary,
                                focusedContainerColor = ForgeBackground,
                                unfocusedContainerColor = ForgeBackground
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.sendChatMessage(projectId) },
                            enabled = inputText.isNotBlank() && !isSending,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) ForgePrimary else ForgeBorder)
                                .testTag("chat_send_button")
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = ForgeOnPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = ForgeOnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = ForgeBackground
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // AI Role Header Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ForgeSurfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ForgePrimary, ForgeSecondary))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ShortsForge Creative Director",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeTextPrimary
                                )
                            )
                            Text(
                                text = "Powered by Gemini 3.5 Flash • Multi-turn Copilot",
                                style = MaterialTheme.typography.labelSmall.copy(color = ForgeSecondary)
                            )
                        }
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = ForgeTextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No messages yet.",
                                style = MaterialTheme.typography.titleSmall.copy(color = ForgeTextSecondary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ask for advice on viral pacing, captions, hooks, or retention strategies.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ForgeTextTertiary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessageEntity) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ForgeSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = ForgeOnSecondary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) ForgePrimaryDim else ForgeSurface)
                .border(
                    0.5.dp,
                    if (isUser) ForgePrimary else ForgeBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ForgeTextPrimary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}
