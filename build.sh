#!/usr/bin/env bash
set -e

echo "========================================================"
echo "  Sentinel Twin - Android APK Automated Build Utility"
echo "========================================================"
echo ""

if [ ! -f "./gradlew" ]; then
    echo "[ERROR] ./gradlew not found. Run from repository root."
    exit 1
fi

chmod +x ./gradlew

echo "[1/3] Running unit tests..."
./gradlew testDebugUnitTest --no-daemon

echo "[2/3] Compiling debug APK..."
./gradlew assembleDebug --no-daemon

echo ""
echo "[3/3] Build Successful!"
echo "--------------------------------------------------------"
echo "Compiled APK location:"
echo "  app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "To install on connected device:"
echo "  adb install -r app/build/outputs/apk/debug/app-debug.apk"
echo "========================================================"