#!/bin/bash

set -euo pipefail

if ! command -v mvn >/dev/null 2>&1; then
  echo "'mvn' could not be found! Please install 'maven' and Java 21 (Temurin)!"
  exit 1
fi

mvn wrapper:wrapper

LWJGL_JAR="$HOME/.m2/repository/org/lwjgl/lwjgl/lwjgl/2.9.3/lwjgl-2.9.3.jar"
if [ -f "$LWJGL_JAR" ]; then
  TMPDIR=$(mktemp -d)
  unzip -q "$LWJGL_JAR" -d "$TMPDIR"
  sed -i '/^Sealed:.*/d' "$TMPDIR/META-INF/MANIFEST.MF"
  jar cf "$LWJGL_JAR" -C "$TMPDIR" .
  rm -rf "$TMPDIR"
  echo "Unsealed LWJGL jar for IDE compatibility"
fi

./mvnw package
