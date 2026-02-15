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

@Composable
fun HighlightCode() = DisposableEffect(Unit) {
	var highlighted = false
	var interval: Interval? = null
	interval = setInterval( {
		if (!highlighted && js("'Prism' in window")) {
			Prism.highlightAll()
			highlighted = true
		}
		console.log("Highlighting code")
		clearInterval(interval)
	}, 200)

	onDispose {
		clearInterval(interval)
	}
}
