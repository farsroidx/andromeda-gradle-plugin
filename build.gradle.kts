plugins {
//    id("signing")
    kotlin("jvm") version "2.2.20"
    id("com.gradle.plugin-publish") version "2.0.0"
}

group = "ir.farsroidx.plugin"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

tasks.register("GenerateVersionFile") {

    val packageName = project.group
    val packagePath = packageName.toString().replace(".", "/")

    val file = file("src/main/kotlin/$packagePath/Version.kt")

    file.writeText(
        """
        package $packageName
        
        object Version {
            const val PLUGIN_VERSION   = "${project.version}"
            const val RELEASE_DATETIME = ${System.currentTimeMillis()}
        }
        """.trimIndent()
    )
}

dependencies {
    implementation(
        kotlin("gradle-plugin", version = "2.2.20")
    )
    compileOnly("com.android.tools.build:gradle:8.13.0")
}

//signing {
//    useInMemoryPgpKeys(
//        System.getenv("SIGNING_KEY_ID"),
//        System.getenv("SIGNING_KEY"),
//        System.getenv("SIGNING_PASSWORD")
//    )
//    sign(publishing.publications)
//}

gradlePlugin {

    website.set("https://github.com/farsroidx/andromeda-gradle-plugin")
    vcsUrl.set("https://github.com/farsroidx/andromeda-gradle-plugin")

    plugins {

        create("andromeda") {
            id                  = "ir.farsroidx.andromeda"
            implementationClass = "ir.farsroidx.plugin.andromeda.AndromedaPlugin"
            displayName         = "Andromeda Gradle Plugin"
            description         = """
            Andromeda Gradle Plugin by Farsroidx provides a set of pre-configured extensions, tasks, and utility functions 
            to streamline project setup and build automation. It automatically applies essential pre-build configurations, 
            adds reusable Kotlin DSL extensions, and simplifies dependency management for Android and JVM projects. 
            This plugin is designed to save development time by providing ready-to-use pre-build code, custom tasks, 
            and build conventions that can be immediately applied to any project.
            """.trimIndent()
            tags.set(
                listOf(
                    "andromeda", "gradle", "plugin", "kotlin", "utilities"
                )
            )
        }
    }
}

publishing {

    publications {

        create<MavenPublication>("maven") {

            from( components["java"] )

        }
    }
}