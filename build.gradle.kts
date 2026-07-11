plugins {
	alias(libs.plugins.loom)
}

base.archivesName = "sapphire"
group = "me.maximumpower55"

val buildNum = providers.environmentVariable("GITHUB_RUN_NUMBER")
    .filter(String::isNotEmpty)
	.map { "build.$it" }
    .orElse("local")
    .get()

version = "0.1.0+$buildNum-mc${libs.versions.minecraft.get()}"

sourceSets {
    val main = getByName("main")
    val backend = create("backend")

    backend.apply {
        java {
            compileClasspath += configurations.compileClasspath.get()
        }
    }

    main.apply {
        java {
            compileClasspath += backend.output
            runtimeClasspath += backend.output
        }
    }
}

repositories {
	exclusiveContent {
        forRepositories(maven("https://api.modrinth.com/maven")).filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
	api(libs.bundles.fabric)

    api(libs.lwjgl.sdl)
    val platforms = listOf("macos", "macos-arm64", "linux", "windows", "windows-arm64", "windows-x86")
    platforms.forEach { platform ->
        val module = variantOf(libs.lwjgl.sdl) { classifier("natives-$platform") }
        include(module)
        runtimeOnly(module)
    }
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
            property("mixin.debug.export", "true")
            vmArg("-XX:+AllowEnhancedClassRedefinition")
            vmArg("-XX:+IgnoreUnrecognizedVMOptions")
        }
	}
}

java {
	withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}
