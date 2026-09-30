package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.HeaderStyle
import io.github.ayfri.components.I
import io.github.ayfri.utils.size
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

/** Scroll-driven animations need no scroll listener, browsers lacking them get no bar rather than a full one. */
private val scrollTimelineSupported: Boolean = js("CSS.supports('animation-timeline: scroll()')")

@Composable
fun ReadingProgressBar() {
	if (!scrollTimelineSupported) return
	Style(ScrollStyle)
	Div({ classes(ScrollStyle.progress) })
}

@Composable
fun BackToTopButton() {
	Style(ScrollStyle)

	Button({
		classes(AppStyle.button, ScrollStyle.backToTop)
		attr("aria-label", "Back to top")
		onClick { window.scrollTo(0.0, 0.0) }
	}) {
		I(FontAwesomeType.SOLID, "arrow-up")
	}
}

object ScrollStyle : StyleSheet() {
	val grow by keyframes {
		from { transform { scaleX(0) } }
		to { transform { scaleX(1) } }
	}

	val reveal by keyframes {
		from {
			opacity(0)
			property("visibility", "hidden")
		}
		to {
			opacity(1)
			property("visibility", "visible")
		}
	}

	/** Sits on the header's bottom edge, so it never covers the navigation. */
	val progress by style {
		backgroundColor(Color(AppStyle.ACCENT_COLOR))
		height(2.px)
		left(0.px)
		position(Position.Fixed)
		property("top", "calc(${HeaderStyle.navbarHeight.value()} - 2px)")
		property("transform-origin", "left")
		width(100.percent)
		zIndex(101)
		property("animation", "${grow.name} linear both")
		property("animation-timeline", "scroll(root)")
	}

	/** Fades in over the first 300px of scroll. */
	val backToTop by style {
		bottom(1.5.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.px)
		position(Position.Fixed)
		right(1.5.cssRem)
		size(2.8.cssRem)
		zIndex(100)
		property("animation", "${reveal.name} linear both")
		property("animation-timeline", "scroll(root)")
		property("animation-range", "0 300px")
	}
}
