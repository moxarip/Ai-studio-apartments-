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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.JobEntity
import com.example.data.model.ProjectEntity
import com.example.ui.Screen
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.MetricStatCard
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: ShortsForgeViewModel
) {
    val projects by viewModel.projects.collectAsState()
    val exports by viewModel.exports.collectAsState()
    val activeJobs by viewModel.activeJobs.collectAsState()

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "ShortsForge AI",
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ChatStudio("global")) },
                        modifier = Modifier.testTag("action_chat_studio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "AI Copilot Chat",
                            tint = ForgePrimary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ExportHistory) },
                        modifier = Modifier.testTag("action_exports")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Exported Shorts",
                            tint = ForgeSecondary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        modifier = Modifier.testTag("action_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = ForgeTextSecondary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.NewProject) },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOnSecondary) },
                text = { Text("New Project", fontWeight = FontWeight.Bold, color = ForgeOnSecondary) },
                containerColor = ForgeSecondary,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("fab_new_project")
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Hero Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(ForgePrimary, ForgeSecondary)),
                            RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = ForgeSurfaceElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(ForgePrimaryDim.copy(alpha = 0.35f), Color.Transparent),
                                    radius = 700f
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ForgeSecondary)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "GEMINI PRO VIDEO PIPELINE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ForgeOnSecondary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "9:16 Auto-Framing",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextTertiary)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Forge Viral Shorts from Long-Form Videos",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForgeTextPrimary,
                                    lineHeight = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Paste an authorized video source or upload local media. Gemini AI detects the peak retention moments, generates animated captions, and renders portrait shorts ready for TikTok & Reels.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ForgeTextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Screen.NewProject) },
                                colors = ButtonDefaults.buttonColors(containerColor = ForgePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("hero_create_project_button")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = ForgeOnPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start New Project", fontWeight = FontWeight.Bold, color = ForgeOnPrimary)
                            }
                        }
                    }
                }
            }

            // Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Projects",
                        value = "${projects.size}",
                        icon = Icons.Default.Folder,
                        accentColor = ForgePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Active Jobs",
                        value = "${activeJobs.size}",
                        icon = Icons.Default.Sync,
                        accentColor = ForgeSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Shorts",
                        value = "${exports.size}",
                        icon = Icons.Default.MovieFilter,
                        accentColor = ForgeTertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Active Processing Jobs Card
            if (activeJobs.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Processing Pipeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForgeTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeJobs.forEach { job ->
                            ActiveJobCard(
                                job = job,
                                onClick = {
                                    viewModel.activeJobId.value = job.id
                                    viewModel.navigateTo(Screen.JobStatus(job.id))
                                }
                            )
                        }
                    }
                }
            }

            // Recent Projects Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Projects",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForgeTextPrimary
                        )
                    )
                    if (projects.isNotEmpty()) {
                        Text(
                            text = "${projects.size} total",
                            style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextTertiary)
                        )
                    }
                }
            }

            // Projects List or Empty State
            if (projects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.2f))))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(ForgeSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoCall,
                                    contentDescription = null,
                                    tint = ForgeSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No Projects Forged Yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Paste a YouTube link or select an authorized video source to detect viral moments.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ForgeTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.NewProject) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ForgeSecondary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ForgeSecondary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Create Your First Project")
                            }
                        }
                    }
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectListItem(
                        project = project,
                        onClick = {
                            viewModel.selectedProjectId.value = project.id
                            viewModel.navigateTo(Screen.ProjectDetail(project.id))
                        },
                        onDelete = {
                            viewModel.deleteProject(project.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ActiveJobCard(
    job: JobEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("active_job_${job.id}"),
        colors = CardDefaults.cardColors(containerColor = ForgeSurfaceVariant),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ForgeSecondary, ForgePrimary)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { job.progress / 100f },
                        modifier = Modifier.size(24.dp),
                        color = ForgeSecondary,
                        strokeWidth = 3.dp,
                        trackColor = ForgeBorder
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = formatStageTitle(job.stage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Text(
                            text = "Stage Progress: ${job.progress}%",
                            style = MaterialTheme.typography.labelSmall.copy(color = ForgeSecondary)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Status",
                    tint = ForgeTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { job.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ForgeSecondary,
                trackColor = ForgeBorder
            )
        }
    }
}

@Composable
fun ProjectListItem(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("project_item_${project.id}"),
        colors = CardDefaults.cardColors(containerColor = ForgeSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.2f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ForgeSurfaceVariant)
            ) {
                if (project.thumbnailUri.isNotBlank()) {
                    AsyncImage(
                        model = project.thumbnailUri,
                        contentDescription = project.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = ForgeTextTertiary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Duration pill overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    val m = (project.durationSeconds / 60).toInt()
                    val s = (project.durationSeconds % 60).toInt()
                    Text(
                        text = String.format("%02d:%02d", m, s),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.channelOrAuthor.ifBlank { "Creator" },
                        style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = project.status)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = ForgeTextTertiary
                )
            }
        }
    }
}

fun formatStageTitle(stage: String): String {
    return when (stage) {
        "validating_source" -> "Validating Legal Authorization"
        "transcribing" -> "Transcribing Audio Track"
        "analyzing_highlights" -> "Gemini Pro Retention Analysis"
        "awaiting_user_approval" -> "Clips Ready for Approval"
        "preparing_subtitles" -> "Generating Subtitle Cues (ASS)"
        "generating_narration" -> "Generating AI Narration"
        "rendering" -> "Rendering 9:16 FFmpeg Video"
        "completed" -> "Completed"
        "failed" -> "Processing Failed"
        else -> stage.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}
