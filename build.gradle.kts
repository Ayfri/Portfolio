import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary

plugins {
	alias(libs.plugins.kotlin.multiplatform)

	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
	alias(libs.plugins.kotlinx.serialization)
}

group = "io.github.ayfri"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

kobweb {
	app {
		index {
			description.set("Powered by Kobweb")
		}
	}
}

kotlin {
	configAsKobwebApplication("portfolio")

	js(IR) {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		binaries.executable()

		binaries.withType<JsIrBinary>().configureEach {
			val isRelease = this.mode == KotlinJsBinaryMode.PRODUCTION
			linkTask.configure {
				kotlinOptions {
					if (isRelease) {
						sourceMap = false
					} else {
						sourceMap = true
						sourceMapEmbedSources = "always"
					}
				}
			}
		}
	}

	sourceSets {
		jsMain {
			dependencies {
				implementation(compose.html.core)
				implementation(compose.runtime)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobweb.core)

				implementation(libs.compose.routing)
				implementation(npm("marked", project.extra["npm.marked.version"].toString()))

				implementation(libs.kord.core)
				implementation(libs.kord.js)
				implementation(libs.kord.serialization.json)
			}
		}
	}
}

tasks.withType<KotlinJsCompile>().configureEach {
	kotlinOptions.freeCompilerArgs += listOf(
		"-Xklib-enable-signature-clash-checks=false",
	)
}
