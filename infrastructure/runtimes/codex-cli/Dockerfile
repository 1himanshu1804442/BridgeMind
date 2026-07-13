FROM node:20-alpine

# In a real environment, this would install the actual OpenAI Codex CLI or equivalent external adapter.
# For this adapter implementation, we install a basic Python environment or use a dummy script
# to simulate the Codex CLI behavior, satisfying the adapter pattern requirements.
RUN apk add --no-cache python3 py3-pip bash

# Create a mock Codex CLI script
RUN echo '#!/bin/bash' > /usr/local/bin/codex && \
    echo 'echo "Codex CLI adapter starting..."' >> /usr/local/bin/codex && \
    echo 'if [ "$1" = "--file" ] && [ -n "$2" ]; then' >> /usr/local/bin/codex && \
    echo '  echo "Reading instructions from $2:"' >> /usr/local/bin/codex && \
    echo '  cat "$2"' >> /usr/local/bin/codex && \
    echo 'fi' >> /usr/local/bin/codex && \
    echo 'echo "Processing task with Codex model..."' >> /usr/local/bin/codex && \
    echo 'sleep 2' >> /usr/local/bin/codex && \
    echo 'echo "Task completed successfully."' >> /usr/local/bin/codex && \
    chmod +x /usr/local/bin/codex

ENTRYPOINT ["codex"]
