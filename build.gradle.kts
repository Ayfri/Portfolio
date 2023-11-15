import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import kotlinx.html.*
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
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

val blogInputDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/articles")

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

				"""org.jetbrains.compose.web.dom.Img(src="${image.destination}", alt="$altText") {
					|   attr("loading", "lazy")
					|   attr("decoding", "async")
					|}
				""".trimMargin()
			}

			code.set { code ->
				val text = "\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\""

				"""io.github.ayfri.CodeBlock($text, "${code.info.takeIf { it.isNotBlank() }}")"""
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
					title(title)
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

class MarkdownVisitor : AbstractVisitor() {
	private val _frontMatter = mutableMapOf<String, List<String>>()
	val frontMatter: Map<String, List<String>> = _frontMatter

	override fun visit(customBlock: CustomBlock) {
		if (customBlock is YamlFrontMatterBlock) {
			val yamlVisitor = YamlFrontMatterVisitor()
			customBlock.accept(yamlVisitor)
			_frontMatter.putAll(
				yamlVisitor.data
					.mapValues { (_, values) ->
						values.map { it.yamlStringToKotlinString() }
					}
			)
		}
	}
}

data class BlogEntry(
	val file: File,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val tags: List<String>,
)

fun String.escapeQuotes() = this.replace("\"", "\\\"")

val generateBlogSourceTask = task("generateBlogSource") {
	group = "io/github/ayfri"
	val blogGenDir = layout.buildDirectory.dir("generated/ayfri/src/jsMain/kotlin").get()
	blogInputDir.asFile.mkdirs()

	inputs.dir(blogInputDir)
		.withPropertyName("blogArticles")
		.withPathSensitivity(PathSensitivity.RELATIVE)
	outputs.dir(blogGenDir)
		.withPropertyName("blogGeneratedSource")

	doLast {
		val parser = kobweb.markdown.features.createParser()
		val blogEntries = mutableListOf<BlogEntry>()

		blogInputDir.asFileTree.forEach { blogArticle ->
			val rootNode = parser.parse(blogArticle.readText())
			val visitor = MarkdownVisitor()

			rootNode.accept(visitor)

			val fm = visitor.frontMatter
			val requiredFields = listOf("title", "description", "date", "nav-title")
			val (title, desc, date, navTitle) = requiredFields
				.map { key -> fm[key]?.singleOrNull() }
				.takeIf { values -> values.all { it != null } }
				?.requireNoNulls()
				?: run {
					val missingFields = requiredFields.filter { key -> fm[key] == null }
					println("Skipping $blogArticle in the listing as it is missing required frontmatter fields ($missingFields)")
					return@forEach
				}

			val tags = fm["tags"] ?: emptyList()
			blogEntries.add(BlogEntry(blogArticle.relativeTo(blogInputDir.asFile), date, title, desc, navTitle, tags))
		}

		blogGenDir.file("$group/articles.kt").asFile.apply {
			parentFile.mkdirs()
			writeText(buildString {
				appendLine(
					"""
					|// This file is generated. Modify the build script if you need to change it.
					|
					|package io.github.ayfri.articles
					|
					|import io.github.ayfri.components.ArticleEntry
					|
					|val articlesEntries = listOf${if (blogEntries.isEmpty()) "<ArticleEntry>" else ""}(
                    """.trimMargin()
				)
				blogEntries.sortedByDescending { it.date }.forEach { entry ->
					appendLine(
						"""    ArticleEntry("/articles/${
							entry.file.path.substringBeforeLast(".md").splitCamelCase().joinToString("-") { word -> word.lowercase() }
						}", "${entry.date}", "${entry.title.escapeQuotes()}", "${entry.desc.escapeQuotes()}", "${entry.navTitle.escapeQuotes()}"),"""
					)
				}

				appendLine(")")
			})

			println("Generated $absolutePath")
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
			kotlin.srcDir(generateBlogSourceTask)

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
