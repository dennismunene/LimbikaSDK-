# Build and run

The build is pinned to its original 2016 toolchain — Gradle 2.4, Android Gradle plugin 1.2.3, compile SDK 23 — so expect to install older components alongside whatever you already have.

## Set up

Install JDK 8. Gradle 2.4 cannot parse modern Java version strings and stops with `Could not determine java version from '21.0.9'`, so the JDK bundled with current Android Studio will not work here.

In Android Studio's SDK Manager, tick "Show Package Details" and install:

- SDK Platform 23 (Android 6.0)
- Android SDK Build-Tools 23.0.2
- Android Support Repository

That last one matters. Both modules declare only `jcenter()`, and jcenter no longer serves `com.android.support:appcompat-v7:23.1.1`, though it does still serve the Android Gradle plugin itself.

Point Gradle at the SDK. Opening the project in Android Studio writes `local.properties` for you; otherwise export `ANDROID_HOME`, or create `local.properties` with a single `sdk.dir=` line. `local.properties` is untracked, so a fresh clone has neither.

```bash
export JAVA_HOME=/path/to/jdk8
export ANDROID_HOME=$HOME/Android/Sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
```

## Build

From the repository root:

```bash
./gradlew :limbikasdk:assembleRelease
./gradlew :app:assembleDebug
```

On Windows use `.\gradlew.bat`, and set the variables the PowerShell way in the same session you build from:

```powershell
$env:JAVA_HOME = "C:\Path\To\jdk8"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
```

The first run downloads Gradle 2.4, so give it a few minutes. The library AAR lands in `limbikasdk/build/outputs/aar/` and the demo APK in `app/build/outputs/apk/`.

## Run the demo

Start an emulator or connect a phone with USB debugging enabled:

```bash
adb install -r app/build/outputs/apk/app-debug.apk
adb shell monkey -p com.dmk.sampleappwidgetsdk -c android.intent.category.LAUNCHER 1
```

With several devices attached, find the serial with `adb devices` and add `-s SERIAL` after `adb`.

## Troubleshooting

**`Could not determine java version from '...'`** — Gradle 2.4 only understands Java 6 to 8. Point `JAVA_HOME` at a JDK 8 installation.

**`Could not find com.android.support:appcompat-v7:23.1.1`** — the Android Support Repository package is missing. Install it, or add Google's Maven repository to the `allprojects { repositories { ... } }` block in the root `build.gradle`.

**`SDK location not found`** — set `ANDROID_HOME`, or create `local.properties` as described above.

## Good to know

The library keeps view state in a SQLite database called `limbikaView` and declares `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE`. The demo targets API 23, so on newer Android versions you may need to grant storage access before saved images reappear.

Neither module defines a signing configuration, so `assembleRelease` produces unsigned output.
