package io.github.ayfri.utils

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
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

// Vendor-prefixed background-clip
fun StyleScope.webkitBackgroundClip(value: BackgroundClip) = property("-webkit-background-clip", value)
fun StyleScope.mozBackgroundClip(value: BackgroundClip) = property("-moz-background-clip", value)

// Vendor-prefixed text-fill-color
fun StyleScope.webkitTextFillColor(value: CSSColorValue) = property("-webkit-text-fill-color", value)
fun StyleScope.mozTextFillColor(value: CSSColorValue) = property("-moz-text-fill-color", value)

// Animation delay
fun StyleScope.animationDelay(value: CSSSizeValue<out CSSUnitTime>) = property("animation-delay", value)
fun StyleScope.animationDelay(value: String) = property("animation-delay", value)

// Tab size
fun StyleScope.tabSize(value: Int) = property("tab-size", value)

// Image rendering
fun StyleScope.imageRendering(value: String) = property("image-rendering", value)

// Webkit line clamp (for text truncation)
fun StyleScope.webkitLineClamp(value: Int) = property("-webkit-line-clamp", value)
fun StyleScope.webkitBoxOrient(value: String) = property("-webkit-box-orient", value)
fun StyleScope.displayWebkitBox() = property("display", "-webkit-box")

// Gradient border background helper
// Uses the two-layer background trick: a solid/transparent fill on padding-box + gradient on border-box.
fun StyleScope.gradientBorderBackground(
	fillGradient: Gradient,
	borderGradient: Gradient = linearGradient(45.deg) {
		add(Color("#00D4FF"))
		add(Color("#FF0080"))
	},
) {
	background(Background.list(
		Background.of(
			image = BackgroundImage.of(borderGradient),
			origin = BackgroundOrigin.BorderBox,
			clip = BackgroundClip.BorderBox
		),
		Background.of(
			image = BackgroundImage.of(fillGradient),
			origin = BackgroundOrigin.PaddingBox,
			clip = BackgroundClip.PaddingBox
		)
	))
}

// Convenience overload for solid-color fill
fun StyleScope.gradientBorderBackground(
	fillColor: CSSColorValue,
	borderGradient: Gradient = linearGradient(45.deg) {
		add(Color("#00D4FF"))
		add(Color("#FF0080"))
	},
) {
	gradientBorderBackground(
		fillGradient = linearGradient {
			add(fillColor)
			add(fillColor)
		},
		borderGradient = borderGradient
	)
}
