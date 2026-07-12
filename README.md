# Knurl

A native Android barbell workout tracker. Pounds-first, offline-only, Room-backed, built entirely
in the cloud via GitHub Actions — no local Android toolchain required.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Room for local persistence
- Coroutines + Flow, MVVM
- minSdk 26, targetSdk/compileSdk 36

## Installing the debug APK on your phone

1. Open the latest green **"Build debug APK"** run in this repo's **Actions** tab.
2. Download the `app-debug` artifact (a zip) and extract `app-debug.apk`.
3. Move it to your phone (Drive, email to yourself, or USB).
4. On the phone, allow installing unknown apps for the source app, then tap the APK to install.

Debug APKs are auto-signed with the standard debug key, so `app-debug.apk` installs without any
signing setup.

## Development

Every push to `main` triggers a GitHub Actions build that produces a downloadable debug APK.
There is no local build requirement; the CI run is the source of truth for build correctness.
