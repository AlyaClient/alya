#!/bin/bash

OS_FLAGS=""
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS_FLAGS="-XstartOnFirstThread"
fi

JDK_PATH="$HOME/Library/Java/JavaVirtualMachines/azul-26/Contents/Home/bin/java"
echo "JDK PATH: $JDK_PATH"

SCRIPT_DIR=$(pwd)
CP="$SCRIPT_DIR/build/libs/Alya-1.0.jar:$SCRIPT_DIR/build/libs/libs"

cd "$SCRIPT_DIR/jars" || exit

$JDK_PATH $OS_FLAGS \
  "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.heapdump" \
  "-Dminecraft.launcher.brand=minecraft-launcher" \
  "-Dminecraft.launcher.version=3.2.13" \
  "-Dio.netty.transport.noNative=true" \
  "-Dminecraft.client.jar=$SCRIPT_DIR/build/libs/Alya-1.0.jar" \
  --enable-native-access=ALL-UNNAMED \
  --sun-misc-unsafe-memory-access=allow \
  -cp "$CP" \
  -Xmx4G \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseG1GC \
  -XX:G1NewSizePercent=20 \
  -XX:G1ReservePercent=20 \
  -XX:MaxGCPauseMillis=50 \
  -XX:G1HeapRegionSize=32M \
  start.Main \
  --gameDir "$SCRIPT_DIR/jars" \
  --uuid 0 \
  --userType msa
