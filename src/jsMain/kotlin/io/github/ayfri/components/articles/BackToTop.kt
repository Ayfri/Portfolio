package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.utils.zIndex
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.w3c.dom.events.EventListener

@Composable
fun BackToTopButton() {
	Style(BackToTopStyle)

	val isVisible = remember { mutableStateOf(false) }

	DisposableEffect(Unit) {
		val scrollListener = EventListener {
			isVisible.value = window.scrollY > 300
		}

		window.addEventListener("scroll", scrollListener)

		onDispose {
			window.removeEventListener("scroll", scrollListener)
		}
	}

	Button({
		classes(BackToTopStyle.button)
		if (isVisible.value) {
			classes(BackToTopStyle.visible)
		}
		onClick {
			window.scrollTo(0.0, 0.0)
		}
	}) {
		I({
			classes("fas", "fa-arrow-up")
		})
	}
}

object BackToTopStyle : StyleSheet() {
	const val BUTTON_COLOR = "#FFFFFF1A"
	const val BUTTON_HOVER_COLOR = "#FFFFFF40"
	const val BUTTON_SHADOW_COLOR = "#00000040"

	@OptIn(ExperimentalComposeWebApi::class)
	val button by style {
		position(Position.Companion.Fixed)
		bottom(30.px)
		right(30.px)
		width(50.px)
		height(50.px)
		borderRadius(50.percent)
		backgroundColor(Color(BUTTON_COLOR))
		color(Color.white)
		border(0.px)
		cursor(Cursor.Companion.Pointer)
		boxShadow(0.px, 2.px, 10.px, 0.px, Color(BUTTON_SHADOW_COLOR))
		opacity(0)
		pointerEvents(PointerEvents.Companion.None)
		zIndex(100)

		transitions {
			"opacity" {
				duration(0.3.s)
			}
			"transform" {
				duration(0.3.s)
			}
			"background-color" {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color(BUTTON_HOVER_COLOR))
			transform {
				scale(1.1)
			}
		}

		self + active style {
			transform {
				scale(0.95)
			}
		}
	}

	val visible by style {
		opacity(1)
		pointerEvents(PointerEvents.Companion.Auto)
	}
}
