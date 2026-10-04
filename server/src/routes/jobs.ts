import { Router, Request, Response } from 'express';
import { jobs } from './projects';

export const jobsRouter = Router();

// GET /api/jobs/:jobId
jobsRouter.get('/:jobId', (req: Request, res: Response) => {
  const job = jobs.get(req.params.jobId);
  if (!job) {
    return res.status(404).json({ error: 'Job not found' });
  }
  return res.json({ job });
});
