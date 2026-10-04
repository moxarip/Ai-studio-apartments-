package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun JobStatusScreen(
    jobId: String,
    viewModel: ShortsForgeViewModel
) {
    val jobState by viewModel.repository.observeJob(jobId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "Processing Job Status",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() }
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        val job = jobState
        if (job == null) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Progress Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ForgeSecondary, ForgePrimary)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Current Stage",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary)
                                )
                                Text(
                                    text = formatStageTitle(job.stage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeTextPrimary
                                    )
                                )
                            }
                            StatusBadge(status = job.stage)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Progress", style = MaterialTheme.typography.labelMedium.copy(color = ForgeTextTertiary))
                            Text("${job.progress}%", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ForgeSecondary))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { job.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ForgeSecondary,
                            trackColor = ForgeBorder
                        )
                    }
                }
            }

            // Real Pipeline Stage Timeline
            item {
                Text(
                    text = "Durable Job Pipeline",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                val pipelineStages = listOf(
                    "validating_source" to "Source Authorization & Container Check",
                    "transcribing" to "Speech-to-Text Transcription",
                    "analyzing_highlights" to "Gemini Pro Video Understanding",
                    "awaiting_user_approval" to "Clips Proposed & Approval",
                    "preparing_subtitles" to "ASS Subtitle Generation",
                    "rendering" to "FFmpeg 9:16 Portrait Cropping & Burn-in",
                    "completed" to "Finalized & Export Ready"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        pipelineStages.forEachIndexed { index, (stageKey, stageLabel) ->
                            val isCompleted = isStagePassed(job.stage, stageKey)
                            val isCurrent = job.stage == stageKey
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCompleted -> ForgeSuccess
                                                isCurrent -> ForgeSecondary
                                                else -> ForgeBorder
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text("${index + 1}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stageLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isCurrent) ForgeTextPrimary else if (isCompleted) ForgeTextSecondary else ForgeTextTertiary,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Live Worker Terminal Logs Console
            item {
                Text(
                    text = "Worker Terminal Execution Log",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, Color(0xFF1E293B))))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STDOUT / STDERR CONSOLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ForgeTextTertiary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (job.stage == "completed") ForgeSuccess else ForgeSecondary)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = job.logMessages.ifBlank { "[Worker] Initializing queue listener on container node..." },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Actions (Review Clips or Retry)
            item {
                if (job.stage == "awaiting_user_approval") {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.ProjectDetail(job.projectId)) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("review_clips_button")
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = null, tint = ForgeOnSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Review AI Proposed Clips", fontWeight = FontWeight.Bold, color = ForgeOnSecondary)
                    }
                } else if (job.stage == "completed") {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.ExportHistory) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeSuccess),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("view_exports_button")
                    ) {
                        Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Rendered Shorts", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                } else if (job.stage == "failed") {
                    Button(
                        onClick = { viewModel.retryJob(job) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry Processing Job", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

fun isStagePassed(currentStage: String, checkStage: String): Boolean {
    val order = listOf(
        "validating_source",
        "transcribing",
        "analyzing_highlights",
        "awaiting_user_approval",
        "preparing_subtitles",
        "generating_narration",
        "rendering",
        "uploading_output",
        "completed"
    )
    val curIdx = order.indexOf(currentStage)
    val checkIdx = order.indexOf(checkStage)
    return curIdx > checkIdx || currentStage == "completed"
}
