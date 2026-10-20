import com.varabyte.kobweb.common.text.ensureSurrounded
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import kotlinx.html.*
import org.commonmark.node.Text
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
				// Dates are formatted as "2023-11-13" and become midnight in Paris, e.g. "2023-11-13T00:00:00+01:00".
				fun String.atParisMidnight() =
					LocalDate.parse(this).atStartOfDay(ZoneId.of("Europe/Paris")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
				val dateCreatedComplete = dateCreated.atParisMidnight()
				val dateModifiedComplete = dateModified.atParisMidnight()

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

			val description = "Pierre Roy (Ayfri), a French full-stack and AI developer and open-source creator. " +
				"Projects, experience and a technical blog on Kotlin, Kore and web development."

			globals["author"] = author
			globals["description"] = description
			globals["url"] = url

			faviconPath = "/logo.png"

			head.apply {
				add {
					fun HEAD.meta(property: String, content: String) {
						meta {
							attributes["property"] = property
							this.content = content
						}
					}

					meta(name = "Author", content = author)

					meta(property = "og:locale", content = "en_US")
					meta(property = "og:site_name", content = author)

					meta(name = "twitter:creator", content = twitterHandle)
					meta(name = "twitter:site", content = twitterHandle)

					link(rel = "describedby", href = "/llms.txt")
					link(href = "/feed.xml", rel = "alternate", type = "application/rss+xml") {
						attributes["title"] = "Pierre Roy's Blog"
					}
					link(rel="preconnect", href="https://fonts.googleapis.com")
					link(href = "https://fonts.googleapis.com/css2?family=Source+Sans+3:ital,wght@0,300..800;1,300..800&display=swap", rel = "stylesheet")
					link(href = "/JetBrainsMono.css", rel = "preload", htmlAs = LinkAs.style) {
						attributes += "fetchpriority" to "high"
						onLoad = "this.rel='stylesheet'"
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
		all {
			// `transitions`, `transform` and friends are all marked experimental, every stylesheet uses them.
			languageSettings.optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
		}

		jsMain {
			resources.srcDir(files(portfolioGeneratedResourcesRoot).builtBy("generateSitemap", "generateLlmsFiles"))
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
				implementation(libs.kobwebx.silk.icons.lucide)
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
			var clean = routeOverride.removeSuffix("/index")
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

class Link(val label: String, val url: String, val note: String)

val generateLlmsFilesTask = tasks.register("generateLlmsFiles") {
	group = "build"
	description = "Generate llms.txt, llms-full.txt, a Markdown copy of every article and the RSS feed for LLMs, agents and feed readers."

	val articleDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/articles")
	val publicOut = portfolioGeneratedResourcesRoot.map { it.dir("public") }
	val articlesOut = publicOut.map { it.dir("articles") }
	val llmsOut = publicOut.map { it.file("llms.txt") }
	val llmsFullOut = publicOut.map { it.file("llms-full.txt") }
	val feedOut = publicOut.map { it.file("feed.xml") }

	inputs.dir(articleDir)
	outputs.dir(articlesOut)
	outputs.files(llmsOut, llmsFullOut, feedOut)

	doLast {
		val baseUrl = "https://ayfri.com"
		val name = "Pierre Roy (Ayfri)"
		val frontMatterKey = Regex("""^([\w-]+):\s*(.*)$""")
		val markdownLink = Regex("""\[([^\]]*)]\([^)]*\)""")

		val rootRelativeLink = Regex("""\]\(/(?!/)""")

		class Article(
			val slug: String,
			val title: String,
			val description: String,
			val created: String,
			val body: String,
			val modified: String,
		) {
			val url = "$baseUrl/articles/$slug/"
			val markdown = "# $title\n\n> $description\n\nPublished $created, updated $modified. Canonical: $url\n\n$body\n"
		}

		/** A missing field fails the build: `ArticleLayout` always links the Markdown copy, which would be a 404. */
		val articles = (articleDir.asFile.listFiles() ?: emptyArray()).filter { it.extension == "md" }.map { file ->
			val lines = file.readText().lines()
			val end = lines.drop(1).indexOfFirst { it.trim() == "---" } + 1
			if (lines.first().trimStart('﻿').trim() != "---" || end == 0) {
				throw GradleException("'${file.name}' has no front matter.")
			}

			val fields = lines.subList(1, end)
				.mapNotNull { frontMatterKey.find(it)?.destructured }
				.associate { (key, value) -> key to value.trim().removeSurrounding("\"").removeSurrounding("'") }
			val missing = listOf("title", "description", "routeOverride").filter { fields[it].isNullOrBlank() }
			if (missing.isNotEmpty()) throw GradleException("'${file.name}' is missing the front matter fields $missing.")

			val invalidDates = listOf("date-created", "date-modified").filter { key -> fields[key]?.let { runCatching { LocalDate.parse(it) }.isFailure } == true }
			if (invalidDates.isNotEmpty()) throw GradleException("'${file.name}' has invalid dates in $invalidDates, expected yyyy-MM-dd.")

			val created = fields["date-created"].orEmpty()
			Article(
				slug = fields.getValue("routeOverride").removeSuffix("/index").trim('/').removePrefix("articles/"),
				title = fields.getValue("title"),
				description = fields.getValue("description"),
				created = created,
				modified = fields["date-modified"] ?: created,
				body = lines.drop(end + 1).joinToString("\n").trim().replace(rootRelativeLink, "]($baseUrl/"),
			)
		}.sortedWith(compareByDescending<Article> { it.created }.thenBy { it.title })

		val articlesDir = articlesOut.get().asFile
		articlesDir.deleteRecursively()
		articles.forEach { article ->
			articlesDir.resolve("${article.slug}/index.md").apply { parentFile.mkdirs() }.writeText(article.markdown)
		}

		val summary = "Pierre Roy (Ayfri) is a French full-stack and AI developer and open-source creator. " +
			"This site is his portfolio and a technical blog on Kotlin, Kore (Minecraft datapacks) and web development."

		val pages = listOf(
			Link("Home", "$baseUrl/", "hero, featured projects and a short timeline"),
			Link("About me", "$baseUrl/about-me/", "his story, from first programs in 2014 to Kotlin, Minecraft modding, AI and web projects"),
			Link("Experiences", "$baseUrl/experiences/", "work and internships at Link2Brain, Eliophot, Ynov and BlueFrog"),
			Link("Skills", "$baseUrl/skills/", "languages, frameworks and tools, each backed by real projects"),
			Link("Portfolio", "$baseUrl/portfolio/", "how this Kotlin and Compose for Web portfolio was designed and built"),
			Link("Projects", "$baseUrl/projects/", "open-source projects on GitHub, searchable by language and tag"),
			Link("Blog", "$baseUrl/articles/", "all articles, searchable and filterable by tag"),
			Link("CV in English (PDF)", "$baseUrl/cv-en.pdf", "the resume in English"),
			Link("CV in French (PDF)", "$baseUrl/cv.pdf", "the resume in French"),
		)

		val optional = listOf(
			Link("All articles in one file", "$baseUrl/llms-full.txt", "every article above as a single Markdown document"),
			Link("RSS feed", "$baseUrl/feed.xml", "every article, newest first"),
			Link("GitHub", "https://github.com/Ayfri", "open-source projects and contributions"),
			Link("LinkedIn", "https://www.linkedin.com/in/pierre-roy-ayfri/", "professional profile"),
			Link("Portfolio source code", "https://github.com/Ayfri/Portfolio", "the source of this site"),
			Link("X", "https://x.com/ayfri_", "updates and announcements"),
			Link("Twitch", "https://www.twitch.tv/ayfri_", "live streams"),
			Link("Discord", "https://discord.gg/invite/BySjRNQ9Je", "community server"),
			Link("Kore documentation for LLMs", "https://kore.ayfri.com/llms.txt", "llms.txt of the Kore datapack library docs"),
		)

		val articleLinks = articles.map {
			Link(it.title, "${it.url}index.md", it.description.replace(markdownLink) { match -> match.groupValues[1] })
		}

		fun section(title: String, links: List<Link>) =
			"## $title\n\n" + links.joinToString("\n") { "- [${it.label}](${it.url}): ${it.note}" } + "\n"

		val intro = "Pierre is based in France and holds a Data Scientist Master's degree from Ynov Aix (2026). " +
			"He builds web products with Kotlin, Svelte and TypeScript, ships AI features in Python, " +
			"and maintains Kore, the Kotlin library used to generate Minecraft datapacks. " +
			"He worked as a full-stack developer at Eliophot (2023 to 2025) and as lead full-stack and AI developer at Link2Brain (2025 to 2026). " +
			"His side projects include PokéCards-Collector, Atom Clicker and GPT Images."

		llmsOut.get().asFile.writeText(
			listOf(
				"# $name\n\n> $summary\n\n$intro\n\nEvery HTML page on ayfri.com also returns Markdown when requested with an `Accept: text/markdown` header. Every article is also available as Markdown at `/articles/<slug>/index.md`.\n",
				section("Articles", articleLinks),
				section("Pages", pages),
				section("Optional", optional),
			).joinToString("\n")
		)

		llmsFullOut.get().asFile.writeText(
			"# $name - articles\n\n> $summary\n\n" + articles.joinToString("\n\n---\n\n") { it.markdown }
		)

		fun String.xml() = replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
		fun String.rfc1123() = LocalDate.parse(this).atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)

		val lastBuildDate = articles.maxOfOrNull { it.modified }?.takeIf { it.isNotBlank() }
		val items = articles.joinToString("\n") { article ->
			val pubDate = if (article.created.isBlank()) "" else "\n<pubDate>${article.created.rfc1123()}</pubDate>"
			val plainDescription = article.description.replace(markdownLink) { match -> match.groupValues[1] }.replace("`", "")
			"<item>\n<title>${article.title.xml()}</title>\n<link>${article.url}</link>\n" +
				"<guid isPermaLink=\"true\">${article.url}</guid>\n<description>${plainDescription.xml()}</description>$pubDate\n</item>"
		}

		feedOut.get().asFile.writeText(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n<channel>\n" +
				"<title>Pierre Roy's Blog</title>\n<link>$baseUrl/articles/</link>\n<description>${summary.xml()}</description>\n<language>en</language>\n" +
				(lastBuildDate?.let { "<lastBuildDate>${it.rfc1123()}</lastBuildDate>\n" } ?: "") +
				"<atom:link href=\"$baseUrl/feed.xml\" rel=\"self\" type=\"application/rss+xml\"/>\n$items\n</channel>\n</rss>\n"
		)

		logger.lifecycle("Generated llms.txt, llms-full.txt, feed.xml and ${articles.size} article Markdown files in '${publicOut.get()}'")
	}
}

tasks.named("jsProcessResources") {
	dependsOn(downloadDataTask)
}

tasks.matching { it.name.endsWith("KotlinJs") }.configureEach {
	dependsOn(downloadDataTask)
}

// The export discards the source map (`includeSourceMap = false`), so building it only slows minification down.
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>()
	.matching { it.name == "jsBrowserProductionWebpack" }
	.configureEach { sourceMaps = false }
