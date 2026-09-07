package io.github.ayfri

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLImageElement
import web.http.FetchPriority


fun AttrsScope<HTMLImageElement>.fetchPriority(priority: FetchPriority) {
	attr("fetchpriority", priority.toString())
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
