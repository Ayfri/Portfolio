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
