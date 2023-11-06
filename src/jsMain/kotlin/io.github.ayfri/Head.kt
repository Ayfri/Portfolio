package io.github.ayfri

import androidx.compose.runtime.Composable
import io.github.ayfri.style.AppStyle
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement

@Composable
fun Head() {
	Style(AppStyle)
	Link(
		href = "https://fonts.googleapis.com/css2?family=Open+Sans:wght@100;300;400;500;700&display=swap",
		rel = "stylesheet"
	)
	Link(href = "https://dev-cats.github.io/code-snippets/JetBrainsMono.css", rel = "stylesheet")

	TagElement<HTMLScriptElement>("link", {
		attr("rel", "preload")
		attr("fetchpriority", "high")
		attr("as", "image")
		attr("href", localImage("avatar.webp"))
		attr("type", "image/webp")
	}) {}

	Script(src = "https://kit.fontawesome.com/74fed0e2b5.js", CrossOrigin.ANONYMOUS, ScriptMode.DEFER)
	Script(src = "https://www.googletagmanager.com/gtag/js?id=G-TS3BHPVFKK", mode = ScriptMode.ASYNC)
	Script(
		content = "function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag('js',new Date),gtag('config','G-TS3BHPVFKK')"
	)
}

@Composable
fun Link(href: String, rel: String = "", type: String = "") {
	TagElement<HTMLLinkElement>("link", {
		attr("href", href)
		if (rel.isNotEmpty()) attr("rel", rel)
		if (type.isNotEmpty()) attr("type", type)
	}) {}
}

enum class ScriptMode(val value: String) {
	DEFAULT(""),
	ASYNC("async"),
	DEFER("defer")
}

enum class CrossOrigin(val value: String) {
	NONE(""),
	ANONYMOUS("anonymous"),
	USE_CREDENTIALS("use-credentials")
}

@Composable
fun Script(src: String, crossOrigin: CrossOrigin = CrossOrigin.NONE, mode: ScriptMode = ScriptMode.DEFAULT) {
	TagElement<HTMLScriptElement>("script", {
		attr("src", src)
		if (crossOrigin.value.isNotEmpty()) attr("crossorigin", crossOrigin.value)
		if (mode.value.isNotEmpty())
			when (mode) {
				ScriptMode.ASYNC -> attr("async", "")
				ScriptMode.DEFER -> attr("defer", "")
				else -> Unit
			}
	}) {}
}

@Composable
fun Script(content: String) {
	TagElement<HTMLScriptElement>("script", null) {
		Text(content)
	}
}

fun setTitle(title: String) {
	document.querySelector("title")?.textContent = title
}
