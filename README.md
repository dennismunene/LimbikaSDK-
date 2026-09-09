# Limbika SDK

An Android library built around `LimbikaView`, a canvas-backed `View` that the user can drag, resize and rotate. It renders text, a drawable or a circular badge, exposes tap, drag and rotation listeners, and saves each view's position, styling and bitmap to SQLite so it returns on the next launch.

Two Gradle modules:

- `limbikasdk` — the library, package `com.dmk.limbikasdk`.
- `app` — a demo app showing text, image and circle blocks.

## Using it

```java
LimbikaView block = new LimbikaView(this);
block.setKey("caption");   // identity used to save and restore this block
block.setText("Text Here");
block.setTextSize(30);
block.onResume();          // restores a previously saved block
addContentView(block, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
```

Call `saveViewState()` to persist position and styling, and `saveBitmap()` when the block holds an image.

## Build and run

This project dates from 2016 and its toolchain has not been updated. You'll need JDK 8, Android SDK Platform 23 and Build-Tools 23.0.2 — newer JDKs cannot run its Gradle wrapper.

Follow the [run instructions](run_instructions.md) to build the library and the demo app.
