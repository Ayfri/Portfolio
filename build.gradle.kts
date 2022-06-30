import org.jetbrains.compose.compose

plugins {
	kotlin("multiplatform")
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
	
	sourceSets {
		val jsMain by getting {
			dependencies {
				implementation(compose.web.core)
				implementation(compose.runtime)
				implementation("app.softwork:routing-compose:0.2.4")
			}
		}
	}
}

// create a task to generate the app

task("compose") {
	val app = project.file("app")
}