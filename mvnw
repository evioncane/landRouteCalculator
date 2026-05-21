#!/usr/bin/env bash
# Thin Maven wrapper that activates Java 21 from SDKMAN before delegating to mvn.
set -e

# Find the newest Java 21 candidate available in SDKMAN
if [ -d "$HOME/.sdkman/candidates/java" ]; then
  JAVA21=$(ls -d "$HOME/.sdkman/candidates/java/21".*/ 2>/dev/null | sort -V | tail -1)
  if [ -n "$JAVA21" ]; then
    export JAVA_HOME="${JAVA21%/}"
  fi
fi

# Verify we actually have Java 21+
JAVA_MAJOR=$("$JAVA_HOME/bin/java" -version 2>&1 | awk -F '"' 'NR==1{print $2}' | cut -d. -f1)
if [ "${JAVA_MAJOR:-0}" -lt 21 ] 2>/dev/null; then
  echo "ERROR: Java 21 is required. Set JAVA_HOME or install via: sdk install java 21.0.9-tem" >&2
  exit 1
fi

exec mvn "$@"
