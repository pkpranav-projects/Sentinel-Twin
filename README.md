<div align="center">

# Sentinel Twin: Android Mobile Security & Behavioral Digital Twin Framework

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-7986CB?style=for-the-badge)](https://m3.material.io/)
[![Room DB](https://img.shields.io/badge/Storage-Room%20Database-3DDC84?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Pattern-Clean%20%2F%20MVVM-orange?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Release](https://img.shields.io/badge/Release-v1.0.0-blue?style=for-the-badge)](https://github.com/pkpranav-projects/Sentinel-Twin/releases/tag/v1.0.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

A proactive Android endpoint security system that creates an in-memory **Behavioral Digital Twin** of your mobile device, continuously contrasting real-time telemetry against learned operational baselines to detect stealthy zero-day deviations, privilege abuse, and persistent device drift.

[Core Concept](#-the-digital-twin-security-paradigm) • [Engine Architecture](#-engine-architecture--data-pipeline) • [Compose UI Suite](#-compose-screen-suite) • [Demo Scenarios](#-pre-configured-demo-scenarios) • [Build & Install](#-build--installation) • [Author](#-author)

</div>

---

## The Digital Twin Security Paradigm

Traditional mobile antivirus solutions rely primarily on static signature scanning—checking if installed package names or file hashes match known malware catalogs. Sophisticated spyware, unauthorized telemetry exfiltration, and root compromises easily evade signature scanners by mutating code or leveraging legitimate Android permissions.

**Sentinel Twin** introduces a modern paradigm:

```text
+-------------------+        Live Telemetry        +----------------------+
|  Physical Device  | -------------------------->  | Snapshot Normalizer  |
+-------------------+                              +----------------------+
                                                              |
                                                              v
+-------------------+       Calculates Drift       +----------------------+
|   Digital Twin    | <==========================> |     Drift Engine     |
| (Learned Baseline)|                              +----------------------+
+-------------------+                                         |
                                                              v
                                                   +----------------------+
                                                   | Deterministic Reason |
                                                   |  & Evidence Builder  |
                                                   +----------------------+
```

1. **Continuous Telemetry Normalization**: Normalizes physical hardware and OS states (network sockets, battery draw curves, sensor states, package access patterns) into structured `SnapshotEntity` records.
2. **Dynamic Baseline Learning**: The `BaselineEngine` models normal operational envelopes for your specific device and user routines.
3. **Multi-Variate Drift Detection**: The `DriftEngine` measures mathematical divergence between live device states and the established digital twin.
4. **Deterministic Reasoning**: Evaluates anomalies using transparent, auditable causal rules rather than opaque AI guesses, building verifiable forensic proof chains via the `EvidenceBuilder`.

---

## Engine Architecture & Data Pipeline

The codebase is engineered strictly following modern Android development practices (Clean Architecture, Kotlin Coroutines, StateFlow, Room ORM, and Jetpack Compose):

```text
com.example.sentineltwin/
├── device/
│   ├── Adapters.kt            # Hardware and OS subsystem telemetry adapters
│   └── SnapshotNormalizer.kt  # Canonical schema transformer for raw system snapshots
├── twin/
│   ├── BaselineEngine.kt      # Statistical baseline computation and rolling calibration
│   ├── DriftEngine.kt         # Delta-distance calculation between baseline and live state
│   └── TwinRepository.kt      # State synchronization layer managing digital twin state
├── reasoning/
│   ├── DeterministicReasoner.kt # Transparent rule evaluation and threat severity scoring
│   └── EvidenceBuilder.kt       # Causal evidence graph construction for forensic audit
├── local/
│   └── SnapshotEntity.kt      # Room ORM database entity for persistent audit logging
└── ui/
    ├── MainScreen.kt          # Primary health overview, live risk gauges, and telemetry cards
    ├── TwinScreen.kt          # Side-by-side visualization of physical vs digital twin states
    ├── TimelineScreen.kt      # Historical anomaly timeline and drift escalation feed
    ├── SandboxScreen.kt       # Controlled threat simulation environment
    └── InvestigateScreen.kt   # Deep forensic inspector with evidence breakdown
```

---

## Compose Screen Suite

| Screen | Primary Functionality |
| :--- | :--- |
| **Main Dashboard** (`MainScreen`) | Real-time endpoint health gauge, overall drift percentage, quick status chips, and immediate security action recommendations. |
| **Digital Twin Explorer** (`TwinScreen`) | High-tech visual comparison rendering the live physical device status alongside its virtual digital twin counterpart. |
| **Anomaly Timeline** (`TimelineScreen`) | Chronological feed of recorded telemetry snapshots, highlighting points where system parameters drifted past safety thresholds. |
| **Interactive Sandbox** (`SandboxScreen`) | An isolated testing playground allowing developers and auditors to trigger controlled parameter drifts and observe engine reactions. |
| **Forensic Investigation** (`InvestigateScreen`) | Detailed breakdown of detected security anomalies with causal evidence trails, telemetry diffs, and risk scores. |

---

## Pre-Configured Demo Scenarios

Sentinel Twin includes an integrated `DemoScenario` runner designed for evaluations, hackathon presentations, and academic research without requiring real malicious malware:

- **Background Exfiltration Simulation**: Simulates unauthorized background radio activation and anomalous packet bursts outside established diurnal baselines.
- **Sensor Stalking Drift**: Replicates an unforegrounded application quietly harvesting location or sensor telemetry.
- **Privilege / Configuration Mutation**: Emulates unexpected system property alterations and unauthorized setting mutations.

---

## Build & Installation

### Prerequisites
- **Android Studio** (Hedgehog | Iguana | Jellyfish or newer)
- **JDK 17+**
- **Android SDK Platform 34** (Target API 34, Min API 24)

### 1. Clone the Repository
```bash
git clone https://github.com/pkpranav-projects/Sentinel-Twin.git
cd Sentinel-Twin
```

### 2. Build Debug APK via Gradle
```bash
# On Linux / macOS:
./gradlew assembleDebug

# On Windows:
.\gradlew.bat assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Install Directly to Connected Device or Emulator
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
*(Or run `./gradlew installDebug` with an active device connected over USB / ADB)*.

---

## Testing

Run unit tests and Android instrumentation tests:

```bash
# Unit tests
./gradlew testDebugUnitTest

# Connected UI & ViewModel tests
./gradlew connectedDebugAndroidTest
```

---

## Repository Structure

```text
Sentinel-Twin/
├── app/
│   ├── src/main/java/com/example/sentineltwin/
│   │   ├── MainActivity.kt          # Single-activity Compose container
│   │   ├── Navigation.kt            # Compose Navigation routes and graph
│   │   ├── domain/                  # Clean domain models and types
│   │   ├── device/                  # System adapters and snapshot normalization
│   │   ├── twin/                    # Digital twin baseline and drift computation
│   │   ├── reasoning/               # Deterministic rule engine & evidence builder
│   │   ├── local/                   # Room database entities and DAOs
│   │   ├── demo/                    # Pre-packaged evaluation scenarios
│   │   └── ui/                      # Jetpack Compose UI screens and themes
│   ├── build.gradle.kts             # App-level build configuration and dependencies
│   └── AndroidManifest.xml          # Permissions and application metadata
├── gradle/                          # Gradle wrapper assets
├── build.gradle.kts                 # Root project build configuration
├── settings.gradle.kts              # Module and repository definitions
└── README.md                        # Framework documentation
```

---

## Author

Developed by **P K PRANAV** ([@pkpranav-projects](https://github.com/pkpranav-projects)).

## License

This project is licensed under the [MIT License](LICENSE).