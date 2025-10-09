@file:Suppress("unused")

package ir.farsroidx.plugin.andromeda

import ir.farsroidx.plugin.Version
import ir.farsroidx.plugin.brightBlue
import ir.farsroidx.plugin.brightYellow
import ir.farsroidx.plugin.createOrGetExtension
import ir.farsroidx.plugin.reset
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * AndromedaPlugin
 *
 * The core entry point of the Andromeda Gradle Plugin.
 * This class is automatically invoked when the plugin is applied
 * to a target [Project] via the Gradle build system.
 *
 * Responsibilities:
 *  - Display plugin metadata (version and release date) in colored logs.
 *  - Register or create the Andromeda extension for configuration.
 */
class AndromedaPlugin : Plugin<Project> {

    /**
     * Called by Gradle when the plugin is applied to a project.
     *
     * @param project The target Gradle project where this plugin is being applied.
     */
    override fun apply(project: Project) {

        // Display a stylized and colorized log message with plugin metadata
        project.logger.lifecycle(
            """
                Andromeda Plugin ${brightYellow}[v${Version.PLUGIN_VERSION}]${reset} ${brightBlue}[${Version.RELEASE_DATETIME}]${reset} applied Successfully!
            """.trimIndent()
        )

        // Register or retrieve the Andromeda extension to expose plugin configuration options
        project.createOrGetExtension<AndromedaExtension>("andromeda")

    }
}