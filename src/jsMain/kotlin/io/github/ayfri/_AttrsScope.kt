package io.github.ayfri

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLImageElement

enum class Priority {
	HIGH,
	LOW,
	AUTO;
}

fun AttrsScope<HTMLImageElement>.fetchPriority(priority: Priority) {
	attr("fetchpriority", priority.name.lowercase())
}

enum class Decoding {
	SYNC,
	ASYNC,
	AUTO;
}

fun AttrsScope<HTMLImageElement>.decoding(decoding: Decoding) {
	attr("decoding", decoding.name.lowercase())
}

enum class Loading {
	EAGER,
	LAZY,
	AUTO;
}

fun AttrsScope<HTMLImageElement>.loading(loading: Loading) {
	attr("loading", loading.name.lowercase())
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
