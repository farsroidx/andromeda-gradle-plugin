package ir.farsroidx.plugin.managers

import org.gradle.api.Project
import java.io.File
import java.util.Properties

/**
 * Internal manager that caches loaded properties files and reloads them
 * only when the underlying file's lastModified changes.
 */
internal object PropertyManager {

    private data class CachedFile(val props: Properties?, val lastModified: Long)

    // Cache keyed by the file path to support multi-project builds
    private val cache = mutableMapOf<File, CachedFile?>()

    /**
     * Read a property value for a project: priority:
     *  1) local.properties (rootDir/local.properties)
     *  2) gradle.properties or project properties (findProperty)
     */
    fun getPropertyValue(project: Project, key: String): String {

        val localFile = File(project.rootDir, "local.properties")

        val localProps = loadFileIfChanged(localFile)

        return localProps?.getProperty(key)
            ?: (project.findProperty(key) as? String)
            ?: "❌ Key not found anywhere."
    }

    /**
     * Load properties from file, but only if file has changed since last load.
     * Returns `null` if file does not exist.
     */
    fun loadFileIfChanged(file: File): Properties? {

        val lastModified = file.lastModified()
        val cached       = cache[file]

        // If cached and unchanged -> reuse
        if (cached != null && cached.lastModified == lastModified) {
            return cached.props
        }

        // Otherwise (not cached or changed), attempt to (re)load
        return if (file.exists()) {
            val props = Properties().apply {
                file.inputStream().use { load(it) }
            }
            cache[file] = CachedFile(props, lastModified)
            props
        } else {
            // file doesn't exist => store null to avoid repeated checks until file created
            cache[file] = null
            null
        }
    }

    fun getInt(project: Project, key: String, default: Int): Int =
        getPropertyValue(project, key).toIntOrNull() ?: default

    fun getBoolean(project: Project, key: String, default: Boolean): Boolean {
        val v = getPropertyValue(project, key).trim().lowercase()
        return when (v) {
            "true", "1", "yes", "on" -> true
            "false", "0", "no", "off" -> false
            else -> default
        }
    }

    fun getDouble(project: Project, key: String, default: Double): Double =
        getPropertyValue(project, key).toDoubleOrNull() ?: default

    fun getList(project: Project, key: String, delimiter: String): List<String> {
        val v = getPropertyValue(project, key)
        return if (v.isBlank()) emptyList() else v.split(delimiter)
            .map { it.trim() }.filter { it.isNotEmpty() }
    }
}