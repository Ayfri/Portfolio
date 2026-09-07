package io.github.ayfri.externals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.w3c.dom.ParentNode
import web.timers.Interval
import web.timers.clearInterval
import web.timers.setInterval

external object Prism {
	fun highlightAll()
	fun highlightAllUnder(container: ParentNode)
}

/** `prism.min.js` is loaded `async`, so poll for it instead of assuming it is there on the first tick. */
@Composable
fun HighlightCode() = DisposableEffect(Unit) {
	var attemptsLeft = 25
	var interval: Interval? = null

	interval = setInterval({
		val ready = js("'Prism' in window") as Boolean
		if (ready) Prism.highlightAll()
		if (ready || --attemptsLeft <= 0) clearInterval(interval)
	}, 200)

	onDispose {
		clearInterval(interval)
	}
}
