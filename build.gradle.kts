plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	application
}

repositories {
	mavenCentral()
}

dependencies {
	val ktorVersion = project.property("ktor.version") as String
	val serializationVersion = project.property("serialization.version") as String

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

application {
	mainClass = "MainKt"
}

kotlin {
	jvmToolchain(25)
}
