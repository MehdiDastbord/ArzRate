# Arz Rates — Cloud-Build Android App

Kotlin + Jetpack Compose Android currency/rates dashboard powered by the Navasan API.

## Build directly in GitHub (no Android Studio)

This repository includes `.github/workflows/build-apk.yml`.

1. Upload the project files to a GitHub repository.
2. Open **Actions**.
3. Choose **Build Android APK**.
4. Tap **Run workflow**.
5. Wait for the green check.
6. Open the completed workflow run.
7. Under **Artifacts**, download `ArzRates-debug-apk`.
8. Extract it and install `app-debug.apk` on Android.

The workflow installs JDK 17 and Gradle 8.10.2 on GitHub's cloud runner, so Android Studio and Gradle do not need to be installed on the phone.

## App features

- First-launch currency selection
- Home dashboard with only selected rates
- Explore all rates returned by the API
- Search by name or code
- Add/remove selected rates
- Local selection persistence
- Manual refresh
- Loading/error states
- System dark/light theme
- Android home-screen widget foundation

## API

`https://api.navasan.tech/latest/?api_key=YOUR_KEY`

The supplied API key is currently stored in the Android build configuration for this prototype. For a public production APK, move the key behind your own backend/proxy because keys embedded in APKs can be extracted.

## Refresh note

The foreground app can refresh as often as required while active. Android does not guarantee an exact 60-second background execution interval for widgets; the widget uses Android's scheduled widget update mechanism.
