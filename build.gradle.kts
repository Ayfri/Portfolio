import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary

plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	id("org.jetbrains.compose")
}

group = "fr.ayfri"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
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
				implementation("app.softwork:routing-compose:${project.extra["compose.routing"]}")
				implementation(npm("marked", project.extra["npm.marked.version"].toString()))

				val ktorVersion = project.extra["ktor.version"] as String
				implementation("io.ktor:ktor-client-core:$ktorVersion")
				implementation("io.ktor:ktor-client-js:$ktorVersion")

				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.extra["serialization.json.version"]}")
			}
		}
	}
}

tasks.withType<KotlinJsCompile>().configureEach {
	kotlinOptions.freeCompilerArgs += listOf(
		"-Xklib-enable-signature-clash-checks=false",
	)
}
