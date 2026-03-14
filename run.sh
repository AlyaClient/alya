#!/bin/bash
JDK_PATH="$HOME/.sdkman/candidates/java/8.0.482-librca/bin/java"
SCRIPT_DIR=$(pwd)

CP="$SCRIPT_DIR/target/Alya-1.0.jar:$SCRIPT_DIR/target/libs"

$JDK_PATH "-XX:HeapDumpPath=$SCRIPT_DIR/Alya.heapdump" \
    "-Djava.library.path=$SCRIPT_DIR/jars/versions/1.8.9/1.8.9-natives" \
    "-Dminecraft.launcher.brand=minecraft-launcher" \
    "-Dminecraft.launcher.version=3.2.13" \
    "-Dminecraft.client.jar=$SCRIPT_DIR/target/Alya-1.0.jar" \
    -cp "$CP" \
    -Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC \
    -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M \
    "-Dlog4j.configurationFile=$SCRIPT_DIR/assets/log_configs/client-1.7.xml" \
    Start --gameDir "$SCRIPT_DIR/jars/.minecraft" \
    --assetIndex 1.8 --uuid 887341c45ea94dd3bae9bb00502124f1 --userType msa
