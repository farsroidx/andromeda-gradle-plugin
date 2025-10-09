package ir.farsroidx.plugin

import com.android.builder.model.BuildType

/**
 * Data class to store detailed build information for APK output.
 *
 * This class is used by the `outputCraft` (or similar) plugin function to provide
 * contextual data for generating APK filenames, logging, or other custom tasks.
 *
 * @property appId The application ID (package name) of the variant (e.g., "com.example.app")
 * @property appName The app name extracted from the resources (strings.xml)
 * @property appDesc Optional description of the variant (from variant.description)
 * @property dirName The build directory name for this variant (e.g., "release", "debug")
 * @property flavorName The product flavor name (empty string if no flavor)
 * @property variantName Full variant name (combination of flavor + build type, e.g., "demoRelease")
 * @property versionName Application versionName as defined in build.gradle
 * @property versionCode Application versionCode as defined in build.gradle
 * @property buildType The [BuildType] instance associated with this variant
 */
data class BuildInfo(
    val appId      : String,
    val appName    : String,
    val appDesc    : String,
    val dirName    : String,
    val flavorName : String,
    val variantName: String,
    val versionName: String,
    val versionCode: Int,
    val buildType: BuildType,
)