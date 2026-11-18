package io.github.ayfri.components

import androidx.compose.runtime.Composable
import io.github.ayfri.AppStyle
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
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
fun A(href: String, content: String = "", vararg classes: String) {
	A(href, {
		classes(*classes)
	}) {
		Text(content)
	}
}

/** Row of [AppStyle.tag]s, nothing when [tags] is empty. Tags past [max] collapse into a `+N` tag. */
@Composable
fun TagList(tags: List<String>, vararg classes: String, max: Int = tags.size) {
	if (tags.isEmpty()) return
	Div({ classes(AppStyle.tagList, *classes) }) {
		tags.take(max).forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
		if (tags.size > max) Span({ classes(AppStyle.tag) }) { Text("+${tags.size - max}") }
	}
}

/** AVIF avatar with a PNG fallback, the `picture` wrapper uses `display: contents` so the parent sizes the `img` directly. */
@Composable
fun Avatar(alt: String, attrs: AttrsScope<HTMLImageElement>.() -> Unit) {
	Picture({ style { display(DisplayStyle.Contents) } }) {
		Source({
			attr("srcset", "/images/avatar.avif")
			attr("type", "image/avif")
		})
		Img("/images/avatar.png", alt) {
			attr("height", "600")
			attr("width", "600")
			attrs()
		}
	}
}

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

private val dialogBuilder = ElementBuilder.createBuilder<HTMLElement>("dialog")

@Composable
fun Dialog(attrs: AttrsScope<HTMLElement>.() -> Unit, content: ContentBuilder<HTMLElement>) = TagElement(dialogBuilder, attrs, content)

@Composable
fun CodeBlock(text: String, lang: String) {
	Pre {
		Code(attrs = {
			classes("language-$lang", "line-numbers")
		}) {
			Text(text)
		}
	}
}
