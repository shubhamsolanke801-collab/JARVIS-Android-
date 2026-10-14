# JARVIS Android Starter

A small native Android starter app with speech recognition, spoken replies, time/battery info, YouTube/Google links, flashlight toggle, and phone Settings shortcut.

## Important
- This ZIP contains **source code**, not a prebuilt APK. It has not been compiled or tested on a physical phone in this environment.
- Speech recognition availability and language depend on Android and the installed speech service.
- The app does not run as an always-listening wake-word assistant. Tap **LISTEN** to start voice recognition.

## Build an APK from an Android phone using GitHub Actions
1. Extract this ZIP on your phone (Files app can usually extract ZIP files).
2. Create a GitHub repository in your browser and upload the extracted project files/folders to the repository root. Keep `.github/workflows/build-apk.yml` included.
3. Open the repository's **Actions** tab and enable Actions if asked.
4. Choose **Build JARVIS APK** → **Run workflow**. Or pushing files to the `main` branch triggers a build.
5. When the workflow finishes successfully, open its run, scroll to **Artifacts**, and download **JARVIS-debug-APK**.
6. Extract the downloaded artifact ZIP and install `app-debug.apk`. Android may ask you to allow installs from your browser/files app; only enable it for the app you are using, then turn it off afterward if desired.

## Build with Android Studio
Open this folder as a project and build the `app` debug APK. The included GitHub workflow is the phone-friendly cloud-build alternative.
