# =============================================================================
# HivePilot Frontend - Multi-stage Dockerfile
# Stage 1: Build the Vite/React app with Node.js
# Stage 2: Serve static files with nginx
# =============================================================================

# ---------------------
# Stage 1: Build
# ---------------------
FROM node:20-alpine AS build

WORKDIR /app

# Copy package manifests first for Docker layer caching.
# npm ci uses the lockfile for deterministic, reproducible installs
# (unlike npm install which may update the lockfile).
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the production bundle (Vite outputs to /app/dist by default)
RUN npm run build

# ---------------------
# Stage 2: Runtime
# ---------------------
FROM nginx:alpine

# Remove the default nginx config so our custom one takes effect
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx configuration that handles SPA routing and API proxying
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built static files from the build stage
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# nginx:alpine image already runs nginx in the foreground via its own CMD,
# but we set it explicitly for clarity.
CMD ["nginx", "-g", "daemon off;"]
