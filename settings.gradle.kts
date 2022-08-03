pluginManagement {
	repositories {
		mavenCentral()
	}
	
	plugins {
		kotlin("jvm").version(extra["kotlin.version"] as String)
		kotlin("plugin.serialization").version(extra["kotlin.version"] as String)
	}
}

rootProject.name = "portfolio"

