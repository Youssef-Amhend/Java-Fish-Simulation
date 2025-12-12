@echo off
echo ========================================
echo   Ocean Ecosystem Simulator
echo ========================================
echo.

REM Check if the JAR exists
if not exist "target\ocean-ecosystem-simulator-2.0.0.jar" (
    echo ERROR: JAR file not found!
    echo.
    echo Please build the project first:
    echo   1. Open IntelliJ IDEA
    echo   2. Go to: Maven sidebar ^> Lifecycle ^> package
    echo   3. Or run: mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)

echo Starting Ocean Simulation...
echo Press Ctrl+C to stop
echo.

java --enable-preview -jar target\ocean-ecosystem-simulator-2.0.0.jar

pause
