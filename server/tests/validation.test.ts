import { isValidYouTubeUrl, extractYouTubeId } from '../src/services/youtube';
import { buildFFmpegArgs } from '../src/services/ffmpeg';
import { UpdateClipSchema } from '../src/schemas/project';

describe('ShortsForge AI Validation & Pipeline Tests', () => {

  test('validates standard YouTube URLs and extracts video IDs', () => {
    const validUrl1 = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ';
    const validUrl2 = 'https://youtu.be/dQw4w9WgXcQ';
    const validUrl3 = 'https://www.youtube.com/shorts/dQw4w9WgXcQ';

    expect(isValidYouTubeUrl(validUrl1)).toBe(true);
    expect(extractYouTubeId(validUrl1)).toBe('dQw4w9WgXcQ');

    expect(isValidYouTubeUrl(validUrl2)).toBe(true);
    expect(extractYouTubeId(validUrl2)).toBe('dQw4w9WgXcQ');

    expect(isValidYouTubeUrl(validUrl3)).toBe(true);
    expect(extractYouTubeId(validUrl3)).toBe('dQw4w9WgXcQ');
  });

  test('rejects invalid or arbitrary non-YouTube URLs', () => {
    expect(isValidYouTubeUrl('https://vimeo.com/123456')).toBe(false);
    expect(isValidYouTubeUrl('https://google.com')).toBe(false);
    expect(isValidYouTubeUrl('not_a_url')).toBe(false);
  });

  test('validates clip start and end timestamps ordering', () => {
    const validClip = {
      startSeconds: 15.0,
      endSeconds: 45.0,
      suggestedTitle: 'Valid Clip'
    };
    expect(UpdateClipSchema.safeParse(validClip).success).toBe(true);

    const invalidClip = {
      startSeconds: 50.0,
      endSeconds: 20.0,
      suggestedTitle: 'Invalid Clip'
    };
    expect(UpdateClipSchema.safeParse(invalidClip).success).toBe(false);
  });

  test('generates valid FFmpeg argument arrays without shell interpolation', () => {
    const args = buildFFmpegArgs({
      inputVideoPath: '/media/source.mp4',
      outputVideoPath: '/exports/short_1.mp4',
      startSeconds: 10,
      endSeconds: 40,
      cropMode: 'subject_aware',
      subtitleAssPath: '/subs/clip.ass'
    });

    expect(args).toContain('-ss');
    expect(args).toContain('10.000');
    expect(args).toContain('-to');
    expect(args).toContain('40.000');
    expect(args).toContain('-i');
    expect(args).toContain('/media/source.mp4');
    expect(args).toContain('-c:v');
    expect(args).toContain('libx264');
    expect(args[args.length - 1]).toBe('/exports/short_1.mp4');
  });

  test('uses fallback center crop when subject tracking is center_crop', () => {
    const args = buildFFmpegArgs({
      inputVideoPath: '/media/source.mp4',
      outputVideoPath: '/exports/short_2.mp4',
      startSeconds: 0,
      endSeconds: 30,
      cropMode: 'center_crop'
    });

    const vfIndex = args.indexOf('-vf');
    expect(vfIndex).toBeGreaterThan(-1);
    expect(args[vfIndex + 1]).toContain('crop=1080:1920');
  });
});
