# Notes

[Neovim](https://neovim.io) editor was mainly used to develop this program. During the development, the program was compiled and tested through the command line with java SDK. But this project was also adapted to be compiled and run using [Intellij IDEA IDE](https://www.jetbrains.com/idea/).

# Requirements

- Java SDK 20
- JavaFX 20.0.2
- Junit 4.13.2 (with hamcrest-core 1.3)
- SQLite JDBC 3.43.0.0

# Getting Started

## Download the required libraries

- Download [JavaFX](https://gluonhq.com/products/javafx/) and put all jar file in `lib/javafx` folder
- Download [Junit](https://github.com/junit-team/junit4/wiki/Download-and-Install) and put all jar file in `lib/junit4` folder
- Download [SQLite JDBC](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc) and put the jar file in `lib/sqlite-jdbc` folder

## Compiling and running the program

There are two ways to run the program:

## Method 1: Using Intellij IDEA IDE

1. Open the root folder in Intellij IDEA IDE: `File -> Open`.
2. Hit the run button in the top right corner of the IDE or use the hotkey `ctrl+R`.

### Method 2: Using a bash script (Unix-like systems only)

1. In the terminal, navigate to the root directory of the project
2. Run `run.sh` script:

> NOTE: you might need to add execute flag to run the script: `chmod u+x run.sh`

```bash
./run.sh
```

You should also be able to run the script from anywhere in the file system but you need to specify the correct path to this script.

# Acknowledgment

- Logo created by [Shopify - Hatchful](https://www.shopify.com/tools/logo-maker)
