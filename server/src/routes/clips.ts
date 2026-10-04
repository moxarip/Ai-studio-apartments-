import { Router, Request, Response } from 'express';
import { UpdateClipSchema } from '../schemas/project';
import { clips, jobs } from './projects';
import { buildFFmpegArgs } from '../services/ffmpeg';

export const clipsRouter = Router();

// PATCH /api/clips/:clipId
clipsRouter.patch('/:clipId', (req: Request, res: Response) => {
  const parseResult = UpdateClipSchema.safeParse(req.body);
  if (!parseResult.success) {
    return res.status(400).json({ error: 'Validation failed', details: parseResult.error.format() });
  }

  const { clipId } = req.params;
  for (const [projId, clipList] of clips.entries()) {
    const idx = clipList.findIndex(c => c.id === clipId);
    if (idx !== -1) {
      clipList[idx] = { ...clipList[idx], ...parseResult.data };
      return res.json({ success: true, clip: clipList[idx] });
    }
  }

  return res.status(404).json({ error: 'Clip not found' });
});

// POST /api/clips/:clipId/render
clipsRouter.post('/:clipId/render', (req: Request, res: Response) => {
  const { clipId } = req.params;
  let targetClip: any = null;

  for (const [projId, clipList] of clips.entries()) {
    const found = clipList.find(c => c.id === clipId);
    if (found) {
      targetClip = found;
      break;
    }
  }

  if (!targetClip) {
    return res.status(404).json({ error: 'Clip not found' });
  }

  const jobId = 'render_job_' + Math.random().toString(36).substring(2, 9);
  const renderJob = {
    id: jobId,
    projectId: targetClip.projectId,
    clipId: targetClip.id,
    stage: 'rendering',
    progress: 30,
    startedAt: new Date().toISOString()
  };
  jobs.set(jobId, renderJob);

  // Demonstrate FFmpeg args synthesis
  const ffmpegArgs = buildFFmpegArgs({
    inputVideoPath: 'source.mp4',
    outputVideoPath: `exports/short_${targetClip.id}.mp4`,
    startSeconds: targetClip.startSeconds,
    endSeconds: targetClip.endSeconds,
    cropMode: targetClip.cropMode || 'subject_aware'
  });

  return res.status(202).json({
    success: true,
    jobId,
    message: 'FFmpeg render job queued',
    ffmpegArgsPreview: ffmpegArgs.slice(0, 10)
  });
});
