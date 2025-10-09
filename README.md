# Andromeda Gradle Plugin ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)

> ![GitHub repo size](https://img.shields.io/github/repo-size/farsroidx/andromeda-gradle-plugin)

Andromeda Gradle Plugin by Farsroidx provides a set of pre-configured extensions, tasks, and utility functions 
to streamline project setup and build automation. It automatically applies essential pre-build configurations, 
adds reusable Kotlin DSL extensions, and simplifies dependency management for Android and JVM projects. 
This plugin is designed to save development time by providing ready-to-use pre-build code, custom tasks, 
and build conventions that can be immediately applied to any project.

### LATEST_VERSION: [![](https://jitpack.io/v/farsroidx/andromeda-gradle-plugin.svg)](https://jitpack.io/#farsroidx/andromeda-gradle-plugin)

1. Add dependency into libs.versions.toml:
```toml
[versions]
agp       = "8.13.0"
kotlin    = "2.2.20"
andromeda = "🔝LATEST_VERSION🔝" <--

[libraries]
....

[plugins]
android-application = { id = "com.android.application"     , version.ref = "agp"       }
kotlin-android      = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin"    }
farsroidx-andromeda = { id = "ir.farsroidx.andromeda"      , version.ref = "andromeda" } <--
```

2. Add into project build.gradle.kts:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android)      apply false
    alias(libs.plugins.kotlin.compose)      apply false
    alias(libs.plugins.farsroidx.andromeda) apply false <--
}
```

3. Add into app build.gradle.kts:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.farsroidx.andromeda) <--
}

andromeda {
  ...
}

android {
  ...
}
```
