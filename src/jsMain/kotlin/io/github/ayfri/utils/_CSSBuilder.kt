package io.github.ayfri.utils

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import io.github.ayfri.AppStyle
import org.jetbrains.compose.web.css.*

fun CSSBuilder.list(vararg selectors: String) = group(*(selectors.map { desc(self, it) }.toTypedArray()))

fun StyleScope.size(height: CSSNumeric, width: CSSNumeric = height) {
	height(height)
	width(width)
}

val SelectorsScope.marker get() = selector("::marker")

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
	add(Color(AppStyle.CARD_EDGE_TOP))
	add(Color(AppStyle.CARD_EDGE_BOTTOM))
}

/** The logo's cyan -> violet -> orchid tube, for the few elements that should glow, never as a default. */
val neonEdgeGradient = linearGradient(135.deg) {
	add(Color(AppStyle.ACCENT_COLOR))
	add(Color(AppStyle.VIOLET_COLOR), 55.percent)
	add(Color(AppStyle.ACCENT_SECONDARY_COLOR))
}

/**
 * Two-layer background trick: a [AppStyle.CARD_BACKGROUND] fill on padding-box over a gradient on border-box, drawn through a transparent border.
 * Only works with an opaque fill, a transparent one lets the border gradient show through the whole element.
 */
fun StyleScope.gradientBorderBackground(borderGradient: Gradient = cardEdgeGradient) {
	val fill = linearGradient {
		add(Color(AppStyle.CARD_BACKGROUND))
		add(Color(AppStyle.CARD_BACKGROUND))
	}

	background(Background.list(
		Background.of(
			image = BackgroundImage.of(borderGradient),
			origin = BackgroundOrigin.BorderBox,
			clip = BackgroundClip.BorderBox
		),
		Background.of(
			image = BackgroundImage.of(fill),
			origin = BackgroundOrigin.PaddingBox,
			clip = BackgroundClip.PaddingBox
		)
	))
}
