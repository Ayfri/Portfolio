package io.github.ayfri.utils

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import io.github.ayfri.AppStyle
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

/**
 * Fades and lifts the element in as it scrolls into view, a scroll-driven animation with no JS. Browsers without
 * `animation-timeline` run it as a zero-length animation and show the element right away.
 * The range is a fixed scroll distance, a percentage of `cover` would keep tall elements (READMEs) faded for screens.
 */
fun StyleScope.revealOnScroll() {
	property("animation", "${AppStyle.revealUp.name} linear both")
	property("animation-timeline", "view()")
	property("animation-range", "entry 0% entry 12rem")
}

/**
 * Skips layout and paint of off-screen items in long lists, [height] being the placeholder size until first render.
 * Not for anchor targets: a jump to an unrendered item lands on estimated positions.
 */
fun StyleScope.renderWhenVisible(height: CSSNumeric) {
	property("content-visibility", "auto")
	property("contain-intrinsic-size", "auto $height")
}

/** Hairline brighter on top than at the bottom, so cards read as lit from above rather than outlined. */
val cardEdgeGradient = linearGradient(180.deg) {
	add(Color("#D9CCFF2E"))
	add(Color("#D9CCFF0F"))
}

/** The logo's cyan -> violet -> orchid tube, for the few elements that should glow, never as a default. */
val neonEdgeGradient = linearGradient(135.deg) {
	add(Color(AppStyle.ACCENT_COLOR))
	add(Color(AppStyle.VIOLET_COLOR), 55.percent)
	add(Color(AppStyle.ACCENT_SECONDARY_COLOR))
}

/**
 * Two-layer background trick: a fill on padding-box over a gradient on border-box, drawn through a transparent border.
 * Only works with an opaque fill, a transparent one lets the border gradient show through the whole element.
 */
fun StyleScope.gradientBorderBackground(
	fillGradient: Gradient,
	borderGradient: Gradient = cardEdgeGradient,
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

fun StyleScope.gradientBorderBackground(
	fillColor: CSSColorValue,
	borderGradient: Gradient = cardEdgeGradient,
) {
	gradientBorderBackground(
		fillGradient = linearGradient {
			add(fillColor)
			add(fillColor)
		},
		borderGradient = borderGradient
	)
}
