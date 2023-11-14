plugins {
	kotlin("jvm")
	alias(libs.plugins.kotlinx.serialization)
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
}

application {
	mainClass = "io.github.ayfri.MainKt"
}
