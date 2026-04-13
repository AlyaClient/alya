<div align="center">
  <img src="src/main/resources/assets/minecraft/client/assets/gui/logo.png" width="128" height="128" alt="Alya Logo" />

# Alya

*A free, open-source Minecraft cheat client.*

![Build](https://github.com/alyaclient/alya/actions/workflows/release.yml/badge.svg)
![License](https://img.shields.io/badge/license-GPL--2.0--only-pink)
![Java](https://img.shields.io/badge/java-25-pink)
</div>

---

## Setting up

### IntelliJ (Recommended)

Open the project in IntelliJ, import it and run the `Start` or `Start (macOS)` run configuration.

### I am the IDE (manual)

#### Prerequisites

- Java 25
- Gradle (Optional, will be downloaded by wrapper)

1. Init the Gradle wrapper and package libraries: `./scripts/init.sh`
2. Build the project: `./gradlew jar shadowJar`
3. Run the project: `./scripts/run.sh`

---

## Project Structure

Packages
- `bypass`: The package holding all of Alya Client's base code.
- `net.minecraft`, `net.minecraftforge` and `net.optifine`: Contains all of Minecraft + Optifines source.
- `de.florianmichael`: ViaMCP for protocol switching
- `resources/lua`: Contains most of Alya client's bypasses and modules.

You will mainly need to look at `bypass` and `resources/lua` while developing. If you need to hook into Minecraft,
you will (obviously) be digging through `net.minecraft`.

### Project Tree

```aiignore
.
├── src/main/java
│   ├── bypass             # Alya base code
│   ├── de.florianmichael  # ViaMCP
│   ├── net.minecraft / net.minecraftforge / net.optifine
│   └── start
├── src/main/resources
│   └── lua                # bypasses and modules
├── jars                   # runtime working directory
├── scripts                # utilities
└── build.gradle.kts
```
