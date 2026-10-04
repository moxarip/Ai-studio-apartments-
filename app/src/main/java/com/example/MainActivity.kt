package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Screen
import com.example.ui.ShortsForgeViewModel
import com.example.ui.screens.*
import com.example.ui.theme.ShortsForgeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShortsForgeTheme {
                val viewModel: ShortsForgeViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val statusMessage by viewModel.statusMessage.collectAsState()

                LaunchedEffect(statusMessage) {
                    statusMessage?.let { msg ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(msg)
                            viewModel.statusMessage.value = null
                        }
                    }
                }

                // Mandatory BackHandler for secondary screens
                BackHandler(enabled = currentScreen !is Screen.Dashboard) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    // Apply outer padding if needed or let screens handle edge-to-edge
                    when (val screen = currentScreen) {
                        is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                        is Screen.NewProject -> NewProjectScreen(viewModel = viewModel)
                        is Screen.ProjectDetail -> ProjectDetailScreen(
                            projectId = screen.projectId,
                            viewModel = viewModel
                        )
                        is Screen.ClipReview -> ClipReviewScreen(
                            clipId = screen.clipId,
                            viewModel = viewModel
                        )
                        is Screen.JobStatus -> JobStatusScreen(
                            jobId = screen.jobId,
                            viewModel = viewModel
                        )
                        is Screen.ChatStudio -> ChatStudioScreen(
                            projectId = screen.projectId,
                            viewModel = viewModel
                        )
                        is Screen.ExportHistory -> ExportHistoryScreen(viewModel = viewModel)
                        is Screen.Settings -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
