import { z } from 'zod';

export const CreateProjectSchema = z.object({
  sourceUrl: z.string().url('Must be a valid URL'),
  title: z.string().min(1, 'Title is required').max(200),
  author: z.string().default('Unknown Creator'),
  thumbnailUrl: z.string().url().optional(),
  durationSeconds: z.number().positive().max(3600, 'Video duration cannot exceed 60 minutes'),
  authorizedMediaUri: z.string().min(1, 'Authorized media source is required'),
  isAuthorized: z.literal(true, {
    errorMap: () => ({ message: 'You must certify legal authorization to process this media' })
  }),
  targetDurationSec: z.enum(['15', '30', '45', '60']).transform(Number).default('30'),
  targetAspectRatio: z.literal('9:16').default('9:16'),
  subtitleStyle: z.enum(['neon_karaoke', 'bold_boxed', 'cinematic_outline', 'arabic_rtl']).default('neon_karaoke'),
  narrationEnabled: z.boolean().default(false),
  narrationVoice: z.enum(['Kore', 'Fenrir', 'Puck', 'Aoede']).default('Kore')
});

export const UpdateClipSchema = z.object({
  suggestedTitle: z.string().min(1).max(150).optional(),
  startSeconds: z.number().nonnegative().optional(),
  endSeconds: z.number().positive().optional(),
  editableTranscript: z.string().optional(),
  narrationScript: z.string().optional(),
  subtitleStyle: z.string().optional(),
  cropMode: z.enum(['subject_aware', 'center_crop']).optional()
}).refine(data => {
  if (data.startSeconds !== undefined && data.endSeconds !== undefined) {
    return data.endSeconds > data.startSeconds;
  }
  return true;
}, {
  message: 'endSeconds must be strictly greater than startSeconds'
});

export const RenderClipSchema = z.object({
  clipId: z.string().uuid()
});

export type CreateProjectInput = z.infer<typeof CreateProjectSchema>;
export type UpdateClipInput = z.infer<typeof UpdateClipSchema>;
