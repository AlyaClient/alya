#!/bin/bash

# Hey you!
# This script is intended for end-users who do not want to
# use a GUI launcher. Do not run if developing.

JDK_PATH="$HOME/.sdkman/candidates/java/21.0.10-zulu/bin/java"
echo "JDK PATH: $JDK_PATH"

SCRIPT_DIR=$(pwd)
CP="$SCRIPT_DIR/Alya.jar:$SCRIPT_DIR/libs"

$JDK_PATH "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.heapdump" \
  "-Djava.library.path=$SCRIPT_DIR/natives" \
  "-Dminecraft.launcher.brand=minecraft-launcher" \
  "-Dminecraft.launcher.version=3.2.13" \
  "-Dio.netty.transport.noNative=true" \
  "-Dminecraft.client.jar=$SCRIPT_DIR/Alya.jar" \
  -cp "$CP" \
  -Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC \
  -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M \
  "-Dlog4j.configurationFile=$SCRIPT_DIR/include/client-1.7.xml" \
  start.Main --gameDir "$SCRIPT_DIR/.minecraft" \
  --assetIndex 1.8 --uuid 0 --userType msa
