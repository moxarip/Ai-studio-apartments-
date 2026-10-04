package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceUrl: String = "",
    val sourcePlatform: String = "youtube",
    val channelOrAuthor: String = "",
    val thumbnailUri: String = "",
    val durationSeconds: Double = 0.0,
    val authorizedMediaUri: String = "",
    val isAuthorized: Boolean = false,
    val authorizationNotes: String = "",
    val targetDurationSec: Int = 30,
    val targetAspectRatio: String = "9:16",
    val language: String = "en",
    val subtitleStyle: String = "neon_karaoke",
    val narrationEnabled: Boolean = false,
    val narrationVoice: String = "Kore",
    val status: String = "draft", // draft, processing, ready, failed
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "clips")
data class ClipEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val suggestedTitle: String,
    val hook: String,
    val summary: String,
    val rationale: String,
    val transcriptExcerpt: String,
    val confidence: Double = 0.9,
    val editableTranscript: String = "",
    val narrationScript: String = "",
    val subtitleStyle: String = "neon_karaoke",
    val subtitleColorHex: String = "#FACC15",
    val cropMode: String = "subject_aware", // subject_aware, center_crop
    val faceTrackStatus: String = "calibrated", // calibrated, center_fallback
    val exportStatus: String = "pending", // pending, rendering, exported, failed
    val exportedFilePath: String = "",
    val viralScore: Int = 88,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val clipId: String? = null,
    val stage: String = "validating_source",
    // validating_source, transcribing, analyzing_highlights, awaiting_user_approval,
    // preparing_subtitles, generating_narration, rendering, uploading_output, completed, failed
    val progress: Int = 0,
    val logMessages: String = "",
    val errorMessage: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "exports")
data class ExportEntity(
    @PrimaryKey val id: String,
    val clipId: String,
    val projectId: String,
    val title: String,
    val filePath: String,
    val thumbnailUri: String = "",
    val durationSeconds: Double,
    val resolution: String = "1080x1920",
    val fileSizeMb: Double = 14.2,
    val exportedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val projectId: String = "global",
    val role: String, // user, assistant, system
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val customGeminiApiKey: String = "",
    val youtubeApiKey: String = "",
    val defaultSubtitleStyle: String = "neon_karaoke",
    val defaultVoice: String = "Kore",
    val subjectTrackingEnabled: Boolean = true,
    val preferredAiModel: String = "gemini-3.1-pro-preview"
)
