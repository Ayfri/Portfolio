package io.github.ayfri.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLImageElement

enum class FontAwesomeType(val value: String) {
	REGULAR("far"),
	SOLID("fas"),
	LIGHT("fal"),
	DOUBLE("fad"),
	BRAND("fab");
}

@Composable
fun I(type: FontAwesomeType = FontAwesomeType.SOLID, icon: String, vararg classes: String, style: StyleScope.() -> Unit = {}) {
	I({
		classes(type.value, "fa-$icon", *classes)
		style(style)
	})
}

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
