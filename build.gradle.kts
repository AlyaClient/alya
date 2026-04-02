import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    java
    id("com.gradleup.shadow") version "9.4.0"
}

group = "dev.thoq"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://litarvan.github.io/maven")
    maven("https://jitpack.io")
}

val lwjglVersion = "3.3.4"
val lwjglAllNatives = listOf(
    "natives-windows",
    "natives-windows-arm64",
    "natives-linux",
    "natives-linux-arm64",
    "natives-macos",
    "natives-macos-arm64",
)

val generatedSrcDir = layout.buildDirectory.dir("generated/src/main/java")

sourceSets {
    main {
        java.srcDir(generatedSrcDir)
    }
}

val generateBuildConfig by tasks.registering {
    val gitHash = try {
        providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim()
    } catch (_: Exception) {
        "unknown"
    }

    val outputDir = generatedSrcDir.get().asFile
    val packageDir = File(outputDir, "dev/thoq/alya")

    outputs.dir(outputDir)

    doLast {
        packageDir.mkdirs()
        File(packageDir, "BuildConfig.java").writeText(
            """
                package dev.thoq.alya;
                
                public class BuildConfig {
                    public static final String GIT_HASH = "$gitHash";
                }
                """.trimIndent()
        )
    }
}

tasks.compileJava {
    dependsOn(generateBuildConfig)
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
    implementation("org.apache.logging.log4j:log4j-api:2.25.3")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("com.mojang:authlib:1.5.25")
    implementation("com.github.dblock:oshi-core:1.2")
    implementation("io.netty:netty-all:4.0.23.Final")
    val useLocalLwjglBridge = gradle.extra["useLocalLwjglBridge"] as Boolean
    if (useLocalLwjglBridge) {
        implementation("org.mcphackers:legacy-lwjgl3")
    } else {
        implementation("com.github.RareHyperIonYT:LWJGL3-Bridge:7983e08837")
    }
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-tinyfd:$lwjglVersion")
    for (natives in lwjglAllNatives) {
        runtimeOnly("org.lwjgl:lwjgl::$natives")
        runtimeOnly("org.lwjgl:lwjgl-glfw::$natives")
        runtimeOnly("org.lwjgl:lwjgl-openal::$natives")
        runtimeOnly("org.lwjgl:lwjgl-opengl::$natives")
        runtimeOnly("org.lwjgl:lwjgl-stb::$natives")
        runtimeOnly("org.lwjgl:lwjgl-tinyfd::$natives")
    }
    implementation("fr.litarvan:openauth:1.1.3")
    implementation("org.luaj:luaj-jse:3.0.1")
}

configurations.all {
    exclude(group = "org.lwjgl.lwjgl")
}

tasks.jar {
    from(sourceSets.main.get().output.resourcesDir)
}

tasks.shadowJar {
    archiveFileName = "libs"
    manifest {
        attributes["Multi-Release"] = "true"
    }
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    exclude("log4j2*.xml")
    transform(Log4j2PluginsCacheFileTransformer::class.java)
    // exclude the project's own classes, only bundle dependencies
    dependencies {
        exclude(dependency("${project.group}:${project.name}"))
    }
    // include project resources
    from(sourceSets.main.get().output.resourcesDir)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
