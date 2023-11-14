package io.github.ayfri

import kotlinx.serialization.Serializable


@Serializable
abstract class JsonLD {
	abstract val `@type`: String
	val `@context` = "https://schema.org"
}

@Serializable
class BlogArticleJsonLD(
	val headline: String,
	val url: String,
	val author: PersonJsonLD,
	val datePublished: String,
	val image: String = "",
	val keywords: List<String> = listOf(),
) : JsonLD() {
	override val `@type` = "BlogPosting"

	@Serializable
	val mainEntityOfPage = WebPageJsonLD(headline, url, image, keywords)
}

@Serializable
class PersonJsonLD(
	val name: String,
	val image: String = "",
	val url: String = "",
	val sameAs: List<String> = listOf(),
) : JsonLD() {
	override val `@type` = "Person"
}

@Serializable
class WebPageJsonLD(
	val headline: String,
	val url: String,
	val image: String = "",
	val keywords: List<String> = listOf(),
) : JsonLD() {
	override val `@type` = "WebPage"
}
