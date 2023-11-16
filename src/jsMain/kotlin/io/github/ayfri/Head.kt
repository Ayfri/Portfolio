package io.github.ayfri

import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

fun setTitle(title: String) = renderComposable(document.head!!) {
	document.querySelector("title")?.remove()
	Title(title)
	MetaProperty("og:title", title)
	MetaProperty("twitter:title", title)
}

fun setDescription(description: String) = renderComposable(document.head!!) {
	document.querySelectorAll("meta[property*=description]").asList().forEach {
		val asHTMLElement = it.unsafeCast<HTMLElement>()
		asHTMLElement.remove()
	}
	document.querySelector("meta[name=description]")?.remove()

	MetaName("description", description)
	MetaProperty("og:description", description)
	MetaProperty("twitter:description", description)
}
