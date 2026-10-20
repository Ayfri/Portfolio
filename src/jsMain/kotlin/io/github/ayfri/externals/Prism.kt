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
	}.then { extendGrammars() }
}

/** Prism misses most classes and calls: PascalCase is a class except after a dot (`Machin.Truc` is an entry), plus Kotlin `buildString {` and `{ k -> }`. */
private fun extendGrammars(): Unit = js("""
	['javascript', 'typescript', 'kotlin'].forEach(function (lang) {
		Prism.languages.insertBefore(lang, 'operator', { 'type-name': { pattern: /(^|[^.$\w])[A-Z](?=[$\w]*[a-z])[$\w]*/, lookbehind: true, alias: 'class-name' } });
	});
	Prism.languages.insertBefore('kotlin', 'operator', {
		'lambda-call': { pattern: /\b[a-z_]\w*(?=\s*\{)/, alias: 'function' },
		'lambda-parameter': { pattern: /(\{\s*)[\w\s,()]+?(?=\s*->)/, lookbehind: true, alias: 'parameter', inside: { punctuation: /[(),]/ } }
	});
""")

@Composable
fun HighlightCode() = LaunchedEffect(Unit) {
	prism.then { Prism.highlightAll() }
}
