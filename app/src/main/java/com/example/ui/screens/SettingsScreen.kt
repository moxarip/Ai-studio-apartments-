package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.network.GeminiApiService
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: ShortsForgeViewModel
) {
    val settingsState by viewModel.userSettings.collectAsState()

    var customKey by remember(settingsState?.customGeminiApiKey) {
        mutableStateOf(settingsState?.customGeminiApiKey ?: "")
    }
    var youtubeKey by remember(settingsState?.youtubeApiKey) {
        mutableStateOf(settingsState?.youtubeApiKey ?: "")
    }
    var preferredModel by remember(settingsState?.preferredAiModel) {
        mutableStateOf(settingsState?.preferredAiModel ?: "gemini-3.1-pro-preview")
    }
    var trackingEnabled by remember(settingsState?.subjectTrackingEnabled) {
        mutableStateOf(settingsState?.subjectTrackingEnabled ?: true)
    }

    val resolvedKey = GeminiApiService.getResolvedApiKey(customKey)
    val isKeyConfigured = resolvedKey.isNotBlank()

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "Settings & Configuration",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() }
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
                Spacer(modifier = Modifier.height(4.dp))
                // Gemini API Status Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isKeyConfigured) listOf(ForgeBorder, ForgeSuccess.copy(alpha = 0.4f))
                            else listOf(ForgeBorder, ForgeWarning.copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isKeyConfigured) ForgeSuccess else ForgeWarning)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isKeyConfigured) "Gemini Cloud API Connected" else "API Key Needed for Live Inference",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isKeyConfigured) ForgeSuccess else ForgeWarning
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isKeyConfigured) {
                                "Active Key: ...${resolvedKey.takeLast(6)}. Connected to Google Generative AI endpoints."
                            } else {
                                "Configure your Gemini API key in the Secrets panel in AI Studio or paste an override key below."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customKey,
                            onValueChange = { customKey = it },
                            label = { Text("Custom Gemini API Key Override", color = ForgeTextSecondary) },
                            placeholder = { Text("AIzaSy...", color = ForgeTextTertiary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgePrimary,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = ForgeTextPrimary,
                                unfocusedTextColor = ForgeTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("custom_gemini_key_input")
                        )
                    }
                }
            }

            // AI Model Configuration
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Configured AI Models",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ModelItemRow(
                            task = "Video Understanding & Highlights",
                            model = "gemini-3.1-pro-preview",
                            description = "Deep temporal reasoning, viral hook extraction & structured JSON clips"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModelItemRow(
                            task = "AI Copilot & Script Refinement",
                            model = "gemini-3.5-flash",
                            description = "Low-latency multi-turn video strategist chat & transcript polish"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModelItemRow(
                            task = "Instant Hook & Title Rewrites",
                            model = "gemini-3.1-flash-lite-preview",
                            description = "Sub-second viral hook generation and caption refinement"
                        )
                    }
                }
            }

            // YouTube Data API Configuration
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "YouTube Data API v3 (Optional)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Public oEmbed metadata is used by default. Adding an official YouTube Data API key enables high-resolution thumbnails and detailed ISO durations.",
                            style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = youtubeKey,
                            onValueChange = { youtubeKey = it },
                            label = { Text("YouTube Data API Key", color = ForgeTextSecondary) },
                            placeholder = { Text("AIzaSy...", color = ForgeTextTertiary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgePrimary,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = ForgeTextPrimary,
                                unfocusedTextColor = ForgeTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("youtube_api_key_input")
                        )
                    }
                }
            }

            // Media Processing & Subject Tracking
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeBorder.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Subject-Aware Cropping (Face Tracking)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeTextPrimary
                                    )
                                )
                                Text(
                                    text = "Generates smoothed dynamic crop expressions instead of static center crops",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                                )
                            }
                            Switch(
                                checked = trackingEnabled,
                                onCheckedChange = { trackingEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = ForgePrimary)
                            )
                        }
                    }
                }
            }

            // Save Settings Button
            item {
                Button(
                    onClick = {
                        viewModel.saveSettings(
                            geminiKey = customKey,
                            ytKey = youtubeKey,
                            preferredModel = preferredModel,
                            trackingEnabled = trackingEnabled
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeSecondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_settings_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = ForgeOnSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings", fontWeight = FontWeight.Bold, color = ForgeOnSecondary)
                }
            }

            // Architecture & Privacy Notice
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ForgeSurfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Privacy & Fair Use Compliance",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForgeTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ShortsForge enforces strict copyright and authorized-use policies. API keys are managed securely and never logged. Media processing conforms to Google Play Developer policies.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ForgeTextSecondary,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ModelItemRow(
    task: String,
    model: String,
    description: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = task, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ForgeTextPrimary))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ForgeSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = model,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ForgeSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )
            }
        }
        Text(text = description, style = MaterialTheme.typography.labelSmall.copy(color = ForgeTextTertiary))
    }
}
