package io.github.ayfri

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

const val ARTICLES_PATH = "../src/jsMain/resources/markdown/articles/"
const val EXPORT_PATH = "../src/jsMain/resources/public/json-ld/"

fun main() {
	val articlesFolder = File(ARTICLES_PATH).canonicalFile
	println("Generating JSON-LD for articles in '${articlesFolder.absolutePath}'")
	val articles = articlesFolder.listFiles() ?: return

	articles.forEach {
		println("Generating JSON-LD for '${it.name}'")
		val articleData = readArticleData(it.readText())

		println("Article data: $articleData")

		val blogPost = BlogArticleJsonLD(
			headline = articleData.title,
			url = "https://ayfri.com/articles/${it.nameWithoutExtension}",
			datePublished = articleData.date,
			author = PersonJsonLD(
				name = "Ayfri",
				url = "https://ayfri.com",
				sameAs = listOf(
					"https://github.com/Ayfri"
				),
			),
			keywords = articleData.navTitle.split(" ")
		)

		val json = Json { prettyPrint = true; encodeDefaults = true }.encodeToString(blogPost)
		val outputJsonFile = File("$EXPORT_PATH${it.nameWithoutExtension}.json").canonicalFile
		outputJsonFile.parentFile.mkdirs()
		println("Writing JSON-LD to '${outputJsonFile.absolutePath}'")
		outputJsonFile.writeText(json)
	}
}
