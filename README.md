# Alya

Alya client is a free, open-source Minecraft cheat client.

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

## Support

If you have trouble, please feel free to join our [Discord](https://discord.gg/J3XUnGaZjQ)
or DM `@thoqx` on Discord.

## License

This project uses the GPL-2.0-only license.

PERMISSIONS
- Use the software for any purpose
- Study and read the source code
- Modify the source code
- Distribute original copies
- Distribute modified copies, under the same license

CONDITIONS
- Copyleft: any distributed version must also be licensed under GPL-2.0
- Source code must be made available when distributing
- Original license and copyright notices must be preserved
- No additional restrictions may be imposed on recipients

RESTRICTIONS
- Cannot be relicensed under a different or more restrictive license
- Cannot be distributed as closed-source without providing source
- Cannot be upgraded to GPL-3.0 or any other version
  (the "-only" suffix locks it strictly to version 2)

See the [LICENSE](LICENSE) file for more information.
