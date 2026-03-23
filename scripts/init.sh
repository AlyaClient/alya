#!/bin/bash

set -euo pipefail

if ! command -v mvn >/dev/null 2>&1; then
  echo "'mvn' could not be found! Please install 'maven' and Java 21 (Temurin)!"
  exit 1
fi

mvn wrapper:wrapper

./mvnw package
