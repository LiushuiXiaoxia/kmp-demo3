plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.googleKsp) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

val checkSourceFormatting = tasks.register("checkSourceFormatting") {
    notCompatibleWithConfigurationCache("Runs direct file-system inspection over source files.")
    doLast {
        val sourceFiles = fileTree(rootDir) {
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**", ".gradle/**", "iosApp/**")
        }

        val violations = mutableListOf<String>()
        sourceFiles.forEach { file ->
            file.readLines().forEachIndexed { index, line ->
                if ('\t' in line) {
                    violations += "${file.relativeTo(rootDir)}:${index + 1} contains a tab character"
                }
                if (line.endsWith(" ") || line.endsWith("\t")) {
                    violations += "${file.relativeTo(rootDir)}:${index + 1} has trailing whitespace"
                }
            }
        }

        if (violations.isNotEmpty()) {
            error(
                buildString {
                    appendLine("Source formatting violations detected:")
                    violations.forEach(::appendLine)
                },
            )
        }
    }
}

tasks.register("verify") {
    dependsOn(
        checkSourceFormatting,
        ":composeApp:compileKotlinJvm",
        ":composeApp:compileKotlinWasmJs",
        ":composeApp:compileAndroidMain",
        ":composeApp:allTests",
    )
}
