package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.github.ayfri.utils.linearGradient
import io.github.ayfri.utils.zIndex
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.events.EventListener
import kotlin.math.max
import kotlin.math.min

@Composable
fun ReadingProgressBar() {
	Style(ProgressBarStyle)

	val progress = remember { mutableStateOf(0) }

	DisposableEffect(Unit) {
		val scrollListener = EventListener {
			val totalHeight = max(
				document.documentElement?.scrollHeight ?: 0,
				document.body?.scrollHeight ?: 0
			) - window.innerHeight

			val currentScroll = window.scrollY
			val calculatedProgress = if (totalHeight > 0) {
				min(100, ((currentScroll / totalHeight) * 100).toInt())
			} else 0

			progress.value = calculatedProgress
		}

		window.addEventListener("scroll", scrollListener)

		onDispose {
			window.removeEventListener("scroll", scrollListener)
		}
	}

	Div({
		classes(ProgressBarStyle.container)
	}) {
		Div({
			classes(ProgressBarStyle.progress)
			style {
				width(progress.value.percent)
			}
		})
	}
}

object ProgressBarStyle : StyleSheet() {
	const val PROGRESS_BAR_BACKGROUND_COLOR = "#FFFFFF10"
	const val PROGRESS_GRADIENT_START_COLOR = "#9C7CF4"
	const val PROGRESS_GRADIENT_END_COLOR = "#6EBAE7"

	val container by style {
		position(Position.Companion.Fixed)
		top(0.px)
		left(0.px)
		width(100.percent)
		height(4.px)
		backgroundColor(Color(PROGRESS_BAR_BACKGROUND_COLOR))
		zIndex(1000)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val progress by style {
		height(100.percent)
		background(linearGradient(90.deg) {
			stop(Color(PROGRESS_GRADIENT_START_COLOR))
			stop(Color(PROGRESS_GRADIENT_END_COLOR))
		})
		width(0.percent)

		transitions {
			properties("width") {
				duration(0.2.s)
				timingFunction(AnimationTimingFunction.Companion.EaseOut)
			}
		}
	}
}
