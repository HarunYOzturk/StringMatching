#!/bin/bash

# String Matching Algorithm Test System
# Main entry point for running tests

echo "Compiling Java files..."
javac src/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    cd src
    java ManualTest "$@"
else
    echo "Compilation failed! Please fix the errors and try again."
    exit 1
fi

