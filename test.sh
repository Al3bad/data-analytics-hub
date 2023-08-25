#!/usr/bin/env bash

# Set working directory as the location of the script (root folder of the project)
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
cd $SCRIPT_DIR

# cleanup bin files
[ -d "./bin" ] && rm -r bin
[ -d "./bin_test" ] && rm -r bin_test

# Compile java code
javac -d bin src/*.java
# Compile java unit tests
javac -d bin_test -cp lib/junit-4.13.2.jar:bin test/*.java

# Run test
# java -cp lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:bin_test:bin org.junit.runner.JUnitCore ...

# cleanup bin files
[ -d "./bin_test" ] && rm -r bin_test
[ -d "./bin" ] && rm -r bin
