#!/bin/bash

set -euo pipefail

JDK_PATH="$HOME/.sdkman/candidates/java/21.0.10-zulu/bin/java"
echo "JDK PATH: $JDK_PATH"

SCRIPT_DIR=$(pwd)

CP="$SCRIPT_DIR/build/classes:$SCRIPT_DIR/jars/versions/1.8.9/1.8.9.jar:$SCRIPT_DIR/build/libs/libs"

./gradlew build

cd "$SCRIPT_DIR/jars"

$JDK_PATH "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.dev.heapdump" \
  "-Dalya.dev.resources=$SCRIPT_DIR/src/main/resources" \
  "-Djava.library.path=$SCRIPT_DIR/jars/versions/1.8.9/1.8.9-natives" \
  "-Dminecraft.launcher.brand=minecraft-launcher" \
  "-Dminecraft.launcher.version=3.2.13" \
  "-Dio.netty.transport.noNative=true" \
  -cp "$CP" -Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC \
  -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M \
  "-Dlog4j2.status=OFF" \
  start.Main --gameDir "$SCRIPT_DIR/jars/.minecraft" \
  --assetIndex 1.8 --uuid 0 --userType msa
