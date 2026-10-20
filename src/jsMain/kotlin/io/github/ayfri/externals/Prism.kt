package io.github.ayfri.externals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import kotlin.js.Promise

external object Prism {
	fun highlightAll()
}

/** Loads Prism and its CSS once, on the first page with code. The CSS goes first in `<head>` so `CodeTheme` overrides the stock theme. */
private val prism by lazy {
	Promise<Unit> { resolve, _ ->
		if (js("'Prism' in window") as Boolean) return@Promise resolve(Unit)
		val head = document.head!!
		document.querySelector("link[href='/prism.min.css']") ?: head.prepend(document.createElement("link").apply {
			setAttribute("rel", "stylesheet")
			setAttribute("href", "/prism.min.css")
		})
		val script = document.querySelector("script[src='/prism.min.js']") ?: document.createElement("script").apply {
			setAttribute("async", "")
			setAttribute("data-manual", "")
			setAttribute("src", "/prism.min.js")
			head.append(this)
		}
		script.addEventListener("load", { resolve(Unit) })
	}.then { tagTypeNames() }
}

/** Prism only sees a JS/TS class after `class`, `new` or `extends`: colour every PascalCase name (`Uint8Array`) as one, ALL_CAPS stays a constant. */
private fun tagTypeNames(): Unit = js("""
	['javascript', 'typescript'].forEach(function (lang) {
		Prism.languages.insertBefore(lang, 'operator', { 'type-name': { pattern: /\b[A-Z](?=[$\w]*[a-z])[$\w]*/, alias: 'class-name' } });
	})
""")

@Composable
fun HighlightCode() = LaunchedEffect(Unit) {
	prism.then { Prism.highlightAll() }
}
