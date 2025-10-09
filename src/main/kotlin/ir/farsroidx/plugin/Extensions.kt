@file:Suppress("unused")

package ir.farsroidx.plugin

import ir.farsroidx.plugin.managers.PropertyManager
import org.gradle.api.Project

// Normal colors
private const val black = "\u001B[30m"
const val red     = "\u001B[31m"
const val green   = "\u001B[32m"
const val yellow  = "\u001B[33m"
const val blue    = "\u001B[34m"
const val magenta = "\u001B[35m"
const val cyan    = "\u001B[36m"
const val white   = "\u001B[37m"

// Bright colors
const val brightBlack = "\u001B[90m"
const val brightRed   = "\u001B[91m"
const val brightGreen = "\u001B[92m"
const val brightYellow = "\u001B[93m"
const val brightBlue    = "\u001B[94m"
const val brightMagenta = "\u001B[95m"
const val brightCyan    = "\u001B[96m"
const val brightWhite   = "\u001B[97m"

// Reset
const val reset = "\u001B[0m"

/**
 * Returns the property value for [key].
 *
 * Priority:
 * 1. local.properties at project root
 * 2. gradle.properties / project properties (findProperty)
 *
 * This extension uses an internal cache and will automatically reload the
 * local.properties when the file's lastModified timestamp changes.
 *
 * @receiver Project current Gradle project
 * @param key property name
 * @return property value or empty string if not found
 */
fun Project.getPropertyValue(key: String): String = PropertyManager.getPropertyValue(this, key)

/** Safe Int property reader with default. */
fun Project.getIntProperty(key: String, default: Int = 0): Int = PropertyManager.getInt(this, key, default)

/** Safe Boolean property reader with common truthy/falsy values. */
fun Project.getBooleanProperty(key: String, default: Boolean = false): Boolean =
    PropertyManager.getBoolean(this, key, default)

/** Safe Double property reader with default. */
fun Project.getDoubleProperty(key: String, default: Double = 0.0): Double = PropertyManager.getDouble(this, key, default)

/** Reads a delimited list from a property (default delimiter is comma). */
fun Project.getListProperty(key: String, delimiter: String = ","): List<String> =
    PropertyManager.getList(this, key, delimiter)

/**
 * Logs a debug message in green.
 *
 * @param message The message to log.
 */
fun dLog(message: Any) {
    println("${brightGreen}[DEBUG] $message$reset")
}

/**
 * Logs an informational message in blue.
 *
 * @param message The message to log.
 */
fun iLog(message: Any) {
    println("${brightBlue}[INFO] $message$reset")
}

/**
 * Logs a warning message in yellow.
 *
 * @param message The message to log.
 */
fun wLog(message: Any) {
    println("$brightYellow[WARN] $message$reset")
}

/**
 * Logs an error message in red.
 *
 * @param message The message to log.
 */
fun eLog(message: Any) {
    println("${red}[ERROR] $message$reset")
}

/**
 * Retrieves the existing Gradle extension of type [T] or creates a new one if it does not exist.
 *
 * This generic helper ensures that only a single instance of the specified extension type [T]
 * is associated with the project, preventing `DuplicateExtensionException` when the plugin
 * is applied multiple times (e.g., in both root and subprojects).
 *
 * If an extension with the given [name] already exists in the project's extension container,
 * that instance is returned. Otherwise, a new instance of [T] is created, registered, and returned.
 *
 * The `reified` type parameter allows type-safe access to the extension type at runtime.
 *
 * Example usage:
 * ```
 * val rootExt = project.createOrGetExtension<MyExtension>("myExtension")
 * rootExt.someProperty = "value"
 * ```
 *
 * @param name The name of the extension.
 * @receiver Project The Gradle project to attach or retrieve the extension from.
 * @return The extension instance of type [T] associated with this project.
 */
internal inline fun <reified T> Project.createOrGetExtension(name: String): T {
    return (extensions.findByName(name) as? T)
        ?: extensions.create(
            name, T::class.java, this
        )
}