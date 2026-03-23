# Alya

Alya client is a free, open-source Minecraft cheat client.

## Setting up

1. Init the Maven wrapper, libraries and run first-time build

```sh
./scripts/init.sh
```

Thats it!

## Building release

```sh
./mvnw package
```

## Development

To launch in development mode

```sh
./scripts/dev.sh
```

> [!NOTE]
> You **MUST** use the BellSoft Liberica JDK on Linux as others are
> not compatible with the current LWJGL (upgrade to v3 planned)
