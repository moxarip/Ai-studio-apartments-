package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.media.AuthorizedSampleSource
import com.example.data.media.MediaSourceProvider
import com.example.data.model.*
import com.example.data.network.GeminiApiService
import com.example.data.network.YouTubeMetadataService
import com.example.data.network.YouTubeVideoMetadata
import com.example.data.repository.ShortsForgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    object NewProject : Screen()
    data class ProjectDetail(val projectId: String) : Screen()
    data class ClipReview(val clipId: String) : Screen()
    data class JobStatus(val jobId: String) : Screen()
    data class ChatStudio(val projectId: String) : Screen()
    object ExportHistory : Screen()
    object Settings : Screen()
}

class ShortsForgeViewModel(application: Application) : AndroidViewModel(application) {
    val repository = ShortsForgeRepository(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation history stack for Android BackHandler
    private val backStack = mutableListOf<Screen>(Screen.Dashboard)

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            _currentScreen.value = backStack.last()
            return true
        }
        return false
    }

    // Repository Flows
    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exports: StateFlow<List<ExportEntity>> = repository.allExports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeJobs: StateFlow<List<JobEntity>> = repository.activeJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<JobEntity>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // New Project Form State
    val youtubeUrlInput = MutableStateFlow("")
    val isFetchingYouTubeMeta = MutableStateFlow(false)
    val youtubeMetadata = MutableStateFlow<YouTubeVideoMetadata?>(null)
    val selectedSampleSource = MutableStateFlow<AuthorizedSampleSource?>(MediaSourceProvider.VERIFIED_SAMPLE_SOURCES.first())
    val uploadedMediaUri = MutableStateFlow<String?>(null)
    val isUserAuthorizedCert = MutableStateFlow(false)
    val targetDurationSec = MutableStateFlow(30)
    val selectedSubtitleStyle = MutableStateFlow("neon_karaoke")
    val narrationEnabled = MutableStateFlow(false)
    val selectedVoice = MutableStateFlow("Kore")
    val statusMessage = MutableStateFlow<String?>(null)

    // Active Clip & Project State
    val selectedProjectId = MutableStateFlow<String?>(null)
    val selectedClipId = MutableStateFlow<String?>(null)
    val activeJobId = MutableStateFlow<String?>(null)

    // Chat State
    val chatInputText = MutableStateFlow("")
    val isSendingChatMessage = MutableStateFlow(false)

    fun validateAndFetchYouTubeMetadata(url: String) {
        youtubeUrlInput.value = url
        if (url.isBlank()) return

        viewModelScope.launch {
            isFetchingYouTubeMeta.value = true
            val settings = userSettings.value
            val res = YouTubeMetadataService.fetchMetadata(url, settings?.youtubeApiKey)
            res.onSuccess { meta ->
                youtubeMetadata.value = meta
                statusMessage.value = "Retrieved official metadata: ${meta.title}"
            }.onFailure { err ->
                statusMessage.value = "Notice: ${err.message}"
            }
            isFetchingYouTubeMeta.value = false
        }
    }

    fun selectSampleSource(source: AuthorizedSampleSource) {
        selectedSampleSource.value = source
        uploadedMediaUri.value = null
        isUserAuthorizedCert.value = true
    }

    fun onMediaUploaded(uriString: String) {
        uploadedMediaUri.value = uriString
        selectedSampleSource.value = null
        isUserAuthorizedCert.value = true
        statusMessage.value = "Authorized media uploaded successfully."
    }

    fun createAndStartProject() {
        viewModelScope.launch {
            val meta = youtubeMetadata.value
            val sample = selectedSampleSource.value
            val uploaded = uploadedMediaUri.value

            val title = meta?.title ?: sample?.title ?: "ShortsForge Project #${(100..999).random()}"
            val author = meta?.authorName ?: sample?.author ?: "Creator"
            val thumb = meta?.thumbnailUrl ?: sample?.thumbnailUrl ?: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600"
            val dur = meta?.durationSeconds ?: sample?.durationSeconds ?: 480.0
            val mediaUri = uploaded ?: sample?.id ?: "sample_ai_keynote"

            val project = repository.createProject(
                title = title,
                sourceUrl = youtubeUrlInput.value.ifBlank { "https://youtube.com/watch?v=sample" },
                author = author,
                thumbnailUrl = thumb,
                durationSeconds = dur,
                authorizedMediaUri = mediaUri,
                isAuthorized = isUserAuthorizedCert.value,
                targetDurationSec = targetDurationSec.value,
                subtitleStyle = selectedSubtitleStyle.value,
                narrationEnabled = narrationEnabled.value,
                narrationVoice = selectedVoice.value
            )

            selectedProjectId.value = project.id

            // Trigger AI highlight detection job
            val sampleTranscript = sample?.sampleTranscript
            val jobId = repository.startAnalysisJob(project.id, sampleTranscript)
            activeJobId.value = jobId
            navigateTo(Screen.JobStatus(jobId))
        }
    }

    fun retryJob(job: JobEntity) {
        viewModelScope.launch {
            if (job.clipId != null) {
                val newJobId = repository.startClipRenderJob(job.clipId)
                activeJobId.value = newJobId
                navigateTo(Screen.JobStatus(newJobId))
            } else {
                val newJobId = repository.startAnalysisJob(job.projectId)
                activeJobId.value = newJobId
                navigateTo(Screen.JobStatus(newJobId))
            }
        }
    }

    fun renderClip(clipId: String) {
        viewModelScope.launch {
            val jobId = repository.startClipRenderJob(clipId)
            activeJobId.value = jobId
            navigateTo(Screen.JobStatus(jobId))
        }
    }

    fun updateClipDetails(clip: ClipEntity) {
        viewModelScope.launch {
            repository.updateClip(clip)
            statusMessage.value = "Clip updated successfully."
        }
    }

    fun rewriteClipHookWithAi(clip: ClipEntity) {
        viewModelScope.launch {
            val settings = userSettings.value
            val res = GeminiApiService.rewriteViralHook(
                currentTitle = clip.suggestedTitle,
                transcript = clip.editableTranscript.ifBlank { clip.transcriptExcerpt },
                apiKeyOverride = settings?.customGeminiApiKey
            )
            res.onSuccess { (newTitle, newHook) ->
                val updated = clip.copy(
                    suggestedTitle = newTitle,
                    hook = newHook
                )
                repository.updateClip(updated)
                statusMessage.value = "Generated viral hook with Gemini Lite!"
            }
        }
    }

    fun sendChatMessage(projectId: String) {
        val text = chatInputText.value.trim()
        if (text.isBlank()) return
        chatInputText.value = ""

        viewModelScope.launch {
            isSendingChatMessage.value = true
            repository.sendStudioChatMessage(projectId, text)
            isSendingChatMessage.value = false
        }
    }

    fun clearChat(projectId: String) {
        viewModelScope.launch {
            repository.clearChat(projectId)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (selectedProjectId.value == projectId) {
                selectedProjectId.value = null
            }
            if (_currentScreen.value is Screen.ProjectDetail) {
                navigateTo(Screen.Dashboard)
            }
        }
    }

    fun saveSettings(
        geminiKey: String,
        ytKey: String,
        preferredModel: String,
        trackingEnabled: Boolean
    ) {
        viewModelScope.launch {
            val current = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(
                current.copy(
                    customGeminiApiKey = geminiKey,
                    youtubeApiKey = ytKey,
                    preferredAiModel = preferredModel,
                    subjectTrackingEnabled = trackingEnabled
                )
            )
            statusMessage.value = "Settings saved successfully."
        }
    }
}
