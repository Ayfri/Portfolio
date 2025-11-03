package io.github.ayfri

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLImageElement
import web.html.ImageDecoding
import web.html.Loading
import web.http.FetchPriority


fun AttrsScope<HTMLImageElement>.fetchPriority(priority: FetchPriority) {
	attr("fetchpriority", priority.toString())
}

fun AttrsScope<HTMLImageElement>.decoding(decoding: ImageDecoding) {
	attr("decoding", decoding.toString())
}

fun AttrsScope<HTMLImageElement>.loading(loading: Loading) {
	attr("loading", loading.toString())
}

fun AttrsScope<HTMLImageElement>.srcset(vararg srcset: Pair<String, String>) {
	attr("srcset", srcset.joinToString(", ") { if (it.second.isNotBlank()) "${it.first} ${it.second}" else it.first })
}

fun AttrsScope<HTMLImageElement>.sizes(vararg sizes: String) {
	attr("sizes", sizes.joinToString(", "))
}

fun AttrsScope<HTMLImageElement>.alt(alt: String) {
	attr("alt", alt)
}

fun AttrsScope<HTMLImageElement>.height(height: Int) {
	attr("height", height.toString())
}

fun AttrsScope<HTMLImageElement>.width(width: Int) {
	attr("width", width.toString())
}
