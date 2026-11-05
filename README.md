# Islami Naat o Dua

Simple Islamic app for Android (Kotlin): daily naat player, English tasbeeh
counter, namaz timings and Qibla direction — one simple app.

- **Naat:** plays "Maula Ya Salli Wa Sallim" (Duff Only) by Aqiib Farid
  (CC BY 4.0 — attribution in the app and in every release).
- **Tasbeeh:** counter with English dhikr names.
- **Prayer:** namaz timings plus a Qibla direction guide.

Package: `com.islamicstories.naatdua` · minSdk 24 · targetSdk 34.

## Release

The released APK is **v2.2** (versionCode 4) — see
[Releases](https://github.com/amjadmughal7428-byte/islami-naat-o-dua/releases).
Download `app-release.apk` from the v2.2 release and install it on Android.

## Build from source

```sh
./gradlew assembleRelease   # needs Android SDK 34 + JDK 17
```

Release signing reads `keystore.properties` at the project root
(`storeFile`, `storePassword`, `keyAlias`, `keyPassword`) — that file is
**not** in this repo; create your own keystore for release builds.
Debug builds use the default debug key.

## Note on binary assets

The naat audio (`app/src/main/res/raw/maula_ya_salli_duff.mp3`) and the
fonts (`app/src/main/res/font/*.ttf`) are **not** in this repo — they are
binary files. To build the naat player, place the CC BY 4.0 recording
"Maula Ya Salli Wa Sallim" (Duff Only) by Aqiib Farid as
`app/src/main/res/raw/maula_ya_salli_duff.mp3`, and the Roboto and Amiri
font files under `app/src/main/res/font/`.

## Privacy

Privacy policy: https://sites.google.com/view/islaminaatodua/privacy-policy
