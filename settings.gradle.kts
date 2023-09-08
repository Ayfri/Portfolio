rootProject.name = "portfolio"

pluginManagement {
	repositories {
		mavenCentral()
	}

	plugins {
		val kotlinVersion = extra["kotlin.version"] as String
		kotlin("jvm") version kotlinVersion
		kotlin("plugin.serialization") version kotlinVersion
	}
}
