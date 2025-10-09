@file:Suppress("unused")

package ir.farsroidx.plugin.andromeda

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import ir.farsroidx.plugin.BuildInfo
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.w3c.dom.Element
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Represents the extension entry point for the Andromeda Gradle Plugin.
 *
 * This extension provides customizable build-time utilities and configurations
 * accessible from Gradle scripts, typically as:
 *
 * ```
 * andromeda {
 *
 *     // Extract name from: 'src/main/res/values/strings.xml'
 *
 *     // <string name="app_name">Application</string>
 *
 *     // Application [1.0.0]
 *
 *     outputCraft { "${it.appName} [${it.versionName}]" }
 *
 * }
 *
 * android {
 *
 *     defaultConfig {
 *         versionName = "1.0.0"
 *     }
 * }
 * ```
 *
 * The primary purpose of this class is to handle automatic post-build APK
 * renaming and metadata extraction from Android resources.
 *
 * @property project The Gradle [Project] to which this extension is attached.
 */
open class AndromedaExtension(private val project: Project) {

    /**
     * Automatically renames generated APK files based on app metadata and build configuration.
     *
     * The function can be configured to execute only for release builds,
     * and supports custom naming strategies through a callback function.
     *
     * @param onlyInRelease If true, renaming will occur only for non-debuggable (release) variants.
     * @param resourceFieldName The name of the string resource to use for the app name (default: `app_name`).
     * @param resourceFilePath The relative path to the strings.xml file containing the app name.
     * @param callback A lambda that generates the final APK name using the [BuildInfo] data model.
     */
    fun outputCraft(
        onlyInRelease: Boolean = true,
        resourceFieldName: String = "app_name",
        resourceFilePath: String = "src/main/res/values/strings.xml",
        callback: (BuildInfo) -> String = { info -> "${info.appName} [${info.versionName}]" }
    ) {

        // Locate the Android Gradle Plugin extension
        val androidExt = project.extensions.findByType(BaseAppModuleExtension::class.java)
            ?: error("BaseAppModuleExtension not found")

        // Iterate through all Android build variants (e.g., debug, release)
        androidExt.applicationVariants.all appVariant@{ variant ->

            val isReleaseMode = !variant.buildType.isDebuggable

            // Skip variants that are not release builds when onlyInRelease = true
            if (onlyInRelease && !isReleaseMode) return@appVariant

            // Iterate through all outputs (APKs) for the current variant
            variant.outputs.all { baseVariantOutput ->

                // Generate task name based on variant (PascalCase)
                val taskName = variant.name.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                }

                val newTaskName = "renameApkAfter$taskName"

                // Register a new Gradle task responsible for renaming the APK after assembly
                val renameTask: TaskProvider<*> = project.tasks.register(newTaskName) { task ->

                    task.doLast {

                        val apkFile = baseVariantOutput.outputFile

                        // Skip if no APK is generated
                        if (!apkFile.exists()) return@doLast

                        // Extract the app name from the specified strings.xml resource file
                        val appName = try {

                            val file = project.file(resourceFilePath)
                            if (!file.exists())
                                throw Exception("The resource file '$resourceFilePath' not found.")

                            val doc = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder()
                                .parse(file)

                            doc.documentElement.normalize()
                            val strings = doc.getElementsByTagName("string")

                            // Find the element matching the desired name attribute
                            (0 until strings.length)
                                .map { index -> strings.item(index) }
                                .filterIsInstance<Element>()
                                .firstOrNull { element ->
                                    element.getAttribute("name") == resourceFieldName
                                }
                                ?.textContent ?: "app-${variant.name}"

                        } catch (e: Exception) {

                            println("⚠️ Failed to parse XML: ${e.message}")
                            "app-${variant.name}"
                        }

                        // Build structured info model for this variant
                        val buildInfo = BuildInfo(
                            appId       = variant.applicationId,            // Application ID (package)
                            appName     = appName,                          // Extracted name from resources
                            appDesc     = variant.description,              // Optional variant description
                            dirName     = variant.dirName,                  // Output directory name
                            flavorName  = variant.flavorName,               // Product flavor (if used)
                            variantName = variant.name ?: "unknown",        // Full variant identifier
                            versionName = variant.versionName ?: "unknown", // Version name from Gradle config
                            versionCode = variant.versionCode,              // Version code
                            buildType   = variant.buildType,                // Build type instance
                        )

                        // Generate new file name using the provided callback
                        val newName = "${callback(buildInfo)}.apk"
                        val newFile = File(apkFile.parentFile, newName)

                        // Rename the APK file
                        apkFile.renameTo(newFile)

                        val buildDir = newFile.parentFile.absolutePath

                        // Print a clearly formatted, clickable summary in the Gradle console
                        project.logger.lifecycle("----------------------------------------------------------------------")
                        project.logger.lifecycle("📦 APK Successfully Generated.")
                        project.logger.lifecycle("📁 Output Directory: $buildDir")
                        project.logger.lifecycle("🏷️ APK File Name: ${newFile.name}")
                        project.logger.lifecycle("----------------------------------------------------------------------")
                    }
                }

                // Ensure rename task runs after the variant's assemble task completes
                project.tasks.named("assemble${taskName}").configure { task ->
                    task.finalizedBy(renameTask)
                }
            }
        }
    }
}