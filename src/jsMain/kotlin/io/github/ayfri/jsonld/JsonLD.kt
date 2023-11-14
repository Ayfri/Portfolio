package io.github.ayfri.jsonld

import kotlinx.serialization.Serializable


@Serializable
abstract class JsonLD {
	@JsName("type")
	abstract val `@type`: String

	@JsName("context")
	val `@context` = "https://schema.org"
}

@Serializable
class BlogArticleJsonLD(
	val headline: String,
	val url: String,
	val author: PersonJsonLD,
	val datePublished: String,
	val image: String? = null,
	val keywords: List<String>? = null,
) : JsonLD() {
	override val `@type` = "BlogPosting"

	@Serializable
	val mainEntityOfPage = WebPageJsonLD(headline, url, image, keywords)
}

@Serializable
class PersonJsonLD(
	val name: String,
	val image: String? = null,
	val url: String? = null,
	val sameAs: List<String>? = null,
) : JsonLD() {
	override val `@type` = "Person"
}

@Serializable
class WebPageJsonLD(
	val headline: String,
	val url: String,
	val image: String? = null,
	val keywords: List<String>? = null,
) : JsonLD() {
	override val `@type` = "WebPage"
}
