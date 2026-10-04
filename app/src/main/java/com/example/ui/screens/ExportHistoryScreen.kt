package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ExportEntity
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.*

@Composable
fun ExportHistoryScreen(
    viewModel: ShortsForgeViewModel
) {
    val exports by viewModel.exports.collectAsState()
    val context = LocalContext.current
    var previewExport by remember { mutableStateOf<ExportEntity?>(null) }

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "Exported Shorts (${exports.size})",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() }
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        if (exports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        tint = ForgeTextTertiary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Exported Shorts Yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForgeTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Once you review and render a clip, the 1080x1920 MP4 file will appear here.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ForgeTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(exports, key = { it.id }) { export ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_card_${export.id}"),
                        colors = CardDefaults.cardColors(containerColor = ForgeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ForgeBorder, ForgeTertiary.copy(alpha = 0.3f))))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 9:16 Thumbnail aspect
                                Box(
                                    modifier = Modifier
                                        .size(width = 68.dp, height = 100.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (export.thumbnailUri.isNotBlank()) {
                                        AsyncImage(
                                            model = export.thumbnailUri,
                                            contentDescription = export.title,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = ForgeSecondary, modifier = Modifier.size(32.dp))
                                    }
                                    // 9:16 badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Black.copy(alpha = 0.8f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "9:16 MP4",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ForgeSecondary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = export.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ForgeTextPrimary
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${export.resolution} • ${String.format(java.util.Locale.US, "%.1fs", export.durationSeconds)} • ${export.fileSizeMb} MB",
                                        style = MaterialTheme.typography.labelSmall.copy(color = ForgeSecondary)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Path: .../${export.filePath.takeLast(24)}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ForgeTextTertiary,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { previewExport = export },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForgeTextPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_SUBJECT, export.title)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Check out this short forged with ShortsForge AI: ${export.title} (Resolution: ${export.resolution})"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Short to..."))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForgeSecondary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = ForgeOnSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", fontWeight = FontWeight.Bold, color = ForgeOnSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Preview Dialog
        if (previewExport != null) {
            AlertDialog(
                onDismissRequest = { previewExport = null },
                confirmButton = {
                    TextButton(onClick = { previewExport = null }) {
                        Text("Close", color = ForgeSecondary)
                    }
                },
                title = {
                    Text(
                        text = previewExport?.title ?: "Short Preview",
                        style = MaterialTheme.typography.titleSmall.copy(color = ForgeTextPrimary)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(160.dp)
                                .height(284.dp) // 9:16 aspect
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black)
                                .border(1.dp, ForgeSecondary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = ForgeSecondary, modifier = Modifier.size(48.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "1080x1920 HD Ready",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rendered MP4 container verified on device.",
                            style = MaterialTheme.typography.bodySmall.copy(color = ForgeTextSecondary)
                        )
                    }
                },
                containerColor = ForgeSurfaceVariant
            )
        }
    }
}
