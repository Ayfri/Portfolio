plugins {
	kotlin("jvm")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.ktor:ktor-client-core:${project.property("ktor.version")}")
	implementation("io.ktor:ktor-client-cio:${project.property("ktor.version")}")
}