package io.github.ayfri.jsonld

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class JsonLD {
	@JsName("context")
	val `@context` = "https://schema.org"
}

@Serializable
@SerialName("Graph")
class GraphJsonLD(
	@JsName("graph")
	val `@graph`: List<JsonLD>,
) : JsonLD()

@Serializable
@SerialName("BlogPosting")
class BlogArticleJsonLD(
	val headline: String,
	val url: String,
	val author: JsonLD,
	val datePublished: String,
	val dateModified: String? = null,
	val image: String? = null,
	val inLanguage: String? = null,
	val keywords: List<String>? = null,
) : JsonLD() {
	@Serializable
	val mainEntityOfPage: JsonLD = WebPageJsonLD(headline, url, image, keywords)
}

@Serializable
@SerialName("Person")
class PersonJsonLD(
	val name: String,
	val image: String? = null,
	val url: String? = null,
	val sameAs: List<String>? = null,
) : JsonLD()

@Serializable
@SerialName("WebPage")
class WebPageJsonLD(
	val headline: String,
	val url: String,
	val image: String? = null,
	val keywords: List<String>? = null,
) : JsonLD()

@Serializable
@SerialName("WebSite")
class WebSiteJsonLD(
	val url: String,
	val name: String,
	val sameAs: List<String>? = null,
) : JsonLD()

@Serializable
@SerialName("SoftwareSourceCode")
class SoftwareSourceCodeJsonLD(
	val name: String,
	val description: String? = null,
	val author: JsonLD,
	val codeRepository: String,
	val programmingLanguage: String? = null,
	val dateCreated: String? = null,
	val dateModified: String? = null,
	val keywords: List<String>? = null,
	val license: String? = null,
	val url: String,
) : JsonLD()

@Serializable
@SerialName("ItemList")
class ItemListJsonLD(
	val itemListElement: List<ListItemJsonLD>,
	val numberOfItems: Int = itemListElement.size,
) : JsonLD()

@Serializable
@SerialName("ListItem")
class ListItemJsonLD(
	val position: Int,
	val item: JsonLD,
) : JsonLD()

@Serializable
@SerialName("BreadcrumbList")
class BreadcrumbListJsonLD(
	val itemListElement: List<ListItemJsonLD>,
) : JsonLD()
