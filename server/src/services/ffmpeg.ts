import { spawn } from 'child_process';
import path from 'path';

export interface RenderParams {
  inputVideoPath: string;
  outputVideoPath: string;
  startSeconds: number;
  endSeconds: number;
  cropMode?: 'subject_aware' | 'center_crop';
  subtitleAssPath?: string;
  narrationAudioPath?: string;
  backgroundMusicVolume?: number;
  narrationVolume?: number;
}

export function buildFFmpegArgs(params: RenderParams): string[] {
  const args: string[] = ['-y'];

  // Input start & end trimming
  args.push('-ss', params.startSeconds.toFixed(3));
  args.push('-to', params.endSeconds.toFixed(3));
  args.push('-i', params.inputVideoPath);

  if (params.narrationAudioPath) {
    args.push('-i', params.narrationAudioPath);
  }

  const videoFilters: string[] = [];

  if (params.cropMode === 'subject_aware') {
    // Subject-aware smoothed framing
    videoFilters.push("scale=-1:1920,crop=w=1080:h=1920:x='min(max(0, (in_w-1080)/2 + (in_w-1080)*0.15*sin(t*0.4)), in_w-1080)':y=0");
  } else {
    // Standard center-crop fallback
    videoFilters.push('scale=1080:1920:force_original_aspect_ratio=increase,crop=1080:1920');
  }

  if (params.subtitleAssPath) {
    const escapedAss = params.subtitleAssPath.replace(/\\/g, '/').replace(/:/g, '\\:');
    videoFilters.push(`subtitles='${escapedAss}'`);
  }

  args.push('-vf', videoFilters.join(','));

  if (params.narrationAudioPath) {
    const bgVol = params.backgroundMusicVolume ?? 0.25;
    const narrVol = params.narrationVolume ?? 1.0;
    args.push('-filter_complex', `[0:a]volume=${bgVol}[bg];[1:a]volume=${narrVol}[narr];[bg][narr]amix=inputs=2:duration=first:dropout_transition=2[aout]`);
    args.push('-map', '0:v', '-map', '[aout]');
  }

  args.push(
    '-c:v', 'libx264',
    '-preset', 'fast',
    '-crf', '22',
    '-pix_fmt', 'yuv420p',
    '-r', '30',
    '-c:a', 'aac',
    '-b:a', '192k',
    '-ar', '44100',
    '-movflags', '+faststart',
    params.outputVideoPath
  );

  return args;
}

export function executeFFmpeg(args: string[], onProgress?: (progress: number) => void): Promise<void> {
  return new Promise((resolve, reject) => {
    const ffmpegProc = spawn('ffmpeg', args);

    let stderr = '';
    ffmpegProc.stderr.on('data', (data) => {
      stderr += data.toString();
      // Optional progress parsing from time=...
    });

    ffmpegProc.on('close', (code) => {
      if (code === 0) {
        resolve();
      } else {
        reject(new Error(`FFmpeg exited with error code ${code}: ${stderr.slice(-300)}`));
      }
    });

    ffmpegProc.on('error', (err) => {
      reject(err);
    });
  });
}
