package io.github.ayfri.jsonld

import kotlinx.js.JsPlainObject

/**
 * Collections are JS arrays because these objects go straight to `JSON.stringify`: a Kotlin `List` only serializes
 * through its `toJSON`, which `emptyList()` lacks, so an empty one dumps Kotlin internals, including a BigInt that throws.
 */
@JsPlainObject
external interface JsonLD {
	@JsName("@context")
	val context: String

	@JsName("@type")
	val type: String
}

@JsPlainObject
external interface GraphJsonLD : JsonLD {
	@JsName("@graph")
	val graph: Array<JsonLD>
}

@JsPlainObject
external interface BlogArticleJsonLD : JsonLD {
	val author: JsonLD
	val dateModified: String?
	val datePublished: String
	val headline: String
	val image: String?
	val inLanguage: String?
	val keywords: Array<String>?
	val mainEntityOfPage: WebPageJsonLD
	val url: String
}

@JsPlainObject
external interface PersonJsonLD : JsonLD {
	val image: String?
	val jobTitle: String?
	val name: String
	val sameAs: Array<String>?
	val url: String?
}

@JsPlainObject
external interface WebPageJsonLD : JsonLD {
	val headline: String
	val image: String?
	val keywords: Array<String>?
	val url: String
}

@JsPlainObject
external interface WebSiteJsonLD : JsonLD {
	val name: String
	val sameAs: Array<String>?
	val url: String
}

@JsPlainObject
external interface SoftwareSourceCodeJsonLD : JsonLD {
	val author: JsonLD
	val codeRepository: String
	val dateCreated: String?
	val dateModified: String?
	val description: String?
	val keywords: Array<String>?
	val license: String?
	val name: String
	val programmingLanguage: String?
	val url: String
}

@JsPlainObject
external interface ItemListJsonLD : JsonLD {
	val itemListElement: Array<ListItemJsonLD>
	val numberOfItems: Int
}

@JsPlainObject
external interface ListItemJsonLD : JsonLD {
	val item: JsonLD
	val position: Int
}

@JsPlainObject
external interface BreadcrumbListJsonLD : JsonLD {
	val itemListElement: Array<ListItemJsonLD>
}
