package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ClipEntity
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.*

@Composable
fun ClipReviewScreen(
    clipId: String,
    viewModel: ShortsForgeViewModel
) {
    val clipState by viewModel.repository.observeClip(clipId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "Review & Polish Short",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() }
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        val clip = clipState
        if (clip == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForgeSecondary)
            }
            return@Scaffold
        }

        var editableTitle by remember(clip.suggestedTitle) { mutableStateOf(clip.suggestedTitle) }
        var editableTranscript by remember(clip.editableTranscript) {
            mutableStateOf(clip.editableTranscript.ifBlank { clip.transcriptExcerpt })
        }
        var editableNarration by remember(clip.narrationScript) { mutableStateOf(clip.narrationScript) }
        var startSec by remember(clip.startSeconds) { mutableDoubleStateOf(clip.startSeconds) }
        var endSec by remember(clip.endSeconds) { mutableDoubleStateOf(clip.endSeconds) }
        var cropMode by remember(clip.cropMode) { mutableStateOf(clip.cropMode) }
        var subStyle by remember(clip.subtitleStyle) { mutableStateOf(clip.subtitleStyle) }
        var isPlayingPreview by remember { mutableStateOf(true) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 9:16 Vertical Video Frame Preview
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                        .border(
                            2.dp,
                            Brush.verticalGradient(listOf(ForgePrimary, ForgeSecondary)),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Vertical 9:16 aspect ratio box simulator
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(9f / 16f)
                            .background(ForgeSurfaceVariant)
                    ) {
                        // Background gradient / video simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1E1B4B),
                                            Color(0xFF0F172A),
                                            Color(0xFF18182E)
                                        )
                                    )
                                )
                        )

                        // Top Overlay badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "9:16 HD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ForgeSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (cropMode == "subject_aware") ForgeSuccess.copy(alpha = 0.2f) else ForgeBorder)
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (cropMode == "subject_aware") "● Face Track" else "Center Crop",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (cropMode == "subject_aware") ForgeSuccess else ForgeTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Center Play / Pause Indicator
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .align(Alignment.Center)
                                .clickable { isPlayingPreview = !isPlayingPreview },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White
                            )
                        }

                        // Burn-in Subtitles Simulator (Positioned in vertical safe zone, bottom 28%)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 54.dp, start = 12.dp, end = 12.dp)
                        ) {
                            val sampleWords = editableTranscript.take(50).uppercase()
                            when (subStyle) {
                                "neon_karaoke" -> {
                                    Text(
                                        text = sampleWords,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFACC15),
                                            textAlign = TextAlign.Center,
                                            lineHeight = 22.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(6.dp)
                                    )
                                }
                                "bold_boxed" -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = sampleWords,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        text = sampleWords,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // TikTok/Reels Safe Margin guidelines notice
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "Safe Overlays Margin OK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }
            }

            // Timestamps Fine-Tuning Slider
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeSecondary.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clip Trim Bounds",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeTextPrimary
                                )
                            )
                            val duration = (endSec - startSec).coerceAtLeast(1.0)
                            Text(
                                text = "Duration: ${String.format(java.util.Locale.US, "%.1f", duration)}s",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = ForgeSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Start & End controllers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Start Time
                            Column {
                                Text("Start: ${String.format(java.util.Locale.US, "%.1fs", startSec)}", style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary))
                                Row {
                                    IconButton(
                                        onClick = { if (startSec > 0.5) startSec -= 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "-0.5s", tint = ForgeSecondary)
                                    }
                                    IconButton(
                                        onClick = { if (startSec + 5.0 < endSec) startSec += 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircleOutline, contentDescription = "+0.5s", tint = ForgeSecondary)
                                    }
                                }
                            }

                            // End Time
                            Column(horizontalAlignment = Alignment.End) {
                                Text("End: ${String.format(java.util.Locale.US, "%.1fs", endSec)}", style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary))
                                Row {
                                    IconButton(
                                        onClick = { if (endSec - 0.5 > startSec + 5.0) endSec -= 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "-0.5s", tint = ForgeSecondary)
                                    }
                                    IconButton(
                                        onClick = { endSec += 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircleOutline, contentDescription = "+0.5s", tint = ForgeSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Title & AI Rewrite Hook Button
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Short Title & Hook",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeTextPrimary
                                )
                            )
                            TextButton(
                                onClick = {
                                    viewModel.rewriteClipHookWithAi(clip)
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = ForgeSecondary)
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("⚡ AI Hook Rewrite", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editableTitle,
                            onValueChange = { editableTitle = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgePrimary,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = ForgeTextPrimary,
                                unfocusedTextColor = ForgeTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
                        )
                    }
                }
            }

            // Editable Transcript & Subtitles
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Spoken Transcript (Auto Subtitles Source)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editableTranscript,
                            onValueChange = { editableTranscript = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgePrimary,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = ForgeTextPrimary,
                                unfocusedTextColor = ForgeTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_transcript_input"),
                            minLines = 3
                        )
                    }
                }
            }

            // Cropping Mode & Subtitle Style Controls
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Framing & Visual Styles",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Crop Mode
                        Text("Cropping & Framing:", style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = cropMode == "subject_aware",
                                onClick = { cropMode = "subject_aware" },
                                label = { Text("Smart Subject-Aware") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForgePrimary,
                                    selectedLabelColor = ForgeOnPrimary,
                                    containerColor = ForgeSurfaceVariant,
                                    labelColor = ForgeTextSecondary
                                )
                            )
                            FilterChip(
                                selected = cropMode == "center_crop",
                                onClick = { cropMode = "center_crop" },
                                label = { Text("Center 9:16 Crop") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForgePrimary,
                                    selectedLabelColor = ForgeOnPrimary,
                                    containerColor = ForgeSurfaceVariant,
                                    labelColor = ForgeTextSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Subtitle Style
                        Text("Subtitle Styling:", style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "neon_karaoke" to "Neon",
                                "bold_boxed" to "Boxed",
                                "cinematic_outline" to "Cinematic",
                                "arabic_rtl" to "RTL"
                            ).forEach { (id, label) ->
                                FilterChip(
                                    selected = subStyle == id,
                                    onClick = { subStyle = id },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForgeSecondary,
                                        selectedLabelColor = ForgeOnSecondary,
                                        containerColor = ForgeSurfaceVariant,
                                        labelColor = ForgeTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val updated = clip.copy(
                                suggestedTitle = editableTitle,
                                editableTranscript = editableTranscript,
                                narrationScript = editableNarration,
                                startSeconds = startSec,
                                endSeconds = endSec,
                                cropMode = cropMode,
                                subtitleStyle = subStyle
                            )
                            viewModel.updateClipDetails(updated)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Save Changes")
                    }

                    Button(
                        onClick = {
                            val updated = clip.copy(
                                suggestedTitle = editableTitle,
                                editableTranscript = editableTranscript,
                                narrationScript = editableNarration,
                                startSeconds = startSec,
                                endSeconds = endSec,
                                cropMode = cropMode,
                                subtitleStyle = subStyle
                            )
                            viewModel.updateClipDetails(updated)
                            viewModel.renderClip(clip.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp).testTag("render_short_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = ForgeOnSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Render Short", fontWeight = FontWeight.Bold, color = ForgeOnSecondary)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
