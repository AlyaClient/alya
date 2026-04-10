#!/bin/bash

#
# Copyright (c) 2026 Alya Client.
#
# Alya Client is a free, open-source Minecraft hacked client.
#
#     This program is free software; you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation; either version 2 of the License, or
#     (at your option) any later version.
#
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License along
#     with this program; if not, write to the Free Software Foundation, Inc.,
#     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

set -euo pipefail

# Hey you!
# This script is intended for end-users who do not want to
# use a gui launcher. Do not run if developing.

# Modify this to match your Java installation
JDK_PATH="$HOME/.sdkman/candidates/java/25.0.2-zulu/bin/java"
echo "JDK PATH: $JDK_PATH"

SCRIPT_DIR=$(pwd)
CP="$SCRIPT_DIR/Alya.jar:$SCRIPT_DIR/libs"

EXTRA_FLAGS=""
if [[ "$(uname)" == "Darwin" ]]; then
  EXTRA_FLAGS="-XstartOnFirstThread"
fi

mkdir -p "$SCRIPT_DIR/.minecraft"
cd "$SCRIPT_DIR/.minecraft"

$JDK_PATH "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.heapdump" \
  "-Dminecraft.launcher.brand=minecraft-launcher" \
  "-Dminecraft.launcher.version=3.2.13" \
  "-Dio.netty.transport.noNative=true" \
  --enable-native-access=ALL-UNNAMED \
  --sun-misc-unsafe-memory-access=allow \
  "-Dminecraft.client.jar=$SCRIPT_DIR/Alya.jar" \
  $EXTRA_FLAGS -cp "$CP" -Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC \
  -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M \
  start.Main --gameDir "$SCRIPT_DIR/.minecraft" \
  --assetIndex 1.8 --uuid 0 --userType msa
