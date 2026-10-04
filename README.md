# ShortsForge AI — Full-Stack AI Video-to-Shorts Platform

ShortsForge AI transforms authorized source videos into vertical 9:16 short-form videos (YouTube Shorts, TikTok, and Instagram Reels) powered by Google Gemini video understanding, automated subtitle animation, and an FFmpeg rendering pipeline.

---

## 🌟 Key Capabilities

1. **YouTube Metadata & Authorized Source Verification**
   - Extracts permitted public metadata using the official YouTube Data API & official YouTube oEmbed endpoints.
   - Enforces legal compliance: a YouTube URL alone is only a metadata reference and is **never** used to download protected streams or bypass DRM.
   - Zero-permission Android Photo/Video picker (`PickVisualMedia`) for local authorized video file uploads.
   - Pre-licensed Creative Commons sample source media (Tech Keynote, Founder Podcast, Esports Highlight).

2. **Gemini Pro Highlight Detection & Video Understanding**
   - Uses `gemini-3.1-pro-preview` for complex multi-modal retention curve analysis.
   - Structured JSON output with `startSeconds`, `endSeconds`, `suggestedTitle`, `hook`, `summary`, `rationale`, and `viralScore`.
   - Sub-second title & viral hook rewriting using `gemini-3.1-flash-lite-preview`.
   - Dedicated multi-turn AI Studio Creative Director Copilot powered by `gemini-3.5-flash`.

3. **Vertical 9:16 FFmpeg Video Pipeline**
   - Aspect-ratio-aware scaling and cropping from 16:9 to portrait 1080x1920.
   - Smooth **Smart Subject-Aware Framing** with face tracking simulation and centered crop fallback.
   - Advanced SubStation Alpha (`.ass`) subtitle generator with customizable fonts, colors, safe overlay margins, and karaoke word-level highlighting.
   - Optional AI voice narration mixing with automated background audio ducking.
   - Production argument array generation (`spawn('ffmpeg', args)`) preventing shell injection.

4. **Durable Job State Machine & Progress Tracking**
   - Real stage progression: `validating_source` ➔ `transcribing` ➔ `analyzing_highlights` ➔ `awaiting_user_approval` ➔ `preparing_subtitles` ➔ `generating_narration` ➔ `rendering` ➔ `completed`.
   - Real progress reporting and live worker execution console logs.

5. **Local & Cloud Persistence**
   - Android Client: Powered by Room SQLite database with reactive Kotlin `Flow` and MVVM architecture.
   - Server Backend: PostgreSQL schema for projects, clips, jobs, and exports.

---

## 📱 Android Client Architecture

- **UI Framework:** Jetpack Compose with Material Design 3 Dark Theme.
- **State Management:** MVVM with `ShortsForgeViewModel` and Kotlin coroutines/Flow.
- **Local Persistence:** Room Database (`ShortsForgeDatabase`) with DAOs for Projects, Clips, Jobs, Exports, and Chat Messages.
- **Image & Media Loading:** Coil Compose 2.7.0.
- **Secrets Management:** Injected via `BuildConfig.GEMINI_API_KEY` with user settings override support.

---

## 🚀 Backend & Worker Architecture (`/server`)

- **Runtime:** Node.js (TypeScript) + Express
- **Queue & Worker:** Redis + BullMQ for long-running media rendering
- **Video Tools:** FFmpeg 6+ / FFprobe installed on the container node
- **Validation:** Zod request schema validation
- **Containerization:** Multi-stage Dockerfile and Docker Compose

### Running Backend with Docker Compose

```bash
cd server
cp .env.example .env
# Fill in GEMINI_API_KEY and YOUTUBE_DATA_API_KEY in .env

docker-compose up --build
```

### Running Backend Locally for Development

```bash
cd server
npm install
npm run dev      # Starts Express REST API on http://localhost:4000
npm run worker   # Starts BullMQ video processing worker
npm test         # Executes automated Jest test suite
```

---

## 🔌 Backend REST API Endpoints

- `POST /api/projects`: Create a project with validated YouTube reference and authorized source.
- `GET /api/projects`: List all projects.
- `GET /api/projects/:projectId`: Retrieve project metadata, authorization status, and proposed clips.
- `POST /api/projects/:projectId/analyze`: Queue Gemini highlight detection on authorized media.
- `GET /api/jobs/:jobId`: Query real-time job status, stage, progress percentage, and terminal logs.
- `PATCH /api/clips/:clipId`: Update clip timestamps, suggested title, transcript, or crop mode.
- `POST /api/clips/:clipId/render`: Queue a 9:16 portrait FFmpeg render job with burned-in subtitles.
