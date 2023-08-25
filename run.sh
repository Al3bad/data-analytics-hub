#!/usr/bin/env bash

# Set working directory as the location of the script (root folder of the project)
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
cd $SCRIPT_DIR

# cleanup bin files
[ -d "./bin" ] && rm -r bin

# Compile java code
# javac -d bin -cp lib/sqlite-jdbc-3.42.0.0.jar:bin src/*.java
javac -d bin -cp lib/sqlite-jdbc-3.42.0.0.jar:lib/javafx/*:bin src/*.java

# Run the program
java --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -cp lib/sqlite-jdbc-3.42.0.0.jar:lib/javafx/*:bin Main $1

# cleanup bin files
[ -d "./bin" ] && rm -r bin
