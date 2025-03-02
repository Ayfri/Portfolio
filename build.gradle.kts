import com.varabyte.kobweb.common.text.ensureSurrounded
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import kotlinx.html.*
import org.commonmark.node.Text
import java.net.HttpURLConnection
import java.net.URI

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.kotlin.compose)

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
}

fun HEAD.meta(property: String, content: String) {
	meta {
		attributes["property"] = property
		this.content = content
	}
}

operator fun <K : Any, V : Any> MapProperty<K, V>.set(key: K, value: V) {
	put(key, value)
}

operator fun <K : Any, V : Any> MapProperty<K, V>.get(key: K) = getting(key)

val blogInputDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/articles")

val downloadDataTask = tasks.register("downloadData") {
	val file = layout.buildDirectory.file("generated/ayfri/src/jsMain/kotlin/io/github/ayfri/data/Data.kt").get().asFile
	outputs.dir(file.parentFile)

	doLast {
		val dataLink = "https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json"
		val url = URI(dataLink).toURL()
		val connection = url.openConnection() as HttpURLConnection
		connection.requestMethod = "GET"
		connection.connect()
		val responseCode = connection.responseCode
		if (responseCode != 200) {
			throw Exception("Error while downloading data, response code: $responseCode")
		}

		var data = connection.inputStream.readBytes().decodeToString()
		data = data.replace(Regex("\\$"), "\\\${\"\\$\"}")
		val tripleQuotes = "\"\"\""
		val kotlinOutput = """
			package io.github.ayfri.data
			
			const val rawData = $tripleQuotes$data$tripleQuotes
		""".trimIndent()

		file.parentFile.mkdirs()
		file.writeText(kotlinOutput)
		logger.lifecycle("Generated '$file'")
	}
}

data class BlogEntry(
	val file: File,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val keywords: List<String>,
	val dateModified: String,
)

fun String.escapeQuotes() = this.replace("\"", "\\\"")

kobweb {
	markdown {
		handlers {
			img.set { image ->
				val altText = image.children()
					.filterIsInstance<Text>()
					.map { it.literal.escapeSingleQuotedText() }
					.joinToString("")
				this.childrenOverride = emptyList()

				"""org.jetbrains.compose.web.dom.Img(src="${image.destination}", alt="$altText") {
					|   attr("loading", "lazy")
					|   attr("decoding", "async")
					|}
				""".trimMargin()
			}

			code.set { code ->
				val text = "\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\""

				"""io.github.ayfri.components.CodeBlock($text, "${code.info.takeIf { it.isNotBlank() }}")"""
			}
		}

		process = { markdownFile ->
			val blogEntries = mutableListOf<BlogEntry>()

			markdownFile.forEach { entry ->
				val path = File(entry.filePath)
				val fileName = path.name
				val fm = entry.frontMatter
				val requiredFields = listOf("title", "description", "date-created", "date-modified", "nav-title")
				val title = fm["title"]?.firstOrNull()
				val desc = fm["description"]?.firstOrNull()
				val dateCreated = fm["date-created"]?.firstOrNull()
				val dateModified = fm["date-modified"]?.firstOrNull()
				val navTitle = fm["nav-title"]?.firstOrNull()

				if (title == null || desc == null || dateCreated == null || dateModified == null || navTitle == null) {
					println("Skipping '$fileName', missing required fields in front matter of $fileName: ${requiredFields.filter { fm[it] == null }}")
					return@forEach
				}

				val keywords = fm["keywords"]?.firstOrNull()?.split(Regex(",\\s*")) ?: emptyList()
				// Dates are only formatted in this format "2023-11-13"
				val dateCreatedComplete = dateCreated.split("-").let { (year, month, day) ->
					"$year-$month-${day}T00:00:00.000000000+01:00"
				}
				val dateModifiedComplete = dateModified.split("-").let { (year, month, day) ->
					"$year-$month-${day}T00:00:00.000000000+01:00"
				}

				blogEntries.add(
					BlogEntry(
						file = path,
						date = dateCreatedComplete,
						title = title,
						desc = desc,
						navTitle = navTitle,
						keywords = keywords,
						dateModified = dateModifiedComplete
					)
				)
			}

			generateKotlin("$group/articles.kt", buildString {
				appendLine(
					"""
					|// This file is generated. Modify the build script if you need to change it.
					|
					|package io.github.ayfri
					|
					|import io.github.ayfri.components.ArticleEntry
					|
					|val articlesEntries = listOf${if (blogEntries.isEmpty()) "<ArticleEntry>" else ""}(
					""".trimMargin()
				)

				fun List<String>.asCode() = "listOf(${joinToString { "\"$it\"" }})"

				blogEntries.sortedByDescending { it.date }.forEach { entry ->
					appendLine(
						"""    ArticleEntry("/articles/${
							entry.file.nameWithoutExtension
								.splitCamelCase()
								.joinToString("-") { word -> word.lowercase() }
								.ensureSurrounded("", "/")
						}", "${entry.date}", "${entry.title.escapeQuotes()}", "${entry.desc.escapeQuotes()}", "${entry.navTitle.escapeQuotes()}", ${
							entry.keywords.asCode()
						}, "${entry.dateModified}"),
						""".trimMargin()
					)
				}

				appendLine(")")
			})
		}
	}

	app {
		export {
			includeSourceMap = false
		}

		index {
			val url = "https://ayfri.com"
			val author = "Pierre Roy"
			val twitterHandle = "@Ayfri_"

			val description = """
				Hi, I'm Pierre Roy, an IT student, and I'm passionate about computer science and especially programming.
				Discover my projects and my blog on this website.
			""".trimIndent()

			val image = "$url/images/avatar.webp"

			globals["author"] = author
			globals["description"] = description
			globals["url"] = url

			this.description = description

			head.apply {
				add {
					meta(charset = "utf-8")
					meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
					meta(name = "Author", content = author)

					meta(property = "og:description", content = description)
					meta(property = "og:image", content = image)
					meta(property = "og:type", content = "website")
					meta(property = "og:url", content = url)

					meta(property = "twitter:card", content = "summary")
					meta(property = "twitter:creator", content = twitterHandle)
					meta(property = "twitter:description", content = description)
					meta(property = "twitter:image", content = image)
					meta(property = "twitter:site", content = twitterHandle)

					link(href = "https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;700&display=swap", rel = "stylesheet")
					link(href = "https://dev-cats.github.io/code-snippets/JetBrainsMono.css", rel = "stylesheet")

					link(href = "/prism.min.css", rel = "stylesheet")
					script(src = "/prism.min.js", type = "text/javascript") {
						attributes += "data-manual" to ""
					}

					script(src = "https://kit.fontawesome.com/74fed0e2b5.js", type = "text/javascript") {
						async = true
					}

					script(src = "https://www.googletagmanager.com/gtag/js?id=G-TS3BHPVFKK", type = "text/javascript") {
						defer = true
					}

					script(type = "text/javascript") {
						unsafe {
							raw(
								"""
							function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag('js',new Date),gtag('config','G-TS3BHPVFKK')
							""".trimIndent()
							)
						}
					}
				}
			}
		}
	}
}

kotlin {
	configAsKobwebApplication("portfolio")

	js {
		browser {
			commonWebpackConfig {
				val isDev = project.findProperty("kobwebEnv") == "DEV"
				sourceMaps = isDev
				devServer?.open = false
			}
		}

		compilerOptions {
			target = "es2015"
			useEsClasses = true
		}

		useEsModules()

		binaries.executable()
	}

	sourceSets {
		jsMain {
			kotlin.srcDir(downloadDataTask)

			dependencies {
				implementation(libs.compose.html.core)
				implementation(libs.compose.runtime)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobweb.core)
				implementation(libs.kotlinx.wrappers.browser)
				implementation(libs.kotlinx.serialization.json)
				implementation(npm("marked", project.extra["npm.marked.version"].toString()))
			}
		}
	}
}
