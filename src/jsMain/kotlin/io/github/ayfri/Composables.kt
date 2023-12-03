package io.github.ayfri

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.HTMLTitleElement

enum class FontAwesomeType(val value: String) {
	REGULAR("far"),
	SOLID("fas"),
	LIGHT("fal"),
	DOUBLE("fad"),
	BRAND("fab");
}

@Composable
fun I(type: FontAwesomeType = FontAwesomeType.SOLID, icon: String, style: StyleScope.() -> Unit = {}) {
	I({
		classes(type.value, "fa-$icon")
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

@Composable
fun Img(attrs: AttrsScope<HTMLImageElement>.() -> Unit = {}) {
	TagElement<HTMLImageElement>(
		elementBuilder = ElementBuilder.createBuilder("img"),
		applyAttrs = {
			apply(attrs)
		},
		content = null
	)
}

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

@Composable
fun Title(text: String) {
	TagElement<HTMLTitleElement>(
		elementBuilder = ElementBuilder.createBuilder("title"),
		applyAttrs = {},
		content = {
			Text(text)
		}
	)
}

@Composable
fun Meta(attrs: AttrsScope<HTMLImageElement>.() -> Unit = {}) {
	TagElement<HTMLImageElement>(
		elementBuilder = ElementBuilder.createBuilder("meta"),
		applyAttrs = {
			apply(attrs)
		},
		content = null
	)
}

@Composable
fun MetaProperty(property: String, content: String) = Meta {
	attr("property", property)
	attr("content", content)
}

@Composable
fun MetaName(name: String, content: String) = Meta {
	attr("name", name)
	attr("content", content)
}

@Composable
fun Link(rel: String, href: String) = TagElement<HTMLImageElement>(
	elementBuilder = ElementBuilder.createBuilder("link"),
	applyAttrs = {
		attr("rel", rel)
		attr("href", href)
	},
	content = null
)

@Composable
fun Script(src: String? = null, attrs: AttrsScope<HTMLScriptElement>.() -> Unit = {}) = TagElement<HTMLScriptElement>(
	elementBuilder = ElementBuilder.createBuilder("script"),
	applyAttrs = {
		if (src != null) attr("src", src)
		apply(attrs)
	},
	content = null
)
