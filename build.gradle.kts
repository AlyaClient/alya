import org.gradle.internal.os.OperatingSystem

plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.thoq"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://litarvan.github.io/maven")
    maven { url = uri("https://jitpack.io") }
}

val lwjglVersion = "3.4.1"
val lwjglNatives: String = when {
    OperatingSystem.current().isWindows -> "natives-windows"
    OperatingSystem.current().isLinux   -> "natives-linux"
    OperatingSystem.current().isMacOsX  -> "natives-macos-arm64"
    else -> throw GradleException("Unsupported OS: ${OperatingSystem.current().name}")
}

dependencies {
    implementation("com.paulscode:codecjorbis:20101023")
    implementation("com.paulscode:codecwav:20101023")
    implementation("com.paulscode:libraryjavasound:20101123")
    implementation("com.paulscode:librarylwjglopenal:20100824")
    implementation("com.paulscode:soundsystem:20120107")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:httpcore:4.4.14")
    implementation("com.ibm.icu:icu4j:68.2")
    implementation("net.java.dev.jna:jna:5.6.0")
    implementation("net.java.dev.jna:jna-platform:5.6.0")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("com.mojang:authlib:1.5.25")
    implementation("com.github.dblock:oshi-core:1.2")
    implementation("io.netty:netty-all:4.0.23.Final")
    implementation("com.github.RareHyperIonYT:LWJGL3-Bridge:cb4dd06464")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    implementation("org.lwjgl:lwjgl-tinyfd")
    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd::$lwjglNatives")
    implementation("fr.litarvan:openauth:1.1.3")
    implementation("org.luaj:luaj-jse:3.0.1")
}

configurations.all {
    exclude(group = "org.lwjgl.lwjgl")
}

tasks.shadowJar {
    archiveFileName = "libs"
    manifest {
        attributes["Multi-Release"] = "true"
    }
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    // exclude the project's own classes, only bundle dependencies
    dependencies {
        exclude(dependency("${project.group}:${project.name}"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.register<JavaExec>("start") {
    group = "application"
    description = "Launches Alya client"
    workingDir = file("${project.projectDir}/jars")
    mainClass.set("start.Main")
    classpath = sourceSets.main.get().runtimeClasspath + files("${project.projectDir}/jars/versions/1.8.9/1.8.9.jar")

    dependsOn("processResources")
}
