package io.github.ayfri.components

import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.jsonld.AVATAR_URL
import io.github.ayfri.jsonld.GraphJsonLD
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.asList

private fun element(tag: String, vararg attrs: Pair<String, String>) = document.createElement(tag).apply {
	setAttribute("data-page-head", "")
	attrs.forEach { (name, value) -> setAttribute(name, value) }
}

private fun metaName(name: String, content: String) = element("meta", "name" to name, "content" to content)

private fun metaProperty(property: String, content: String) = element("meta", "property" to property, "content" to content)

/** Written straight to the DOM (`renderComposable(document.head)` leaks a composition per call), tagged `data-page-head` for the next page to remove. */
fun setPageHead(
	title: String,
	description: String,
	canonical: String?,
	robots: String = "max-image-preview:large",
	markdownAlternate: String? = null,
	article: ArticleEntry? = null,
	image: String = AVATAR_URL,
	imageAlt: String = "Portrait of Pierre Roy",
	jsonLD: GraphJsonLD? = null,
) {
	val head = document.head!!
	head.querySelectorAll("[data-page-head], title").asList().unsafeCast<List<Element>>().forEach(Element::remove)

	val tags = buildList {
		add(element("title").apply { textContent = title })
		add(metaName("description", description))
		add(metaProperty("og:title", title))
		add(metaProperty("og:description", description))
		add(metaName("twitter:title", title))
		add(metaName("twitter:description", description))
		add(metaProperty("og:type", if (article != null) "article" else "website"))
		add(metaProperty("og:image", image))
		add(metaProperty("og:image:alt", imageAlt))
		add(metaName("twitter:card", if (image == AVATAR_URL) "summary" else "summary_large_image"))
		add(metaName("twitter:image", image))
		add(metaName("twitter:image:alt", imageAlt))

		if (canonical != null) {
			add(element("link", "rel" to "canonical", "href" to canonical))
			add(metaProperty("og:url", canonical))
		}

		add(metaName("robots", robots))
		if (markdownAlternate != null) add(element("link", "rel" to "alternate", "type" to "text/markdown", "href" to markdownAlternate))

		if (article != null) {
			add(metaProperty("article:published_time", article.date))
			add(metaProperty("article:modified_time", article.dateModified))
		}

		if (jsonLD != null) add(element("script", "type" to "application/ld+json").apply { textContent = JSON.stringify(jsonLD) })
	}

	tags.forEach(head::appendChild)
}
