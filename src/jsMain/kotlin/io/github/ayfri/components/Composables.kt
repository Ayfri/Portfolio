package io.github.ayfri.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

@Composable
fun P(text: String, vararg classes: String = emptyArray()) {
	P({
		classes(*classes)
	}) {
		Text(text)
	}
}

@Composable
fun Span(text: String, vararg classes: String = emptyArray()) {
	Span({
		classes(*classes)
	}) {
		Text(text)
	}
}

@Composable
fun A(href: String, content: String = "", vararg classes: String = arrayOf("link")) {
	A(href, {
		classes(*classes)
	}) {
		Text(content)
	}
}

// Builder hoisted: `createBuilder` allocates, and this composable re-runs on every recomposition.
private val imgBuilder = ElementBuilder.createBuilder<HTMLImageElement>("img")

@Composable
fun Img(attrs: AttrsScope<HTMLImageElement>.() -> Unit = {}) =
	TagElement(imgBuilder, applyAttrs = attrs, content = null)

/** Description list tags, which Compose HTML doesn't ship. */
private val dlBuilder = ElementBuilder.createBuilder<HTMLElement>("dl")
private val dtBuilder = ElementBuilder.createBuilder<HTMLElement>("dt")
private val ddBuilder = ElementBuilder.createBuilder<HTMLElement>("dd")

@Composable
fun Dl(content: ContentBuilder<HTMLElement>) = TagElement(dlBuilder, null, content)

@Composable
fun Dt(content: ContentBuilder<HTMLElement>) = TagElement(dtBuilder, null, content)

@Composable
fun Dd(content: ContentBuilder<HTMLElement>) = TagElement(ddBuilder, null, content)

@Composable
fun CodeBlock(text: String, lang: String? = null) {
	Pre {
		Code(attrs = {
			classes(lang?.let { "language-$it" } ?: "nohighlight", "line-numbers")
		}) {
			Text(text)
		}
	}
}
