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

// Animation delay
fun StyleScope.animationDelay(value: CSSSizeValue<out CSSUnitTime>) = property("animation-delay", value)
fun StyleScope.animationDelay(value: String) = property("animation-delay", value)

// Gradient border background helper
// Uses the two-layer background trick: a solid fill on padding-box + gradient on border-box.
// NOTE: only use this with an opaque fill color. A transparent fill will let the border-box
// gradient show through the whole element instead of just the border ring - for a "border only,
// see-through center" look, set the background manually with the literal `transparent` keyword instead.
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

// Shared full-page background gradient, used by every page's `id("main")` style.
fun StyleScope.pageBackground() {
	backgroundImage(linearGradient(180.deg) {
		add(Color("#0A0A0F"), (-3).percent)
		add(Color("#1A1225"), 14.percent)
		add(Color("#2A1B3D"), 65.percent)
		add(Color("#1E1535"), 90.percent)
	})
}
