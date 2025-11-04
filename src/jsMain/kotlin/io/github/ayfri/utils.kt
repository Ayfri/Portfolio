package io.github.ayfri

import io.github.ayfri.externals.parse
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLParagraphElement
import kotlin.math.max
import kotlin.math.roundToInt

fun localImage(path: String) = "/images/$path"

fun AttrsScope<HTMLParagraphElement>.markdownParagraph(
	text: String,
	breaks: Boolean = false,
	vararg classes: String,
	additionalRefCallback: AttrsScope<HTMLParagraphElement>.(HTMLParagraphElement) -> Unit = {},
) {
	ref {
		if (classes.isNotEmpty()) it.classList.add(*classes)

		val textToParse = if (breaks) text.replace("\n", "<br>") else text
		it.innerHTML = parse(textToParse)
		additionalRefCallback(it)
		onDispose {}
	}
}

inline fun <C : CharSequence, R : C> C.ifNotBlank(block: (C) -> R) = if (isNotBlank()) block(this) else ""

// Calculate estimated reading time based on content
fun calculateReadingTime(content: String): Int {
	// Average reading speed: 250 words per minute
	val wordCount = content.split(Regex("\\s+")).size
	val readingTimeMinutes = (wordCount / 250.0).roundToInt()
	return max(1, readingTimeMinutes) // Minimum 1 minute
}
