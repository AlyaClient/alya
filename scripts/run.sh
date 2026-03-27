#!/bin/bash

JDK_PATH="$HOME/.sdkman/candidates/java/21.0.10-zulu/bin/java"
echo "JDK PATH: $JDK_PATH"

SCRIPT_DIR=$(pwd)

CP="$SCRIPT_DIR/build/libs/Alya-1.0.jar:$SCRIPT_DIR/build/libs/libs"

cd "$SCRIPT_DIR/jars"

$JDK_PATH "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.heapdump" \
  "-Dminecraft.launcher.brand=minecraft-launcher" \
  "-Dminecraft.launcher.version=3.2.13" \
  "-Dio.netty.transport.noNative=true" \
  "-Dminecraft.client.jar=$SCRIPT_DIR/build/libs/Alya-1.0.jar" \
  -cp "$CP" -Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC \
  -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M \
  start.Main --gameDir "$SCRIPT_DIR/jars/.minecraft" \
  --assetIndex 1.8 --uuid 0 --userType msa
