#!/usr/bin/env sh

set -eu

if [ "${1:-}" = "" ]; then
    echo "Usage: $0 /absolute/path/to/app-executable"
    exit 1
fi

APP_EXECUTABLE=$1
DESKTOP_FILE="${HOME}/.local/share/applications/demo03.desktop"

mkdir -p "$(dirname "${DESKTOP_FILE}")"

cat > "${DESKTOP_FILE}" <<EOF
[Desktop Entry]
Type=Application
Name=Demo03
Exec=${APP_EXECUTABLE} %u
Terminal=false
MimeType=x-scheme-handler/demo03;
Categories=Utility;
EOF

chmod 644 "${DESKTOP_FILE}"
xdg-mime default demo03.desktop x-scheme-handler/demo03

echo "Registered demo03:// handler with ${DESKTOP_FILE}"
