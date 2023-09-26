#!/usr/bin/env bash

# Set working directory as the location of the script (root folder of the project)
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
cd $SCRIPT_DIR

binFolderPath="./bin"

# cleanup bin files
[ -d $binFolderPath ] && rm -r $binFolderPath

# Find Java source files (*.java) and JAR files (*.jar)
javaFiles=""
jarFiles=""

while IFS= read -r file; do
	javaFiles="$javaFiles $file"
done < <(find ./src -name "*.java" -type f)

while IFS= read -r file; do
	jarFiles="$jarFiles:$file"
done < <(find ./lib -name "*.jar" -type f)

# Build the classpath for any JAR files found in the source directory and its subdirectories
classpath=${jarFiles#;}

# Compile the Java source files
javac -d $binFolderPath -cp "$classpath" $javaFiles

javaExecutable="java"
modulePath="./lib"
modules="javafx.controls,javafx.fxml"
fullClasspath="$classpath:./resources:$binFolderPath"
mainClass="dev.alabbad.DataAnalyticsHub.Main"

# Run the Java program
"$javaExecutable" --module-path "$modulePath" --add-modules "$modules" -cp "$fullClasspath" "$mainClass"

# cleanup bin files
[ -d $binFolderPath ] && rm -r $binFolderPath
