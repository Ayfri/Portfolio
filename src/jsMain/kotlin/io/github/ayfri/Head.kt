package io.github.ayfri

import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

fun setTitle(title: String) = renderComposable(document.head!!) {
	document.querySelector("title")?.remove()
	Title(title)
	Meta("og:title", title)
	Meta("twitter:title", title)
}

fun setDescription(description: String) = renderComposable(document.head!!) {
	document.querySelectorAll("meta[property*=description]").asList().forEach {
		val asHTMLElement = it.unsafeCast<HTMLElement>()
		asHTMLElement.remove()
	}
	document.querySelector("meta[name=description]")?.remove()

	Meta("description", description)
	Meta("og:description", description)
	Meta("twitter:description", description)
}
