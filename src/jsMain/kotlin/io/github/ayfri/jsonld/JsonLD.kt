package io.github.ayfri.jsonld

import kotlinx.js.JsPlainObject

/**
 * Collections are JS arrays because these objects go straight to `JSON.stringify`: a Kotlin `List` only serializes
 * through its `toJSON`, which `emptyList()` lacks, so an empty one dumps Kotlin internals, including a BigInt that throws.
 */
@JsPlainObject
external interface JsonLD {
	@JsName("@type")
	val type: String

	@JsName("@id")
	val id: String?
}

@JsPlainObject
external interface NodeRef {
	@JsName("@id")
	val id: String
}

@JsPlainObject
external interface GraphJsonLD {
	@JsName("@context")
	val context: String

	@JsName("@graph")
	val graph: Array<JsonLD>
}

@JsPlainObject
external interface PersonJsonLD : JsonLD {
	val alternateName: String?
	val image: String?
	val jobTitle: String?
	val name: String
	val sameAs: Array<String>?
	val url: String?
}

@JsPlainObject
external interface WebSiteJsonLD : JsonLD {
	val alternateName: String?
	val inLanguage: String
	val name: String
	val publisher: NodeRef
	val url: String
}

@JsPlainObject
external interface WebPageJsonLD : JsonLD {
	val description: String
	val inLanguage: String
	val isPartOf: NodeRef
	val mainEntity: NodeRef?
	val name: String
	val url: String
}

@JsPlainObject
external interface BlogPostingJsonLD : JsonLD {
	val author: NodeRef
	val dateModified: String
	val datePublished: String
	val description: String
	val headline: String
	val image: String
	val inLanguage: String
	val keywords: Array<String>
	val mainEntityOfPage: NodeRef
	val publisher: NodeRef
	val url: String
}

@JsPlainObject
external interface SoftwareSourceCodeJsonLD : JsonLD {
	val author: PersonJsonLD
	val codeRepository: String
	val dateCreated: String?
	val dateModified: String?
	val description: String?
	val keywords: Array<String>?
	val name: String
	val programmingLanguage: String?
	val url: String
}

@JsPlainObject
external interface ListItemJsonLD : JsonLD {
	val item: String
	val name: String
	val position: Int
}

@JsPlainObject
external interface ItemListJsonLD : JsonLD {
	val itemListElement: Array<ListItemJsonLD>
	val numberOfItems: Int
}

@JsPlainObject
external interface BreadcrumbListJsonLD : JsonLD {
	val itemListElement: Array<ListItemJsonLD>
}
