import com.varabyte.kobweb.common.text.ensureSurrounded
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import kotlinx.html.*
import org.commonmark.node.Text
import java.net.HttpURLConnection
import java.net.URI

plugins {
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.js.plain.objects)
	alias(libs.plugins.kotlin.multiplatform)

	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

operator fun <K : Any, V : Any> MapProperty<K, V>.set(key: K, value: V) {
	put(key, value)
}

operator fun <K : Any, V : Any> MapProperty<K, V>.get(key: K) = getting(key)

val portfolioGeneratedResourcesRoot = layout.buildDirectory.dir("generated/portfolio-resources")
val portfolioGeneratedKotlinRoot = layout.buildDirectory.dir("generated/portfolio-data/src/jsMain/kotlin")

val downloadDataTask = tasks.register("downloadData") {
	group = "build"
	description = "Download the portfolio GitHub snapshot as JSON for the static site."

	val kotlinOutFile = portfolioGeneratedKotlinRoot.map { it.file("io/github/ayfri/data/PortfolioSnapshot.kt") }

	outputs.file(kotlinOutFile)

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

		val raw = connection.inputStream.readBytes().decodeToString()
		// Keys are camelCased here so the browser can `JSON.parse` straight into the external interfaces,
		// instead of walking every object of the snapshot through a reviver on first paint.
		fun snakeToCamel(key: String) = key.split('_').mapIndexed { i, part ->
			if (i == 0) part else part.replaceFirstChar(Char::uppercase)
		}.joinToString("")

		fun camelCaseKeys(node: Any?): Any? = when (node) {
			is Map<*, *> -> node.entries.associate { (k, v) -> snakeToCamel(k as String) to camelCaseKeys(v) }
			is List<*> -> node.map { camelCaseKeys(it) }
			else -> node
		}

		val toWrite = runCatching {
			groovy.json.JsonOutput.toJson(camelCaseKeys(groovy.json.JsonSlurper().parseText(raw)))
		}.getOrElse { raw }

		kotlinOutFile.get().asFile.apply {
			parentFile.mkdirs()
			writeText(
				listOf(
					"package io.github.ayfri.data",
					"",
					"internal val portfolioSnapshotJson = ${'"'}${'"'}${'"'}",
					toWrite.replace("${'$'}", "${'$'}{'${'$'}'}"),
					"${'"'}${'"'}${'"'}",
				).joinToString("\n")
			)
		}
		logger.lifecycle(
			"Generated '${kotlinOutFile.get()}' (${toWrite.length / 1024} KiB)",
		)
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

val markdownConvertOutputDir = layout.buildDirectory.dir("generated/kobweb/markdown/convert/src/jsMain/kotlin")

tasks.matching { it.name == "kobwebxMarkdownConvert" }.configureEach {
	val outputDir = markdownConvertOutputDir

	doLast {
		outputDir.get().asFile.walkTopDown().filter { it.isFile }.forEach { file ->
			val text = file.readText().replace(Regex("""\$([a-zA-Z_]\w*)""")) { match ->
				"""${'$'}{"$"}${match.groupValues[1]}"""
			}
			file.writeText(text)
		}
	}
}

kobweb {
	val projectGroup = group
	val blogInputDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/articles")

	markdown {
		fun String.escapeQuotes() = this.replace("\"", "\\\"")
		fun String.escapeVariables() = this.replace("$", """${'$'}{"$"}""")

		handlers {
			text.set { text ->
				"org.jetbrains.compose.web.dom.Text(\"\"\"${text.literal.escapeVariables()}\"\"\")"
			}

			img.set { image ->
				val altText = image.children()
					.filterIsInstance<Text>().joinToString("") { it.literal.escapeSingleQuotedText() }
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

		process = { markdownFiles ->
			val blogEntries = mutableListOf<BlogEntry>()

			markdownFiles.forEach { entry ->
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

			generateKotlin("$projectGroup/articles.kt", buildString {
				appendLine("// This file is generated. Modify the build script if you need to change it.")
				appendLine()
				appendLine("package io.github.ayfri")
				appendLine()
				appendLine("import io.github.ayfri.data.ArticleEntry")
				appendLine()
				appendLine("val articlesEntries = listOf${if (blogEntries.isEmpty()) "<ArticleEntry>" else ""}( ")

				blogEntries.sortedByDescending(BlogEntry::date).forEach { entry ->
					val blogInputFile = blogInputDir.asFile.resolve(entry.file.name)
					val content = blogInputFile.readText().substringAfter("\n---")
						.escapeVariables()
						.replace("\"\"\"", "\${\"\\\"\\\"\\\"\"}")

					appendLine("	ArticleEntry(\"/articles/${
						entry.file.nameWithoutExtension
							.splitCamelCase()
							.joinToString("-") { word -> word.lowercase() }
							.ensureSurrounded("", "/")
					}\",")
					appendLine("		\"${entry.date}\",")
					appendLine("		\"${entry.title.escapeQuotes().escapeVariables()}\",")
					appendLine("		\"${entry.desc.escapeQuotes().escapeVariables()}\",")
					appendLine("		\"${entry.navTitle.escapeQuotes().escapeVariables()}\",")
					appendLine("		listOf(${entry.keywords.joinToString { "\"${it.escapeQuotes().escapeVariables()}\"" }}),")
					appendLine("		\"${entry.dateModified}\",")
					appendLine("		\"\"\"$content\"\"\"")
					appendLine("	),")
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

			faviconPath = "/logo.png"

			this.description = description

			head.apply {
				add {
					fun HEAD.meta(property: String, content: String) {
						meta {
							attributes["property"] = property
							this.content = content
						}
					}

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

					link(rel="preconnect", href="https://fonts.googleapis.com")
					link(href = "https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300..800;1,300..800&display=swap", rel = "stylesheet")
					link(href = "/JetBrainsMono.css", rel = "preload", htmlAs = LinkAs.style) {
						attributes += "fetchpriority" to "high"
						onLoad = "this.rel='stylesheet'"
					}

					link(rel = "preload", href = "/prism.min.css", htmlAs = LinkAs.style) {
						attributes += "fetchpriority" to "low"
						onLoad = "this.rel='stylesheet'"
					}
					script(src = "/prism.min.js", type = "text/javascript") {
						attributes += "data-manual" to ""
						attributes += "fetchpriority" to "low"
						async = true
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
	val isDevProperty = project.providers.gradleProperty("kobwebEnv").orNull
	configAsKobwebApplication("portfolio")

	js {
		browser {
			commonWebpackConfig {
				val isDev = isDevProperty == "DEV"
				sourceMaps = isDev
				devServer?.open = false
			}
		}

		compilerOptions {
			target = "es2015"
			useEsClasses = true

			freeCompilerArgs.addAll(
				"-Xes-long-as-bigint",
				"-Xgenerate-polyfills=false",
				"-Xir-generate-inline-anonymous-functions",
				"-Xir-property-lazy-initialization",
				"-Xwarning-level=NOTHING_TO_INLINE:disabled",
			)
		}

		useEsModules()

		binaries.executable()
	}

	sourceSets {
		jsMain {
			resources.srcDir(portfolioGeneratedResourcesRoot)
			kotlin.srcDir(portfolioGeneratedKotlinRoot)

			dependencies {
				// Minifier for the production bundle, see `webpack.config.d/00-bundle-speed.js`.
				implementation(devNpm("@swc/core", libs.versions.swc.get()))
			}
		}
		commonMain {
			dependencies {
				implementation(libs.compose.html.core)
				implementation(libs.compose.runtime)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kotlinx.wrappers.browser)
				implementation(npm("marked", libs.versions.marked.get()))
			}
		}
	}
}

val generateSitemapTask = tasks.register("generateSitemap") {
	group = "build"
	description = "Generate sitemap.xml listing all canonical URLs."

	val sitemapOut = portfolioGeneratedResourcesRoot.map { it.file("public/sitemap.xml") }
	val articleDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/articles")
	val pagesDir = layout.projectDirectory.dir("src/jsMain/kotlin/io/github/ayfri/pages")

	inputs.dir(articleDir)
	inputs.dir(pagesDir)
	outputs.file(sitemapOut)

	doLast {
		val baseUrl = "https://ayfri.com"
		val routes = mutableListOf<String>()

		// Discover static routes from @Page-annotated Kotlin files under pages/. The annotation's own route wins over
		// the file path when it has one, so `@Page("/404")` is not published as `/errors/` and dynamic routes are skipped.
		val pageRegex = Regex("""@Page(?:\("([^"]*)"\))?""")
		pagesDir.asFile.walkTopDown()
			.filter { it.isFile && it.extension == "kt" }
			.forEach { file ->
				val override = pageRegex.find(file.readText())?.groupValues?.get(1) ?: return@forEach

				val route = if (override.isNotEmpty() && override.startsWith("/")) {
					override.removeSuffix("index").ensureSurrounded("/", "/")
				} else if (override.isNotEmpty()) {
					// A relative override is resolved against the file's own directory (e.g. `{user}/{project}`).
					val dir = file.parentFile.relativeTo(pagesDir.asFile).invariantSeparatorsPath
					("/$dir/$override").replace("//", "/").ensureSurrounded("/", "/")
				} else {
					val parts = file.relativeTo(pagesDir.asFile).invariantSeparatorsPath
						.removeSuffix(".kt")
						.split('/')
					buildString {
						append('/')
						parts.forEachIndexed { i, part ->
							if (part != "Index") {
								if (i > 0 && !endsWith('/')) append('/')
								append(part.splitCamelCase().joinToString("-") { it.lowercase() })
							}
						}
						if (!endsWith('/')) append('/')
					}
				}

				// `{}` marks a dynamic route with no single canonical URL; /404/ is served as a noindex error page.
				if (!route.contains('{') && route != "/404/") routes.add(route)
			}

		// Discover article routes + lastmod from markdown frontmatter
		val lastmodMap = mutableMapOf<String, String>()
		val routeRegex = Regex("""routeOverride:\s*(\S+)""")
		val dateRegex = Regex("""date-modified:\s*(\S+)""")
		for (f in articleDir.asFile.listFiles() ?: emptyArray()) {
			if (f.extension != "md") continue
			val content = f.readText()
			val routeOverride = routeRegex.find(content)?.groupValues?.getOrNull(1) ?: continue
			val dateModified = dateRegex.find(content)?.groupValues?.getOrNull(1) ?: continue
			var clean = routeOverride.removeSuffix("/index").removeSuffix("index")
			if (!clean.endsWith('/')) clean += '/'
			lastmodMap[clean] = dateModified
			routes.add(clean)
		}

		routes.sort()

		val xml = buildString {
			appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
			appendLine("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
			for (path in routes) {
				appendLine("  <url>")
				appendLine("    <loc>$baseUrl$path</loc>")
				lastmodMap[path]?.let { appendLine("    <lastmod>$it</lastmod>") }
				appendLine("  </url>")
			}
			appendLine("</urlset>")
		}

		val out = sitemapOut.get().asFile
		out.parentFile.mkdirs()
		out.writeText(xml)
		logger.lifecycle("Generated '${out}' (${xml.length} chars, ${routes.size} URLs)")
	}
}

tasks.named("jsProcessResources") {
	dependsOn(downloadDataTask, generateSitemapTask)
}

tasks.matching { it.name.endsWith("KotlinJs") }.configureEach {
	dependsOn(downloadDataTask)
}

// The export discards the source map (`includeSourceMap = false`), so building it only slows minification down.
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>()
	.matching { it.name == "jsBrowserProductionWebpack" }
	.configureEach { sourceMaps = false }
