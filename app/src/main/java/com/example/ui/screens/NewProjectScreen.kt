package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.media.AuthorizedSampleSource
import com.example.data.media.MediaSourceProvider
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.*

@Composable
fun NewProjectScreen(
    viewModel: ShortsForgeViewModel
) {
    val urlInput by viewModel.youtubeUrlInput.collectAsState()
    val isFetchingMeta by viewModel.isFetchingYouTubeMeta.collectAsState()
    val metadata by viewModel.youtubeMetadata.collectAsState()
    val selectedSample by viewModel.selectedSampleSource.collectAsState()
    val uploadedUri by viewModel.uploadedMediaUri.collectAsState()
    val isAuthorizedCert by viewModel.isUserAuthorizedCert.collectAsState()
    val targetDuration by viewModel.targetDurationSec.collectAsState()
    val subtitleStyle by viewModel.selectedSubtitleStyle.collectAsState()
    val narrationEnabled by viewModel.narrationEnabled.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

    // Zero-permission Android Photo/Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onMediaUploaded(uri.toString())
        }
    }

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "New Shorts Project",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() }
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
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    val canStart = isAuthorizedCert && (uploadedUri != null || selectedSample != null)
                    Button(
                        onClick = { viewModel.createAndStartProject() },
                        enabled = canStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForgeSecondary,
                            disabledContainerColor = ForgeBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_analysis_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ForgeOnSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start AI Forge Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ForgeOnSecondary
                        )
                    }
                    if (!canStart) {
                        Text(
                            text = "⚠️ Please select an authorized video source or upload a file and confirm authorization.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ForgeWarning,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        )
                    }
                }
            }
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
                Spacer(modifier = Modifier.height(4.dp))
                // Step 1: YouTube URL Input
                Text(
                    text = "1. YouTube Source URL (Metadata Reference)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { viewModel.validateAndFetchYouTubeMetadata(it) },
                        placeholder = { Text("https://www.youtube.com/watch?v=...", color = ForgeTextTertiary) },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = ForgePrimary) },
                        trailingIcon = {
                            if (isFetchingMeta) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = ForgeSecondary)
                            } else if (urlInput.isNotBlank()) {
                                IconButton(onClick = { viewModel.validateAndFetchYouTubeMetadata(urlInput) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = ForgeSecondary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgePrimary,
                            unfocusedBorderColor = ForgeBorder,
                            focusedTextColor = ForgeTextPrimary,
                            unfocusedTextColor = ForgeTextPrimary,
                            focusedContainerColor = ForgeSurface,
                            unfocusedContainerColor = ForgeSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("youtube_url_input"),
                        singleLine = true
                    )
                }

                // Metadata Card Preview
                if (metadata != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ForgeSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ForgePrimaryDim, ForgeBorder)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = metadata?.thumbnailUrl,
                                contentDescription = metadata?.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 80.dp, height = 54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = metadata?.title ?: "",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ForgeTextPrimary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${metadata?.authorName} • ${metadata?.durationFormatted}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ForgeSecondary),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Step 2: Legal Guidance & Source File Requirement
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeWarning.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = ForgeWarning, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Source Authorization Policy",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeWarning
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A YouTube URL alone is never treated as permission to download protected streams or bypass DRM. You must provide an authorized video source file you own or choose a verified licensed test source below.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ForgeTextSecondary,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Step 3: Media Source Selection (Upload or Verified Pre-Licensed Source)
            item {
                Text(
                    text = "2. Authorized Media Source Selection",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Upload Button Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        .testTag("upload_media_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uploadedUri != null) ForgePrimaryDim.copy(alpha = 0.2f) else ForgeSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (uploadedUri != null) listOf(ForgePrimary, ForgePrimary) else listOf(ForgeBorder, ForgeBorder)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ForgePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uploadedUri != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = ForgePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uploadedUri != null) "Uploaded Source Video Attached" else "Upload Video File from Storage",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeTextPrimary
                                )
                            )
                            Text(
                                text = if (uploadedUri != null) "Ready for vertical cropping & subtitle burn-in" else "Select MP4, MOV, or WebM video you own (Photo Picker)",
                                style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "OR Choose a Verified Pre-Licensed Source:",
                    style = MaterialTheme.typography.labelMedium.copy(color = ForgeTextTertiary)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Sample sources carousel
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MediaSourceProvider.VERIFIED_SAMPLE_SOURCES.forEach { sample ->
                        val isSelected = selectedSample?.id == sample.id && uploadedUri == null
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectSampleSource(sample) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) ForgeSecondaryDim.copy(alpha = 0.2f) else ForgeSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    if (isSelected) listOf(ForgeSecondary, ForgeSecondary) else listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = sample.thumbnailUrl,
                                    contentDescription = sample.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp, 44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sample.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForgeTextPrimary
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${sample.category} • ${sample.durationFormatted} • ${sample.licenseNotice}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = ForgeSecondary),
                                        maxLines = 1
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = ForgeSecondary)
                                }
                            }
                        }
                    }
                }

                // Legal Authorization Checkbox
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.isUserAuthorizedCert.value = !isAuthorizedCert },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAuthorizedCert,
                        onCheckedChange = { viewModel.isUserAuthorizedCert.value = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ForgeSecondary,
                            checkmarkColor = ForgeOnSecondary
                        ),
                        modifier = Modifier.testTag("authorization_checkbox")
                    )
                    Text(
                        text = "I certify that I hold copyright ownership or explicit authorization to process this media.",
                        style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextSecondary),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Step 4: Clip Duration & Output Settings
            item {
                Text(
                    text = "3. Output Formatting & AI Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForgeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Target duration chips
                Text(text = "Target Clip Duration:", style = MaterialTheme.typography.labelMedium.copy(color = ForgeTextSecondary))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 45, 60).forEach { sec ->
                        val isSelected = targetDuration == sec
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.targetDurationSec.value = sec },
                            label = { Text("${sec}s Short") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForgePrimary,
                                selectedLabelColor = ForgeOnPrimary,
                                containerColor = ForgeSurface,
                                labelColor = ForgeTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subtitle style selector
                Text(text = "Subtitle Animation Style:", style = MaterialTheme.typography.labelMedium.copy(color = ForgeTextSecondary))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "neon_karaoke" to "Neon Karaoke",
                        "bold_boxed" to "Bold Boxed",
                        "cinematic_outline" to "Cinematic",
                        "arabic_rtl" to "Arabic RTL"
                    ).forEach { (id, label) ->
                        val isSelected = subtitleStyle == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedSubtitleStyle.value = id },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForgeSecondary,
                                selectedLabelColor = ForgeOnSecondary,
                                containerColor = ForgeSurface,
                                labelColor = ForgeTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Narration Toggle
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Voiceover Narration",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeTextPrimary
                                    )
                                )
                                Text(
                                    text = "Mix generated speech with automatic background music ducking",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                                )
                            }
                            Switch(
                                checked = narrationEnabled,
                                onCheckedChange = { viewModel.narrationEnabled.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ForgeSecondary,
                                    checkedTrackColor = ForgeSecondary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.testTag("narration_switch")
                            )
                        }

                        if (narrationEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Voice Character:",
                                style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextTertiary)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Kore", "Fenrir", "Puck", "Aoede").forEach { voice ->
                                    val isSelected = selectedVoice == voice
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectedVoice.value = voice },
                                        label = { Text(voice) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ForgePrimary,
                                            selectedLabelColor = ForgeOnPrimary,
                                            containerColor = ForgeSurfaceVariant,
                                            labelColor = ForgeTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}
