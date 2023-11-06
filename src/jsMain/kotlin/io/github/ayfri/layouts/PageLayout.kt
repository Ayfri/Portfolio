package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.Footer
import io.github.ayfri.Head
import io.github.ayfri.header.Header
import io.github.ayfri.setTitle
import io.github.ayfri.titlecase
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.renderComposable

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	LaunchedEffect(title) {
		val pathname = document.location?.pathname ?: "404"
		val currentPageName = pathname.removePrefix("/").replace("/", " ").titlecase()
		setTitle("${currentPageName.ifEmpty { "Home" }} - Pierre Roy")
	}

	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}

	Header()

	Main({
		id("main")
	}) {
		content()
	}

	Footer()

	window.scroll(0.0, 0.0)
}
