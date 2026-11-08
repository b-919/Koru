[![Build Status](https://travis-ci.org/Anuken/Koru.svg?branch=master)](https://travis-ci.org/Anuken/Koru/)

# Koru

A 2D sandbox game, originally created by [Anuken](https://github.com/Anuken/Koru) with libGDX and uCore.
This repository is a port of the game to [Arc](https://github.com/Anuken/Arc), the framework used by
[Mindustry](https://github.com/Anuken/Mindustry).

The gameplay code, assets and world generators are preserved. Rendering, input, UI, networking and
the old uCore libraries have been migrated to Arc 160.4 and Java 17.

![axe-wielding crabs](http://i.imgur.com/6bft2gp.png)

![reflections](https://i.imgur.com/cMqFB6c.gif)

## Features

- Infinite procedurally generated world
- Biomes, caves and structure generation
- Basic building and resource collection
- Reflective water and custom shaders
- Arc scene UI
- Multiplayer using ArcNet and Kryo serialization
- Crabs
- World lighting data
- In-game chat

## Requirements

- JDK 17
- The Gradle wrapper included in this repository

## Building and running

Windows:

```bat
gradlew.bat :desktop:run
```

Linux / macOS:

```bash
./gradlew :desktop:run
```

Start a server, then start a client and press **Connect**:

```bat
gradlew.bat :server:run
gradlew.bat :desktop:run
```

Compile everything without launching:

```bat
gradlew.bat :core:compileJava :server:compileJava :desktop:compileJava
```