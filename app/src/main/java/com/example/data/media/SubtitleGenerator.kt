package com.example.data.media

import java.io.File
import java.util.Locale

data class SubtitleCue(
    val startSeconds: Double,
    val endSeconds: Double,
    val text: String,
    val highlightedWordIndex: Int = -1
)

object SubtitleGenerator {

    /**
     * Generates an ASS (Advanced SubStation Alpha) subtitle file.
     * ASS allows rich styling: custom font, outline, shadow, alignment, margin safe zones,
     * and karaoke color highlighting.
     */
    fun generateAssSubtitleFile(
        outputFile: File,
        cues: List<SubtitleCue>,
        style: String = "neon_karaoke",
        isRtl: Boolean = false
    ): File {
        val (fontName, primaryColor, outlineColor, backColor, borderStyle) = when (style) {
            "neon_karaoke" -> Quintuple("Arial", "&H0000FFFF", "&H00800080", "&H80000000", 1) // Yellow text with purple border
            "bold_boxed" -> Quintuple("Arial", "&H00FFFFFF", "&H00000000", "&HA0000000", 3) // White with solid black box
            "cinematic_outline" -> Quintuple("Arial", "&H00FFFFFF", "&H00000000", "&H00000000", 1) // White with thick black stroke
            "arabic_rtl" -> Quintuple("Arial", "&H00FBBF24", "&H00000000", "&H80000000", 1)
            else -> Quintuple("Arial", "&H0000FFFF", "&H00000000", "&H80000000", 1)
        }

        // Alignments: 2 = Bottom-Center. Vertical margins are 320 to avoid TikTok/Shorts bottom controls and safe areas
        val assContent = buildString {
            appendLine("[Script Info]")
            appendLine("Title: ShortsForge AI Vertical Subtitles")
            appendLine("ScriptType: v4.00+")
            appendLine("WrapStyle: 0")
            appendLine("ScaledBorderAndShadow: yes")
            appendLine("PlayResX: 1080")
            appendLine("PlayResY: 1920")
            appendLine()
            appendLine("[V4+ Styles]")
            appendLine("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding")
            appendLine("Style: Default,$fontName,72,$primaryColor,&H000000FF,$outlineColor,$backColor,-1,0,0,0,100,100,0,0,$borderStyle,4,2,2,80,80,340,1")
            appendLine()
            appendLine("[Events]")
            appendLine("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text")

            for (cue in cues) {
                val startFormatted = formatAssTime(cue.startSeconds)
                val endFormatted = formatAssTime(cue.endSeconds)
                val escapedText = escapeAssText(cue.text)

                // If word highlight is enabled, wrap words with karaoke tags or bold color tag
                val styledText = if (style == "neon_karaoke" && cue.highlightedWordIndex >= 0) {
                    val words = escapedText.split(" ")
                    words.mapIndexed { idx, word ->
                        if (idx == cue.highlightedWordIndex) "{\\c&H0000FF00\\b1}$word{\\r}" else word
                    }.joinToString(" ")
                } else {
                    escapedText
                }

                appendLine("Dialogue: 0,$startFormatted,$endFormatted,Default,,0,0,0,,${if (isRtl) "{\\fe1}$styledText" else styledText}")
            }
        }

        outputFile.writeText(assContent, Charsets.UTF_8)
        return outputFile
    }

    /**
     * Splits full transcript text into timed subtitle cues suitable for vertical shorts
     * (2 to 6 words per screen, high readability, fast pacing).
     */
    fun generateCuesFromTranscript(
        transcript: String,
        startSec: Double,
        endSec: Double
    ): List<SubtitleCue> {
        val rawWords = transcript.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (rawWords.isEmpty()) {
            return listOf(
                SubtitleCue(startSec, endSec, "🔥 Watch this video carefully!")
            )
        }

        val totalDuration = (endSec - startSec).coerceAtLeast(3.0)
        val wordChunks = rawWords.chunked(4) // 4 words per cue for rapid short-form reading
        val timePerChunk = totalDuration / wordChunks.size.toDouble()

        val cues = mutableListOf<SubtitleCue>()
        var currentStart = startSec

        for ((index, chunk) in wordChunks.withIndex()) {
            val chunkEnd = (currentStart + timePerChunk).coerceAtMost(endSec)
            cues.add(
                SubtitleCue(
                    startSeconds = currentStart,
                    endSeconds = chunkEnd,
                    text = chunk.joinToString(" ").uppercase(),
                    highlightedWordIndex = index % chunk.size
                )
            )
            currentStart = chunkEnd
        }

        return cues
    }

    private fun formatAssTime(seconds: Double): String {
        val totalMs = (seconds * 1000).toLong()
        val h = totalMs / 3600000
        val m = (totalMs % 3600000) / 60000
        val s = (totalMs % 60000) / 1000
        val cs = (totalMs % 1000) / 10 // centiseconds
        return String.format(Locale.US, "%d:%02d:%02d.%02d", h, m, s, cs)
    }

    private fun escapeAssText(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("\n", "\\N")
    }

    private data class Quintuple<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )
}
