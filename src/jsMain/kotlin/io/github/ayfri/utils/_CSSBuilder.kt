package io.github.ayfri.utils

import io.github.ayfri.pages.HomeStyle.selector
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.selectors.CSSSelector
import org.jetbrains.compose.web.css.selectors.Nth

fun CSSBuilder.list(vararg selectors: String) = group(*(selectors.map { desc(self, it) }.toTypedArray()))
fun CSSBuilder.list(vararg selectors: CSSSelector) = group(*(selectors.map { desc(self, it) }.toTypedArray()))

fun StyleScope.size(height: CSSNumeric, width: CSSNumeric = height) {
	height(height)
	width(width)
}

fun StyleScope.size(height: CSSAutoKeyword, width: CSSAutoKeyword = height) {
	height(height)
	width(width)
}

inline val StyleScope.focusWithin get() = selector(":focus-within")
inline val StyleScope.marker get() = selector("::marker")

fun StyleScope.margin(value: CSSNumeric, auto: CSSAutoKeyword) = property("margin", "$value $auto")
fun StyleScope.margin(auto: CSSAutoKeyword, value: CSSNumeric) = property("margin", "$auto $value")
fun StyleScope.margin(value: CSSAutoKeyword) = property("margin", value)

val Int.n get() = Nth.Functional(this)
