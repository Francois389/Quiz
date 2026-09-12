#!/bin/sh
set -eu

CONFIG_FILE="/usr/share/nginx/html/config.js"

cat > "$CONFIG_FILE" <<EOF
window.APP_CONFIG = {
  repoUrl: "${REPO_URL:-}"
};
EOF
