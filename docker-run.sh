#!/usr/bin/env bash
# Build the image and run every demo. The only prerequisite is Docker.
set -euo pipefail
cd "$(dirname "$0")"
docker build -t while-ai-demos .
docker run --rm while-ai-demos
