package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.*
import io.github.ayfri.header.Header
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Main
import web.url.URL

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	Style(AnimationsStyle)
	Style(AppStyle)
	setTitle("$title - ${AppGlobals["author"]}'s Portfolio")

	Header()

	val context = rememberPageContext()
	val currentStub = context.route
	val url = URL(AppGlobals["url"] + currentStub.path)
	url.search = ""
	url.hash = ""
	url.pathname = url.pathname.ensureSuffix("/")

	setCanonical(url.toString())

	Main({
		id("main")
	}) {
		content()
	}

	Footer()

	window.scroll(0.0, 0.0)
}
