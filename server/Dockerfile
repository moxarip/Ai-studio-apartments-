# Multi-stage Dockerfile for ShortsForge AI Node.js Backend & Video Worker
FROM node:20-bullseye-slim AS base

# Install FFmpeg, FFprobe, and required video codecs & fonts
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    fonts-liberation \
    fonts-dejavu-core \
    fontconfig \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy dependency manifests
COPY package*.json ./
RUN npm ci

# Copy source code and build
COPY tsconfig.json ./
COPY src/ ./src/
RUN npm run build

# Runtime stage
FROM node:20-bullseye-slim AS runner

RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    fonts-liberation \
    fonts-dejavu-core \
    fontconfig \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY --from=base /app/dist ./dist

# Create media temp directory
RUN mkdir -p /tmp/shortsforge && chmod 777 /tmp/shortsforge

EXPOSE 4000
ENV NODE_ENV=production
CMD ["node", "dist/index.js"]
