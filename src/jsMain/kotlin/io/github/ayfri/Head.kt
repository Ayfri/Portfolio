package io.github.ayfri

import io.github.ayfri.jsonld.generateJsonLD
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

fun selectAll(selector: String) = document.querySelectorAll(selector).asList().unsafeCast<List<HTMLElement>>()

fun setTitle(title: String) = renderComposable(document.head!!) {
	selectAll("meta[property*=title]").forEach(HTMLElement::remove)
	document.querySelector("title")?.remove()

	Title(title)
	MetaProperty("og:title", title)
	MetaProperty("twitter:title", title)
}

fun setDescription(description: String) = renderComposable(document.head!!) {
	selectAll("meta[property*=description]").forEach(HTMLElement::remove)
	document.querySelector("meta[name=description]")?.remove()

	MetaName("description", description)
	MetaProperty("og:description", description)
	MetaProperty("twitter:description", description)
}

fun setCanonical(url: String) = renderComposable(document.head!!) {
	selectAll("link[rel=canonical]").forEach(HTMLElement::remove)
	Link("canonical", url)

	selectAll("meta[property*=url]").forEach(HTMLElement::remove)
	MetaProperty("og:url", url)
	MetaProperty("twitter:url", url)
}

fun setJsonLD() = renderComposable(document.head!!) {
	selectAll("script[type*=ld]").forEach(HTMLElement::remove)

	Script {
		attr("type", "application/ld+json")
		ref {
			it.innerHTML = jsonEncoder.encodeToString(generateJsonLD(window.location.pathname))

			onDispose {}
		}
	}
}
