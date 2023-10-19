#!/usr/bin/env bash

# ==================================================
# --> Set working dirctory as the location of the script
# ==================================================
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
cd $SCRIPT_DIR

# ==================================================
# --> Cleanup bin files (if any)
# ==================================================
binFolderPath="./bin"
binTestFolderPath="./bin_test"

[ -d $binFolderPath ] && rm -r $binFolderPath
[ -d $binTestFolderPath ] && rm -r $binTestFolderPath

# ==================================================
# --> Find Java source files (*.java) and JAR files (*.jar)
# ==================================================
javaFiles=""
javaTestFiles=""
jarFiles=""

while IFS= read -r file; do
	javaFiles="$javaFiles $file"
done < <(find ./src -name "*.java" -type f)

while IFS= read -r file; do
	javaTestFiles="$javaTestFiles $file"
done < <(find ./test -name "*.java" -type f)

while IFS= read -r file; do
	jarFiles="$jarFiles:$file"
done < <(find ./lib -name "*.jar" -type f)

# Build the classpath for any JAR files found in the source directory and its subdirectories
classpath=${jarFiles#;}

# ==================================================
# --> Compiles java files
# ==================================================
# Compile the Java source files
javac -d $binFolderPath -cp "$classpath" $javaFiles

# Compile the Java source files
javac -d $binTestFolderPath -cp "$classpath:$binFolderPath" $javaTestFiles

# ==================================================
# --> Run tests
# ==================================================
java -cp "$classpath:$binTestFolderPath:$binFolderPath" org.junit.runner.JUnitCore test.TestUserDao
java -cp "$classpath:$binTestFolderPath:$binFolderPath" org.junit.runner.JUnitCore test.TestPostDao
java -cp "$classpath:$binTestFolderPath:$binFolderPath" org.junit.runner.JUnitCore test.TestParser

# ==================================================
# --> Cleanup bin files
# ==================================================
[ -d $binFolderPath ] && rm -r $binFolderPath
[ -d $binTestFolderPath ] && rm -r $binTestFolderPath
