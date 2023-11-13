import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import kotlinx.html.HEAD
import kotlinx.html.meta
import org.commonmark.node.Text
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

fun HEAD.meta(property: String, content: String) {
	meta {
		attributes["property"] = property
		this.content = content
	}
}

kobweb {
	export {
		includeSourceMap = false
	}

	markdown {
		routeOverride = { route ->
			"/articles/${route.splitCamelCase().joinToString("-") { word -> word.lowercase() }}"
		}

		handlers {
			img.set { image ->
				val altText = image.children()
					.filterIsInstance<Text>()
					.map { it.literal.escapeSingleQuotedText() }
					.joinToString("")
				this.childrenOverride = emptyList()

				""" org.jetbrains.compose.web.dom.Img(src="${image.destination}", alt="$altText") {
					|   attr("loading", "lazy")
					|   attr("decoding", "async")
					|}
				""".trimMargin()
			}
		}
	}

	app {
		index {
			val url = "https://ayfri.com"
			val author = "Pierre Roy"
			val twitterHandle = "@Ayfri_"

			val description = """
				Hi, I'm Pierre Roy, an IT student at Ynov Aix school, and I'm passionate about computer science and especially programming.
				I'm making all sorts of projects and programming by myself for years. This is my portfolio, welcome!
			""".trimIndent()

			val image = "$url/images/avatar.webp"
			val title = "$author - Portfolio"

			this.description = description

			head.apply {
				add {
					meta(charset = "utf-8")
					meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
					meta(name = "Author", content = author)

					meta(property = "og:description", content = description)
					meta(property = "og:image", content = image)
					meta(property = "og:title", content = title)
					meta(property = "og:type", content = "website")
					meta(property = "og:url", content = url)

					meta(property = "twitter:card", content = "summary")
					meta(property = "twitter:creator", content = twitterHandle)
					meta(property = "twitter:description", content = description)
					meta(property = "twitter:image", content = image)
					meta(property = "twitter:title", content = title)
					meta(property = "twitter:site", content = twitterHandle)
				}
			}
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
				implementation(libs.kotlinx.wrappers.browser)
				implementation(libs.kotlinx.serialization.json)

				implementation(npm("marked", project.extra["npm.marked.version"].toString()))
			}
		}
	}
}

tasks.withType<KotlinJsCompile>().configureEach {
	kotlinOptions.freeCompilerArgs += listOf(
		"-Xklib-enable-signature-clash-checks=false",
	)
}
