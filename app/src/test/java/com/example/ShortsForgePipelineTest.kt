package com.example

import com.example.data.media.FFmpegCommandBuilder
import com.example.data.media.FFmpegRenderParams
import com.example.data.media.SubtitleCue
import com.example.data.media.SubtitleGenerator
import com.example.data.network.YouTubeMetadataService
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ShortsForgePipelineTest {

    @Test
    fun testYouTubeUrlValidationAndIdExtraction() {
        val standardUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        val shortUrl = "https://youtu.be/dQw4w9WgXcQ"
        val shortsUrl = "https://www.youtube.com/shorts/dQw4w9WgXcQ"
        val invalidUrl = "https://example.com/video"

        assertTrue(YouTubeMetadataService.isValidYouTubeUrl(standardUrl))
        assertEquals("dQw4w9WgXcQ", YouTubeMetadataService.extractVideoId(standardUrl))

        assertTrue(YouTubeMetadataService.isValidYouTubeUrl(shortUrl))
        assertEquals("dQw4w9WgXcQ", YouTubeMetadataService.extractVideoId(shortUrl))

        assertTrue(YouTubeMetadataService.isValidYouTubeUrl(shortsUrl))
        assertEquals("dQw4w9WgXcQ", YouTubeMetadataService.extractVideoId(shortsUrl))

        assertFalse(YouTubeMetadataService.isValidYouTubeUrl(invalidUrl))
        assertNull(YouTubeMetadataService.extractVideoId(invalidUrl))
    }

    @Test
    fun testFFmpegCommandBuilderArguments() {
        val params = FFmpegRenderParams(
            inputVideoPath = "/path/to/source.mp4",
            outputVideoPath = "/path/to/output_9_16.mp4",
            startSeconds = 12.5,
            endSeconds = 42.5,
            cropMode = "subject_aware",
            subtitleAssPath = "/path/to/subs.ass",
            narrationAudioPath = "/path/to/voice.aac"
        )

        val args = FFmpegCommandBuilder.buildRenderCommandArgs(params)

        assertTrue(args.contains("ffmpeg"))
        assertTrue(args.contains("-ss"))
        assertTrue(args.contains("12.500"))
        assertTrue(args.contains("-to"))
        assertTrue(args.contains("42.500"))
        assertTrue(args.contains("/path/to/source.mp4"))
        assertTrue(args.contains("/path/to/voice.aac"))

        val vfIndex = args.indexOf("-vf")
        assertTrue(vfIndex != -1)
        val vfString = args[vfIndex + 1]
        assertTrue(vfString.contains("crop="))
        assertTrue(vfString.contains("subtitles="))

        assertEquals("/path/to/output_9_16.mp4", args.last())
    }

    @Test
    fun testSubtitleGeneration() {
        val transcript = "The biggest myth in software engineering is that 10x engineers write more code."
        val cues = SubtitleGenerator.generateCuesFromTranscript(transcript, 10.0, 20.0)

        assertTrue(cues.isNotEmpty())
        assertEquals(10.0, cues.first().startSeconds, 0.001)
        assertTrue(cues.last().endSeconds <= 20.001)

        val tempFile = File.createTempFile("test_subs", ".ass")
        SubtitleGenerator.generateAssSubtitleFile(tempFile, cues, "neon_karaoke")

        assertTrue(tempFile.exists())
        val text = tempFile.readText()
        assertTrue(text.contains("[Script Info]"))
        assertTrue(text.contains("PlayResX: 1080"))
        assertTrue(text.contains("PlayResY: 1920"))
        assertTrue(text.contains("Dialogue:"))
        tempFile.delete()
    }
}
