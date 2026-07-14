plugins {
	alias(libs.plugins.loom)
}

group = "me.maximumpower55"

val buildNum = providers.environmentVariable("GITHUB_RUN_NUMBER")
    .filter(String::isNotEmpty)
	.map { "build.$it" }
    .orElse("local")
    .get()

version = "0.1.0+$buildNum-mc${libs.versions.minecraft.get()}"

dependencies {
    minecraft(libs.minecraft)
	api(libs.bundles.fabric)

    include(libs.lwjgl.sdl)
    implementation(libs.lwjgl.sdl)
    val platforms = listOf("macos", "macos-arm64", "linux", "windows", "windows-arm64", "windows-x86")
    platforms.forEach { platform ->
        val module = variantOf(libs.lwjgl.sdl) { classifier("natives-$platform") }
        include(module)
        runtimeOnly(module)
    }
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

sourceSets {
    create("backend") {
        java {
            compileClasspath += configurations.compileClasspath.get()
        }
    }

    named("main") {
        java {
            compileClasspath += sourceSets["backend"].output
            runtimeClasspath += sourceSets["backend"].output
        }
    }
}

tasks.withType<Jar> {
    from(sourceSets["backend"].output)

    from(rootProject.file("LICENSE"))
}

tasks.processResources {
    inputs.properties(
        "version" to version,
        "minecraft_version" to libs.versions.minecraft.get().replace("snapshot-", "alpha.").replace("pre-", "pre."),
        "loader_version" to libs.versions.loader.get()
    )

    filesMatching("fabric.mod.json") {
        expand(inputs.properties)
    }
}

loom {
    accessWidenerPath = file("src/main/resources/sapphire.classtweaker")

	runs {
        configureEach {
            preferGradleTask = true

            systemProperties.put("mixin.debug.export", "true")
            jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
            jvmArguments.add("-XX:+IgnoreUnrecognizedVMOptions")
        }
	}
}
