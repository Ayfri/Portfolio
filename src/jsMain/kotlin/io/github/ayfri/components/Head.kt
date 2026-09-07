package io.github.ayfri.components

import io.github.ayfri.jsonld.generateJsonLD
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

fun selectAll(selector: String) = document.querySelectorAll(selector).asList().unsafeCast<List<HTMLElement>>()

/**
 * Head tags are rewritten on every navigation. Doing it against the DOM directly rather than through
 * `renderComposable(document.head)` avoids leaking one whole composition per call.
 */
private fun addToHead(vararg elements: Element) = elements.forEach { document.head!!.appendChild(it) }

private fun element(tag: String, vararg attrs: Pair<String, String>) = document.createElement(tag).apply {
	attrs.forEach { (name, value) -> setAttribute(name, value) }
}

private fun metaProperty(property: String, content: String) = element("meta", "property" to property, "content" to content)

fun setTitle(title: String) {
	selectAll("meta[property*=title]").forEach(HTMLElement::remove)
	document.querySelector("title")?.remove()

	addToHead(
		element("title").apply { textContent = title },
		metaProperty("og:title", title),
		metaProperty("twitter:title", title),
	)
}

fun setDescription(description: String) {
	selectAll("meta[property*=description]").forEach(HTMLElement::remove)
	document.querySelector("meta[name=description]")?.remove()

	addToHead(
		element("meta", "name" to "description", "content" to description),
		metaProperty("og:description", description),
		metaProperty("twitter:description", description),
	)
}

fun setKeywords(keywords: String) {
	document.querySelector("meta[name=keywords]")?.remove()

	addToHead(element("meta", "name" to "keywords", "content" to keywords))
}

fun setCanonical(url: String) {
	selectAll("link[rel=canonical]").forEach(HTMLElement::remove)
	selectAll("meta[property*=url]").forEach(HTMLElement::remove)

	addToHead(
		element("link", "rel" to "canonical", "href" to url),
		metaProperty("og:url", url),
		metaProperty("twitter:url", url),
	)
}

/**
 * A `robots` meta tag is only honoured inside `<head>`, and it has to be cleared again on navigation, otherwise
 * the 404 page's `noindex` would follow the visitor onto every page after it.
 */
fun setRobots(value: String?) {
	document.querySelector("meta[name=robots]")?.remove()

	value?.let { addToHead(element("meta", "name" to "robots", "content" to it)) }
}

/** Clears every JSON-LD script, so pages adding their own [io.github.ayfri.jsonld.JsonLD] must call this first. */
fun setJsonLD() {
	selectAll("script[type*=ld]").forEach(HTMLElement::remove)

	addJsonLD(JSON.stringify(generateJsonLD(window.location.pathname)))
}

fun addJsonLD(json: String) = addToHead(
	element("script", "type" to "application/ld+json").apply { textContent = json }
)
