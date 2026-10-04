package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ClipEntity
import com.example.ui.Screen
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: ShortsForgeViewModel
) {
    val project by viewModel.repository.observeProject(projectId).collectAsState(initial = null)
    val clips by viewModel.repository.observeClipsForProject(projectId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = project?.title ?: "Project Details",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ChatStudio(projectId)) },
                        modifier = Modifier.testTag("project_chat_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Project AI Assistant",
                            tint = ForgePrimary
                        )
                    }
                }
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        if (project == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForgeSecondary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Project Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgePrimaryDim.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = project?.thumbnailUri,
                                contentDescription = project?.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 84.dp, height = 56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project?.title ?: "",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeTextPrimary
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = project?.channelOrAuthor ?: "",
                                        style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusBadge(status = project?.status ?: "draft")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Proposed Highlights (${clips.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForgeTextPrimary
                        )
                    )
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.ChatStudio(projectId)) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForgePrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Copilot", fontSize = 12.sp)
                    }
                }
            }

            if (clips.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ForgeSecondary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gemini is analyzing video segments...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = ForgeTextPrimary)
                            )
                        }
                    }
                }
            } else {
                items(clips, key = { it.id }) { clip ->
                    ClipProposalCard(
                        clip = clip,
                        onReview = {
                            viewModel.selectedClipId.value = clip.id
                            viewModel.navigateTo(Screen.ClipReview(clip.id))
                        },
                        onQuickRender = {
                            viewModel.renderClip(clip.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ClipProposalCard(
    clip: ClipEntity,
    onReview: () -> Unit,
    onQuickRender: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReview)
            .testTag("clip_proposal_${clip.id}"),
        colors = CardDefaults.cardColors(containerColor = ForgeSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeSecondary.copy(alpha = 0.25f))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Viral Score badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ForgeSecondary.copy(alpha = 0.15f))
                        .border(1.dp, ForgeSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = ForgeSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Viral Score: ${clip.viralScore}/100",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeSecondary
                            )
                        )
                    }
                }

                // Duration tag
                val dur = (clip.endSeconds - clip.startSeconds).toInt()
                val startM = (clip.startSeconds / 60).toInt()
                val startS = (clip.startSeconds % 60).toInt()
                val endM = (clip.endSeconds / 60).toInt()
                val endS = (clip.endSeconds % 60).toInt()
                Text(
                    text = String.format("%02d:%02d - %02d:%02d (%ds)", startM, startS, endM, endS, dur),
                    style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextTertiary)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = clip.suggestedTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ForgeTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hook line highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ForgeSurfaceVariant)
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = ForgeTertiary,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hook: \"${clip.hook}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ForgeTextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = clip.summary,
                style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReview,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForgeTextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review & Polish", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onQuickRender,
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeSecondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, tint = ForgeOnSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (clip.exportStatus == "exported") "Re-render" else "Render 9:16",
                        fontWeight = FontWeight.Bold,
                        color = ForgeOnSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
