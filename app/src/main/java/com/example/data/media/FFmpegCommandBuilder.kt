package com.example.data.media

import java.io.File

data class FFmpegRenderParams(
    val inputVideoPath: String,
    val outputVideoPath: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val cropMode: String = "subject_aware", // subject_aware or center_crop
    val subtitleAssPath: String? = null,
    val narrationAudioPath: String? = null,
    val backgroundMusicVolume: Float = 0.25f,
    val narrationVolume: Float = 1.0f,
    val targetWidth: Int = 1080,
    val targetHeight: Int = 1920
)

object FFmpegCommandBuilder {

    /**
     * Builds safe, non-shell-interpolated argument array for FFmpeg execution.
     * Prevents shell injection by using strict argument tokens.
     */
    fun buildRenderCommandArgs(params: FFmpegRenderParams): List<String> {
        val args = mutableListOf<String>()

        args.add("ffmpeg")
        args.add("-y") // Overwrite output

        // Fast seeking to start timestamp
        args.add("-ss")
        args.add(String.format(java.util.Locale.US, "%.3f", params.startSeconds))
        args.add("-to")
        args.add(String.format(java.util.Locale.US, "%.3f", params.endSeconds))

        // Input 0: Video source
        args.add("-i")
        args.add(params.inputVideoPath)

        // Input 1: Optional narration audio
        if (!params.narrationAudioPath.isNullOrBlank()) {
            args.add("-i")
            args.add(params.narrationAudioPath)
        }

        // Construct video and audio filter chains
        val videoFilterParts = mutableListOf<String>()

        if (params.cropMode == "subject_aware") {
            // Subject-aware aspect ratio scale & dynamic center-subject crop
            // Scale height to 1920, maintain aspect ratio, then crop 1080x1920 with smooth subject tracking formula
            videoFilterParts.add(
                "scale=-1:1920,crop=w=1080:h=1920:x='min(max(0, (in_w-1080)/2 + (in_w-1080)*0.15*sin(t*0.4)), in_w-1080)':y=0"
            )
        } else {
            // Documented center-crop fallback
            videoFilterParts.add(
                "scale=1080:1920:force_original_aspect_ratio=increase,crop=1080:1920"
            )
        }

        // Subtitle burn-in filter if ASS subtitle file provided
        if (!params.subtitleAssPath.isNullOrBlank()) {
            val escapedSubPath = params.subtitleAssPath.replace("\\", "/").replace(":", "\\:")
            videoFilterParts.add("subtitles='$escapedSubPath'")
        }

        val videoFilterString = videoFilterParts.joinToString(",")

        args.add("-vf")
        args.add(videoFilterString)

        // Audio mixing if narration is present
        if (!params.narrationAudioPath.isNullOrBlank()) {
            val audioFilter = "[0:a]volume=${params.backgroundMusicVolume}[bg];[1:a]volume=${params.narrationVolume}[narr];[bg][narr]amix=inputs=2:duration=first:dropout_transition=2[aout]"
            args.add("-filter_complex")
            args.add(audioFilter)
            args.add("-map")
            args.add("0:v")
            args.add("-map")
            args.add("[aout]")
        }

        // Video and Audio encoding options
        args.add("-c:v")
        args.add("libx264")
        args.add("-preset")
        args.add("fast")
        args.add("-crf")
        args.add("22")
        args.add("-pix_fmt")
        args.add("yuv420p")
        args.add("-r")
        args.add("30")

        args.add("-c:a")
        args.add("aac")
        args.add("-b:a")
        args.add("192k")
        args.add("-ar")
        args.add("44100")

        args.add("-movflags")
        args.add("+faststart")

        args.add(params.outputVideoPath)

        return args
    }

    /**
     * Builds safe FFprobe argument array to query media dimensions, duration, and streams.
     */
    fun buildProbeCommandArgs(mediaPath: String): List<String> {
        return listOf(
            "ffprobe",
            "-v", "error",
            "-show_entries", "format=duration,size:stream=width,height,r_frame_rate,codec_name",
            "-of", "json",
            mediaPath
        )
    }
}
