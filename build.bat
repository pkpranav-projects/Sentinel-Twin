@echo off
setlocal
echo ========================================================
echo   Sentinel Twin - Android APK Automated Build Utility
echo ========================================================
echo.

where gradlew.bat >nul 2>&1
if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat not found. Please run this script from the repository root.
    exit /b 1
)

echo [1/3] Checking Java environment...
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] JDK was not found on system PATH. Attempting Gradle build anyway...
)

echo [2/3] Running unit tests and compiling debug APK...
call .\gradlew.bat assembleDebug --no-daemon
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Build failed. Please verify Android SDK 34 and JDK 17 are installed.
    exit /b %errorlevel%
)

echo.
echo [3/3] Build Successful!
echo --------------------------------------------------------
echo Compiled APK location:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.
echo To install directly on a connected device via ADB:
echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
echo ========================================================