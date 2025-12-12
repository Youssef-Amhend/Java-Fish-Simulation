#!/bin/bash
echo "========================================"
echo "  Ocean Ecosystem Simulator"
echo "========================================"
echo

# Check if the JAR exists
if [ ! -f "target/ocean-ecosystem-simulator-2.0.0.jar" ]; then
    echo "ERROR: JAR file not found!"
    echo
    echo "Please build the project first:"
    echo "  mvn clean package -DskipTests"
    echo
    exit 1
fi

echo "Starting Ocean Simulation..."
echo "Press Ctrl+C to stop"
echo

java --enable-preview -jar target/ocean-ecosystem-simulator-2.0.0.jar
