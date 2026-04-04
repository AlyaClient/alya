# Alya

Alya client is a free, open-source Minecraft cheat client.

## Setting up

### IntelliJ (Recommended)

Open the project in IntelliJ, import it and run the `Start` or `Start (macOS)` run configuration.

### I am the IDE (manual)

#### Prerequisites
- Java 25

1. Init the Gradle wrapper and package libraries

```sh
./scripts/init.sh
```

2. Build the project

```sh
./gradlew jar shadowJar
```

3. Run the project

```shell
./scripts/run.sh
```