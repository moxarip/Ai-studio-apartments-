import { Router, Request, Response } from 'express';
import { CreateProjectSchema } from '../schemas/project';
import { getYouTubeMetadata, isValidYouTubeUrl } from '../services/youtube';
import { detectVideoHighlights } from '../services/gemini';

export const projectsRouter = Router();

// In-memory / DB mock repository
const projects: Map<string, any> = new Map();
const clips: Map<string, any[]> = new Map();
const jobs: Map<string, any> = new Map();

// POST /api/projects
projectsRouter.post('/', async (req: Request, res: Response) => {
  try {
    const parseResult = CreateProjectSchema.safeParse(req.body);
    if (!parseResult.success) {
      return res.status(400).json({ error: 'Validation failed', details: parseResult.error.format() });
    }

    const data = parseResult.data;
    if (!isValidYouTubeUrl(data.sourceUrl)) {
      return res.status(400).json({ error: 'Invalid YouTube source URL format.' });
    }

    const projectId = 'proj_' + Math.random().toString(36).substring(2, 9);
    const newProject = {
      id: projectId,
      ...data,
      status: 'draft',
      createdAt: new Date().toISOString()
    };

    projects.set(projectId, newProject);
    return res.status(201).json({ success: true, project: newProject });
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Server error creating project.' });
  }
});

// GET /api/projects
projectsRouter.get('/', (req: Request, res: Response) => {
  return res.json({ projects: Array.from(projects.values()) });
});

// GET /api/projects/:projectId
projectsRouter.get('/:projectId', (req: Request, res: Response) => {
  const project = projects.get(req.params.projectId);
  if (!project) {
    return res.status(404).json({ error: 'Project not found' });
  }
  const projectClips = clips.get(req.params.projectId) || [];
  return res.json({ project, clips: projectClips });
});

// POST /api/projects/:projectId/analyze
projectsRouter.post('/:projectId/analyze', async (req: Request, res: Response) => {
  const project = projects.get(req.params.projectId);
  if (!project) {
    return res.status(404).json({ error: 'Project not found' });
  }

  const jobId = 'job_' + Math.random().toString(36).substring(2, 9);
  const newJob = {
    id: jobId,
    projectId: project.id,
    stage: 'analyzing_highlights',
    progress: 50,
    startedAt: new Date().toISOString()
  };
  jobs.set(jobId, newJob);

  // Run analysis asynchronously
  (async () => {
    try {
      const generated = await detectVideoHighlights(
        project.title,
        project.durationSeconds,
        req.body.transcript || 'AI video workflow automation presentation.',
        project.targetDurationSec
      );

      const clipEntities = generated.map((c, idx) => ({
        id: `clip_${project.id}_${idx + 1}`,
        projectId: project.id,
        ...c,
        exportStatus: 'pending'
      }));

      clips.set(project.id, clipEntities);
      project.status = 'ready';
      newJob.stage = 'completed';
      newJob.progress = 100;
    } catch (e: any) {
      newJob.stage = 'failed';
      newJob.errorMessage = e.message;
    }
  })();

  return res.status(202).json({ success: true, jobId, message: 'Highlight detection job queued.' });
});

export { projects, clips, jobs };
