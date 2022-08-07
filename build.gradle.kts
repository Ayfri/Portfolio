import org.jetbrains.compose.compose

plugins {
	kotlin("js")
	kotlin("plugin.serialization")
	id("org.jetbrains.compose")
}

group "fr.ayfri"
version "1.0-SNAPSHOT"

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
	}
}

dependencies {
	implementation(compose.web.core)
	implementation(compose.runtime)
	implementation("app.softwork:routing-compose:${project.extra["compose.routing"]}")
	implementation(npm("@types/marked", project.extra["npm.@types/marked"].toString(), true))
	
	val ktorVersion = project.extra["ktor.version"] as String
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-js:$ktorVersion")
	
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.extra["serialization.json.version"]}")
}

task("compose") {
	val app = project.file("app")
}